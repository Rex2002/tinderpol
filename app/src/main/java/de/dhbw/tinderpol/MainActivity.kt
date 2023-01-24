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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    lateinit var adapter: StarredNoticesListItemAdapter


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Room.databaseBuilder(
            applicationContext, NoticeDatabase::class.java, "db-tinderPol"
        ).fallbackToDestructiveMigration().build()
        LocalNoticesDataSource.dao = db.noticeDao()

        adapter = StarredNoticesListItemAdapter(this, SDO.starredNotices)
        recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        binding.textViewEmptyStarredList.visibility = if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {
            SDO.loadCountriesData(resources)
        }

        syncNotices()

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            showReportConfirmDialog()
        }

    }

    private fun showReportConfirmDialog(){
        val settingsFragment = BottomSettingsFragment()
        supportFragmentManager.beginTransaction().add(settingsFragment, "").commit()
    }

    override fun onResume() {
        Log.i("Main", "resume main activity")
        updateStarredNoticesList()
        super.onResume()
    }

    fun updateStarredNoticesList(){
        Log.i("Main", "update starred notices recyclerview")
        adapter.updateData(SDO.starredNotices)
        binding.textViewEmptyStarredList.text = getString(R.string.no_notices_starred)
        binding.textViewEmptyStarredList.visibility = if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun syncNotices(forceRemoteSync: Boolean = false){
        Log.i("Main", "synchronizing notices")
        binding.button.text = getString(R.string.synchronizing_notices)
        binding.button.isEnabled = false
        binding.textViewEmptyStarredList.text = getString(R.string.loading_starred_notices)

        GlobalScope.launch {
            SDO.syncNotices(getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE), forceRemoteSync)
            SDO.initStarredNotices()
            withContext(Dispatchers.Main){
                updateStarredNoticesList()
                binding.button.text = getString(R.string.start_swipe_button)
                binding.button.isEnabled = true
            }
        }


    }
}