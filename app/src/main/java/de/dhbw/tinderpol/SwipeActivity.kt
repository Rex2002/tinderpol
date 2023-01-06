package de.dhbw.tinderpol

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
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
        updateShownImg()


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
                SDO.getNextNotice()
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
        val nameText = "${SDO.getCurrentNotice().firstName.toString()} ${SDO.getCurrentNotice().lastName} (${SDO.getCurrentNotice().sex.toString()})"
        binding.textViewFullName.text = nameText
        binding.noticeImage.load(SDO.getCurrentImageURL()){
            placeholder(android.R.drawable.stat_sys_download)
            error(mtrl_ic_error)
        }
    }
}