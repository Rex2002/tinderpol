package de.dhbw.tinderpol.view

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import coil.load
import com.google.android.material.R.drawable.*
import de.dhbw.tinderpol.ContactInterpolConfirmFragment
import de.dhbw.tinderpol.NoticeInfoFragment
import de.dhbw.tinderpol.R.drawable.ic_launcher_foreground
import de.dhbw.tinderpol.SDO
import de.dhbw.tinderpol.databinding.ActivityNoticeBinding
import de.dhbw.tinderpol.util.OnSwipeTouchListener



class SwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBinding
    private lateinit var image: ImageView

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.noticeImage.load("https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063552") {
            placeholder(ic_launcher_foreground)
            error(mtrl_ic_error)
        }

        binding.root.setOnTouchListener(object : OnSwipeTouchListener(this){
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                binding.noticeImage.load(SDO.getNextImageURL()){
                    placeholder(
                        R.drawable.stat_sys_download)
                    error(mtrl_ic_error)
                }
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
}