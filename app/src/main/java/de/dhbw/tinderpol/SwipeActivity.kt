package de.dhbw.tinderpol

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import coil.load
import com.google.android.material.R.drawable.*
import de.dhbw.tinderpol.databinding.ActivityNoticeBinding
import de.dhbw.tinderpol.util.OnSwipeTouchListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.abs


class SwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBinding
    
    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonPrev.setOnClickListener{
            SDO.getPrevNotice()
            updateShownImg()
        }

        binding.imageButtonNext.setOnClickListener{
            SDO.getNextNotice()
            updateShownImg()
        }

        binding.root.setOnTouchListener(object : OnSwipeTouchListener(this){
            override fun onSwipedLeft() {
                super.onSwipedLeft()
                SDO.getNextNotice()
                updateShownImg()
            }

            override fun onSwipedRight() {
                super.onSwipedRight()
                showReportConfirmDialog()
            }

            override fun onSwipedUp() {
                super.onSwipedUp()
                showBottomSheetDialog()
            }

            override fun onSwipedDown() {
                super.onSwipedDown()
                finish()
            }

            override fun onSwipingSideways(xDiff: Float, posDiffToLast: Pair<Float, Float>) {
                super.onSwipingSideways(xDiff, posDiffToLast)
                val duration = (abs(posDiffToLast.first) / 100).toLong()
                binding.noticeImage.animate().x(xDiff).setDuration(duration).start()
            }

            override fun onMoveDone(event: MotionEvent) {
                super.onMoveDone(event)
                val imageStart = (resources.displayMetrics.widthPixels.toFloat() - binding.noticeImage.width) / 2
                binding.noticeImage.animate().x(imageStart).setDuration(150).start()
            }
        })

        updateShownImg()

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

    fun updateShownImg(){
        val notice = SDO.getCurrentNotice()
        val nameText = "${notice.firstName} ${notice.lastName} (${notice.sex})"
        binding.textViewFullName.text = nameText
        binding.noticeImage.load(SDO.getImageURL()){
            placeholder(android.R.drawable.stat_sys_download)
            error(mtrl_ic_error)
        }
    }

    override fun onStop() {
        super.onStop()
        GlobalScope.launch {
            SDO.persistStatus(getSharedPreferences(
                getString(R.string.shared_preferences_file),
                Context.MODE_PRIVATE
            ))
        }
    }
}