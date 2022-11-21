package de.dhbw.tinderpol.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.dhbw.tinderpol.databinding.ActivitySwipeBinding
import de.dhbw.tinderpol.util.OnSwipeTouchListener


class SwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwipeBinding.inflate(layoutInflater)

        setContentView(binding.root);

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