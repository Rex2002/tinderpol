package de.dhbw.tinderpol

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import de.dhbw.tinderpol.databinding.ActivityNoticeBinding
import de.dhbw.tinderpol.util.OnSwipeTouchListener
import de.dhbw.tinderpol.util.Util
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs


class SwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBinding

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("swipeActivity", "creating instance")
        super.onCreate(savedInstanceState)
        val middle = resources.displayMetrics.widthPixels.toFloat() / 2
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonPrev.setOnClickListener {
            Log.i("swipeActivity", "button prev clicked")
            try {
                val sharedPref = getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
                if(SDO.offlineFlag && sharedPref.getInt(getString(R.string.offlineAnchor), 0) - 20 == SDO.currentNoticeIndex){
                    Util.errorView(this@SwipeActivity, "Reached last cached image in this direction. Please go online to load more images.", "Cache exceeded")
                }
                SDO.getPrevNotice()
                updateShownImg()
            } catch (e: Exception) {
                Util.errorView(this@SwipeActivity, e.message ?: "unknown error", "First notice reached") }
        }

        binding.imageButtonNext.setOnClickListener {
            Log.i("swipeActivity", "button next clicked")
            nextNotice()
            updateShownImg()
        }

        binding.root.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipedLeft() {
                super.onSwipedLeft()
                Log.i("swipeActivity", "swipe left registered")
                nextNotice()
            }

            override fun onSwipedRight() {
                super.onSwipedRight()
                Log.i("swipeActivity", "swipe right registered")
                showReportConfirmDialog()
            }

            override fun onSwipedUp() {
                super.onSwipedUp()
                Log.i("swipeActivity", "swipe up registered")
                showBottomSheetDialog()
            }

            override fun onSwipedDown() {
                super.onSwipedDown()
                Log.i("swipeActivity", "swipe down registered")
                finish()
            }

            override fun onSwipingSideways(xDiff: Float, posDiffToLast: Pair<Float, Float>) {
                super.onSwipingSideways(xDiff, posDiffToLast)
                val duration = (abs(posDiffToLast.first) / 100).toLong()
                binding.noticeImage.animate().x(xDiff).setDuration(duration).start()
            }

            override fun onMoveDone(event: MotionEvent) {
                super.onMoveDone(event)
                val imageStart = middle - (binding.noticeImage.width / 2)
                binding.noticeImage.animate().x(imageStart).setDuration(150).start()
            }

            override fun onClick(pos: Pair<Float, Float>) {
                super.onClick(pos)
                if (pos.first <= middle) updateShownImg(SDO.getPrevImage(applicationContext))
                else updateShownImg(SDO.getNextImage(applicationContext))
            }
        })

        binding.tabDots.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                SDO.currentImgIndex = tab?.position ?: 0
                updateShownImg(null, false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        if(SDO.noticesIsEmpty()){
            Util.errorView(this, "The local notices database is empty. Go online to sync", this::finish, "Empty database")
        }
        else {
            updateShownImg()
        }

        SDO.listenToUpdates {
            runOnUiThread {
                updateShownImg()
            }
        }
    }

    fun showBottomSheetDialog(){
        val bottomSheetDialog = NoticeInfoFragment()
        supportFragmentManager.beginTransaction().add(bottomSheetDialog, "").commit()
    }

    fun showReportConfirmDialog(){
        val confirmReportDialog = ContactInterpolConfirmFragment()
        supportFragmentManager.beginTransaction().add(confirmReportDialog,"").commit()
    }

    private fun updateDots(imgAmount: Int, currentImgIndex: Int) {
        val tabCount = binding.tabDots.tabCount
        // Remove unnecessary tabs:
        for (i in 0 until tabCount - imgAmount) {
            binding.tabDots.removeTabAt(tabCount - i - 1)
        }
        // Add necessary tabs:
        for (i in tabCount until imgAmount) {
            binding.tabDots.addTab(binding.tabDots.newTab())
        }
        // Select current tab:
        binding.tabDots.selectTab(binding.tabDots.getTabAt(currentImgIndex))
    }

    fun nextNotice(){
        try{
            val sharedPref = getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            val offlineAnchor = sharedPref.getInt(getString(R.string.offlineAnchor), 0)
            Log.i("swipeActivity", "offlineAnchor: $offlineAnchor")
            if(SDO.offlineFlag && sharedPref.getInt(getString(R.string.offlineAnchor), 0) + 49 == SDO.currentNoticeIndex){
                Util.errorView(this@SwipeActivity, "Reached last cached image in this direction. Please go online to load more images.", "Cache exceeded")
            }
            SDO.getNextNotice()
            updateShownImg()
        }catch (e: Exception){
            Util.errorView(this@SwipeActivity, e.message ?: "unknown error", "Last notice reached")
        }
    }

    fun updateShownImg(imgURL: Any? = null, toUpdateDots: Boolean = true) {
        val notice = SDO.getCurrentNotice()
        val nameText = "${notice.firstName} ${notice.lastName} (${notice.sex})"
        if (toUpdateDots) updateDots(notice.imgs?.size ?: 0, SDO.currentImgIndex)
        binding.textViewFullName.text = nameText
        binding.noticeImage.load(imgURL ?: SDO.getImage(applicationContext)){
            placeholder(android.R.drawable.stat_sys_download)
            error(
                Drawable.createFromPath(
                    File(getDir(
                        "images", Context.MODE_PRIVATE),
                        SDO.EMPTY_NOTICE_ID + "_0"
                    ).absolutePath
                )
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStop() {
        Log.i("swipeActivity", "killing swipeActivity")
        super.onStop()
        GlobalScope.launch {
            SDO.persistStatus(applicationContext)
        }
    }
}