package de.dhbw.tinderpol

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import de.dhbw.tinderpol.view.SwipeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java);
            startActivity(intentToStartShit);
        }
    }
}