package de.dhbw.tinderpol

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import de.dhbw.tinderpol.data.LocalNoticesDataSource
import de.dhbw.tinderpol.data.room.NoticeDatabase
import androidx.annotation.RequiresApi
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import de.dhbw.tinderpol.util.StarredNoticesListItemAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {
            SDO.syncNotices()
        }

        val db = Room.databaseBuilder(
            applicationContext, NoticeDatabase::class.java, "db-tinderPol"
        ).fallbackToDestructiveMigration().build()
        LocalNoticesDataSource.dao = db.noticeDao()

        binding.textViewExplainText1.setOnClickListener{
            GlobalScope.launch {
                SDO.syncNotices()
            }
        }

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            showReportConfirmDialog()
        }

        val starredNotices = SDO.starredNotices
        val recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = StarredNoticesListItemAdapter(this, starredNotices)
        recyclerView.setHasFixedSize(true)


        /*noticeDao.insertNotices(
            Notice(
                "007", "red", "Kruse", "Eckhardt", "24.12.0000",
                listOf("German"), null, SexID.M, "Germany", "Bethlehem",
                listOf(Charge("UN", "Sharpness")), listOf("German", "English", "Klingon"),
                70.0, 1.76
            )
        )*/
    }

    private fun showReportConfirmDialog(){
        val settingsFragment = BottomSettingsFragment()
        supportFragmentManager.beginTransaction().add(settingsFragment, "").commit()
    }
}