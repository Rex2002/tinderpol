package de.dhbw.tinderpol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.dhbw.tinderpol.databinding.ActivitySwipeBinding


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
        })


    }
}