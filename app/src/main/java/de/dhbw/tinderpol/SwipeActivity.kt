package de.dhbw.tinderpol

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.google.android.material.R.drawable.*
import de.dhbw.tinderpol.databinding.ActivityNoticeBinding
import de.dhbw.tinderpol.util.OnSwipeTouchListener


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
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                val notice = SDO.getNextNotice()
                val nameText = "${notice.firstName} ${notice.lastName} (${notice.sex})"
                binding.textViewFullName.text = nameText
                updateShownImg()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                showReportConfirmDialog()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                showBottomSheetDialog()
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                finish()
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
        binding.noticeImage.load(SDO.getImageURL()){
            placeholder(android.R.drawable.stat_sys_download)
            error(mtrl_ic_error)
        }
    }
}