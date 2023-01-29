package de.dhbw.tinderpol

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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: StarredNoticesListItemAdapter


    @OptIn(DelicateCoroutinesApi::class)
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
            if (it == NetworkConnectivityObserver.Status.Unavailable) {
                Log.i("ConnectivityObserver", "network state changed to unavailable")
                SDO.offlineFlag = true
            }
            else if (it == NetworkConnectivityObserver.Status.Available) {
                SDO.offlineFlag = false
                Log.i("ConnectivityObserver", "network state changed to available")
            }
        }.launchIn(lifecycleScope)

        adapter = StarredNoticesListItemAdapter(this, SDO.starredNotices)
        recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        binding.textViewEmptyStarredList.visibility = if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {
            Log.i("main","starting initial notices sync")
            syncNotices()
            SDO.loadCountriesData(resources)
        }

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            showBottomSettingsDialog()
        }
    }

    private fun showBottomSettingsDialog(){
        val settingsFragment = BottomSettingsFragment()
        supportFragmentManager.beginTransaction().add(settingsFragment, "").commit()
    }

    override fun onResume() {
        Log.i("main", "resumed main activity")
        updateStarredNoticesList()
        super.onResume()
    }

    fun updateStarredNoticesList(){
        Log.i("main", "update starred notices recyclerview")
        adapter.updateData(SDO.starredNotices)
        binding.textViewEmptyStarredList.text = getString(R.string.no_notices_starred)
        binding.textViewEmptyStarredList.visibility = if (SDO.starredNotices.size != 0) View.GONE else View.VISIBLE
    }

    suspend fun syncNotices(forceRemoteSync: Boolean = false){
        Log.i("main", "synchronizing notices")
        runOnUiThread {
            binding.button.text = getString(R.string.synchronizing_notices)
            binding.button.isEnabled = false
            binding.textViewEmptyStarredList.text = getString(R.string.loading_starred_notices)
        }

        CoroutineScope(coroutineContext).launch {
            SDO.initialize(applicationContext, forceRemoteSync)

            withContext(Dispatchers.Main) {
                updateStarredNoticesList()
                binding.button.text = getString(R.string.start_swipe_button)
                binding.button.isEnabled = true
            }
        }
    }

}