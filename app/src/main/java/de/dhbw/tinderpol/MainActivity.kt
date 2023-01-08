package de.dhbw.tinderpol

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import de.dhbw.tinderpol.data.NoticeDatabase
import de.dhbw.tinderpol.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewExplainText1.setOnClickListener{
            SDO.syncNotices()
        }

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            println("hello world")
            showReportConfirmDialog()
            Toast.makeText(this, "settingsbutton called", Toast.LENGTH_SHORT).show()
        }

        //db-stuff probably goes somewhere else in future revisions
        val db = Room.databaseBuilder(
            applicationContext, NoticeDatabase::class.java, "db-tinderPol"
        ).build()

        val noticeDao = db.noticeDao()

        /*noticeDao.insertAll(
            Notice(
                "007", "Kruse", "Eckhardt", "24.12.0000",
                listOf("German"), null, SexID.M, "Germany", "Bethlehem",
                listOf(Charge("UN", "Sharpness")), 70, 176
            )
        )*/
    }

    private fun showReportConfirmDialog(){
        val settingsFragment = SettingsFragment()
        supportFragmentManager.beginTransaction().replace(android.R.id.content,settingsFragment).addToBackStack("settings").commit()
    }
}