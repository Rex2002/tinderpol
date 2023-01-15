package de.dhbw.tinderpol

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import de.dhbw.tinderpol.data.room.NoticeDatabase
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import de.dhbw.tinderpol.util.StarredNoticesListItemAdapter


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
            showReportConfirmDialog()
        }
        SDO.syncNotices()

        val starredNotices = SDO.notices
        val recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = StarredNoticesListItemAdapter(this, starredNotices)
        recyclerView.setHasFixedSize(true)

        //db-stuff probably goes somewhere else in future revisions
        val db = Room.databaseBuilder(
            applicationContext, NoticeDatabase::class.java, "db-tinderPol"
        ).fallbackToDestructiveMigration().build()

        val noticeDao = db.noticeDao()

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