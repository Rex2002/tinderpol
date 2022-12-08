package de.dhbw.tinderpol

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewExplainText1.setOnClickListener{
            GlobalScope.launch { SDO.syncNotices() }
        }

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            println("hello world")
            showReportConfirmDialog()
            Toast.makeText(this, "settingsbutton calld", Toast.LENGTH_SHORT).show()
        }
    }

    fun showReportConfirmDialog(){
        val confirmReportDialog = ContactInterpolConfirmFragment()
        supportFragmentManager.beginTransaction().add(confirmReportDialog,"").commit()
    }
}