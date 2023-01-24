package de.dhbw.tinderpol

import android.annotation.SuppressLint
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.google.android.material.R.drawable.*
import de.dhbw.tinderpol.databinding.ActivityNoticeBinding
import de.dhbw.tinderpol.util.OnSwipeTouchListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.abs


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

            private fun clamp(x: Float, min: Float = -1F, max: Float = 1F): Float {
                return if (x > max) max
                else if (x < min) min
                else x
            }

            override fun onMove(xDiff: Float) {
                super.onMove(xDiff)
                val deg = clamp(2 * xDiff / resources.displayMetrics.widthPixels) * 30
                val prevX = binding.noticeImage.x
                binding.noticeImage.animate().rotation(deg).setDuration(0).start()
                binding.noticeImage.animate().rotation(deg).x(prevX).start()
            }

            override fun onMoveDone(event: MotionEvent) {
                super.onMoveDone(event)
                binding.noticeImage.animate().rotation(0F).setDuration(150).start()
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
}