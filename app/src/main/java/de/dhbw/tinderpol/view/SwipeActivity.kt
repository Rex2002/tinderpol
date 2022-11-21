package de.dhbw.tinderpol.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import coil.load
import com.google.android.material.R.drawable.mtrl_ic_error
import de.dhbw.tinderpol.R
import de.dhbw.tinderpol.R.drawable.ic_launcher_foreground
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

        image = findViewById(R.id.noticeImage)
        image.load("https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063552") {
            placeholder(ic_launcher_foreground)
            error(mtrl_ic_error)
        }

        binding.root.setOnTouchListener(object : OnSwipeTouchListener(this){
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Toast.makeText(this@SwipeActivity, "swipe left, load new picture", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                Toast.makeText(this@SwipeActivity, "swipe right, contact Interpol", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                Toast.makeText(this@SwipeActivity, "swipe up, take a look at the bio", Toast.LENGTH_SHORT).show()
            }
        })


    }
}