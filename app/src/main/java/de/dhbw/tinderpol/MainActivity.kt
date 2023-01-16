package de.dhbw.tinderpol

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.dhbw.tinderpol.databinding.ActivityMainBinding
import de.dhbw.tinderpol.util.StarredNoticesListItemAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {
            SDO.syncNotices()
        }

        binding.button.setOnClickListener {
            val intentToStartShit = Intent(this, SwipeActivity::class.java)
            startActivity(intentToStartShit)
        }

        binding.imageButtonSettings.setOnClickListener{
            showReportConfirmDialog()
        }

        val starredNotices = SDO.notices
        val recyclerView = binding.recyclerViewStarredNoticesList
        recyclerView.adapter = StarredNoticesListItemAdapter(this, starredNotices)
        recyclerView.setHasFixedSize(true)
    }

    private fun showReportConfirmDialog(){
        val settingsFragment = BottomSettingsFragment()
        supportFragmentManager.beginTransaction().add(settingsFragment, "").commit()
    }
}