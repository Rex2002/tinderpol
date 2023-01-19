package de.dhbw.tinderpol

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            val sharedPref = getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            if((sharedPref?.getLong(
                    "lastUpdated",
                    (System.currentTimeMillis() + 86401)
                )?.minus(System.currentTimeMillis())
                    ?: (System.currentTimeMillis() + 86401)) > 8
            ){
                // TODO change the 6 to 86400
                Log.i("main", "updating notices due to expired lifetime")
                SDO.syncNotices()
                sharedPref.edit().putLong("lastUpdated", System.currentTimeMillis()).apply()
            }


        }

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            showReportConfirmDialog()
        }

        val starredNotices = SDO.getStarredNoticesList()
        val recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = StarredNoticesListItemAdapter(this, starredNotices)
        recyclerView.setHasFixedSize(true)

    }

    private fun showReportConfirmDialog(){
        val settingsFragment = BottomSettingsFragment()
        supportFragmentManager.beginTransaction().add(settingsFragment, "").commit()
    }
}