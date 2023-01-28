package de.dhbw.tinderpol

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("settingsFragment","starting settings fragment")

        _binding = FragmentBottomSettingsBinding.inflate(inflater)
        val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
        binding.switchRedNotices.isChecked = sharedPref?.getBoolean(getString(R.string.show_red_notices_shared_prefs), true) ?: true
        binding.switchYellowNotices.isChecked= sharedPref?.getBoolean(getString(R.string.show_yellow_notices_shared_prefs), true) ?: true
        binding.switchUnNotices.isChecked = sharedPref?.getBoolean(getString(R.string.show_UN_notices_shared_prefs), true) ?: true

        binding.switchRedNotices.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            syncFlag = true
            if (sharedPref != null) {
                with(sharedPref.edit()){
                    putBoolean(getString(R.string.show_red_notices_shared_prefs), b)
                    if(!checkMinNoticesSelected()){
                        putBoolean(getString(R.string.show_red_notices_shared_prefs), true)
                        binding.switchRedNotices.isChecked = true
                        Toast.makeText(activity, "You have to select at least one notice type", Toast.LENGTH_SHORT).show()
                    }
                    apply()
                }
            }
        }
        binding.switchYellowNotices.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            syncFlag = true
            if (sharedPref != null) {
                with(sharedPref.edit()){
                    putBoolean(getString(R.string.show_yellow_notices_shared_prefs), b)
                    if(!checkMinNoticesSelected()){
                        putBoolean(getString(R.string.show_yellow_notices_shared_prefs), true)
                        binding.switchYellowNotices.isChecked = true
                        Toast.makeText(activity, "You have to select at least one notice type", Toast.LENGTH_SHORT).show()
                    }
                    apply()
                }
            }
        }
        binding.switchUnNotices.setOnCheckedChangeListener{_: CompoundButton, b:Boolean ->
            syncFlag = true
            if(sharedPref != null){
                with(sharedPref.edit()){
                    putBoolean(getString(R.string.show_UN_notices_shared_prefs), b)
                    if(!checkMinNoticesSelected()){
                        putBoolean(getString(R.string.show_UN_notices_shared_prefs), true)
                        binding.switchUnNotices.isChecked = true
                        Toast.makeText(activity, "You have to select at least one notice type", Toast.LENGTH_SHORT).show()
                    }
                    apply()
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
            Log.i("settingsFragment","clearing swipe history")
            GlobalScope.launch(Dispatchers.IO) {
                if (sharedPref != null) {
                    SDO.clearSwipeHistory(sharedPref, resources)
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
            Log.i("settingsFragment", "initiating force sync of local storage")
            if(activity != null){
                GlobalScope.launch {
                    (activity as MainActivity).syncNotices(true)
                }
            }
            else {
                Log.e("settingsFragment", "sync failed due to activity being null")
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        Log.i("settingsFragment", "onDestroy called")
        super.onDestroy()
        val a: MainActivity? = activity as MainActivity?
        GlobalScope.launch {
            if(syncFlag && a != null){
                a.syncNotices()
            }
        }
        _binding = null
    }

    private fun checkMinNoticesSelected() : Boolean{
        val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
        return if(sharedPref != null) {
            sharedPref.getBoolean(getString(R.string.show_red_notices_shared_prefs), true)
                    || sharedPref.getBoolean(getString(R.string.show_UN_notices_shared_prefs), true)
                    || sharedPref.getBoolean(getString(R.string.show_yellow_notices_shared_prefs), true)
        } else false

    }

}