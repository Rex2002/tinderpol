package de.dhbw.tinderpol

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import de.dhbw.tinderpol.data.LocalNoticesDataSource
import de.dhbw.tinderpol.data.room.NoticeDatabase
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import de.dhbw.tinderpol.util.StarredNoticesListItemAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: StarredNoticesListItemAdapter


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {
            SDO.syncNotices(getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE))
        }

        val db = Room.databaseBuilder(
            applicationContext, NoticeDatabase::class.java, "db-tinderPol"
        ).fallbackToDestructiveMigration().build()
        LocalNoticesDataSource.dao = db.noticeDao()

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            showReportConfirmDialog()
        }
        adapter = StarredNoticesListItemAdapter(this, SDO.starredNotices)
        recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        binding.textViewEmptyStarredList.visibility =if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE
    }

    private fun showReportConfirmDialog(){
        val settingsFragment = BottomSettingsFragment()
        supportFragmentManager.beginTransaction().add(settingsFragment, "").commit()
    }

    override fun onResume() {
        adapter.updateData(SDO.starredNotices)
        binding.textViewEmptyStarredList.visibility =if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE
        Log.i("main", "resumed main activity")
        Log.i("main", SDO.starredNotices.toString())
        super.onResume()
    }
}