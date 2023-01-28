package de.dhbw.tinderpol

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import de.dhbw.tinderpol.data.LocalDataSource
import de.dhbw.tinderpol.data.room.TinderPolDatabase
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import de.dhbw.tinderpol.util.NetworkConnectivityObserver
import de.dhbw.tinderpol.util.StarredNoticesListItemAdapter
import de.dhbw.tinderpol.util.Util
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    lateinit var adapter: StarredNoticesListItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Room.databaseBuilder(
            applicationContext, TinderPolDatabase::class.java, "db-tinderPol"
        ).fallbackToDestructiveMigration().build()
        LocalDataSource.dao = db.getDao()

        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().onEach {
            if (it == NetworkConnectivityObserver.Status.Unavailable)
                SDO.offlineFlag = true
            else if (it == NetworkConnectivityObserver.Status.Available)
                SDO.offlineFlag = false
        }.launchIn(lifecycleScope)

        adapter = StarredNoticesListItemAdapter(this, SDO.starredNotices)
        recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        binding.textViewEmptyStarredList.visibility = if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {
            syncNotices()
            SDO.loadCountriesData(resources)
        }

        binding.textViewExplainText1.setOnClickListener{
            Util.errorView(this, "this is an example error view")
        }

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
        Log.i("Main", "resumed main activity")
        updateStarredNoticesList()
        super.onResume()
    }

    fun updateStarredNoticesList(){
        Log.i("Main", "update starred notices recyclerview")
        adapter.updateData(SDO.starredNotices)
        binding.textViewEmptyStarredList.text = getString(R.string.no_notices_starred)
        binding.textViewEmptyStarredList.visibility = if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE
    }

    suspend fun syncNotices(forceRemoteSync: Boolean = false){
        Log.i("Main", "synchronizing notices")
        runOnUiThread {
            binding.button.text = getString(R.string.synchronizing_notices)
            binding.button.isEnabled = false
            binding.textViewEmptyStarredList.text = getString(R.string.loading_starred_notices)
        }

        CoroutineScope(coroutineContext).launch {
            SDO.initialize(getSharedPreferences(
                getString(R.string.shared_preferences_file),
                Context.MODE_PRIVATE
            ), applicationContext, forceRemoteSync)

            withContext(Dispatchers.Main) {
                updateStarredNoticesList()
                binding.button.text = getString(R.string.start_swipe_button)
                binding.button.isEnabled = true
            }
        }
    }

}