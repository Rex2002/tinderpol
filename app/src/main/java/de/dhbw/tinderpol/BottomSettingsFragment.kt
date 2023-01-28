package de.dhbw.tinderpol

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.dhbw.tinderpol.databinding.FragmentBottomSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomSettingsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSettingsBinding? = null
    private var syncFlag = false

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSettingsBinding.inflate(inflater)
        val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
        binding.switchRedNotices.isChecked = sharedPref?.getBoolean(getString(R.string.show_red_notices_shared_prefs), true) ?: true
        binding.switchYellowNotices.isChecked= sharedPref?.getBoolean(getString(R.string.show_yellow_notices_shared_prefs), true) ?: true
        binding.switchUnNotices.isChecked = sharedPref?.getBoolean(getString(R.string.show_UN_notices_shared_prefs), true) ?: true

        binding.switchRedNotices.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            syncFlag = true
            val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()){
                    putBoolean(getString(R.string.show_red_notices_shared_prefs), b)
                    this.apply()
                    if(!checkMinNoticesSelected()){
                        this.putBoolean(getString(R.string.show_red_notices_shared_prefs), true)
                        this.apply()
                        binding.switchRedNotices.isChecked = true
                        Toast.makeText(activity, "You have to select at least one notice type", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.switchYellowNotices.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            syncFlag = true
            val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()){
                    this.putBoolean(getString(R.string.show_yellow_notices_shared_prefs), b)
                    this.apply()
                    if(!checkMinNoticesSelected()){
                        this.putBoolean(getString(R.string.show_yellow_notices_shared_prefs), true)
                        this.apply()
                        binding.switchYellowNotices.isChecked = true
                        Toast.makeText(activity, "You have to select at least one notice type", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        binding.switchUnNotices.setOnCheckedChangeListener{_: CompoundButton, b:Boolean ->
            syncFlag = true
            val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            if(sharedPref != null){
                with(sharedPref.edit()){
                    this.putBoolean(getString(R.string.show_UN_notices_shared_prefs), b)
                    this.apply()
                    if(!checkMinNoticesSelected()){
                        this.putBoolean(getString(R.string.show_UN_notices_shared_prefs), true)
                        this.apply()
                        binding.switchUnNotices.isChecked = true
                        Toast.makeText(activity, "You have to select at least one notice type", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.buttonClearStarredNotices.setOnClickListener{
            Log.i("BottomSettingsFragment", "clearing starred notices")
            GlobalScope.launch(Dispatchers.IO){
                SDO.clearStarredNotices()
                withContext(Dispatchers.Main){
                    val a : MainActivity? = if (activity == null) null else activity as MainActivity
                    a?.updateStarredNoticesList()
                    Toast.makeText(activity, "Successfully cleared starred notices", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.buttonClearSwipeHistory.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val sharedPref = activity?.getSharedPreferences(
                    getString(R.string.shared_preferences_file),
                    Context.MODE_PRIVATE
                )
                if (sharedPref != null) {
                    SDO.clearSwipeHistory(sharedPref)
                    activity?.runOnUiThread {
                        Toast.makeText(
                            activity,
                            "Successfully cleared swipe history",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.buttonSyncLocalStorages.setOnClickListener{
            if(activity != null){
                GlobalScope.launch {
                    (activity as MainActivity).syncNotices(true)
                }
            }
            else {
                Toast.makeText(activity, "Sync failed due to unknown reason", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        if(syncFlag && activity != null){
            GlobalScope.launch {
                (activity as MainActivity).syncNotices()
            }
        }
        super.onDestroy()
        _binding = null
    }

    private fun checkMinNoticesSelected() : Boolean{
        val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
        return if(sharedPref != null) {
            sharedPref.getBoolean(getString(R.string.show_red_notices_shared_prefs), true) || sharedPref.getBoolean(getString(R.string.show_UN_notices_shared_prefs), true) || sharedPref.getBoolean(getString(R.string.show_yellow_notices_shared_prefs), true)
        } else false

    }

}