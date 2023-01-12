package de.dhbw.tinderpol

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.dhbw.tinderpol.databinding.FragmentBottomSettingsBinding

class BottomSettingsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSettingsBinding.inflate(inflater)
        binding.switchRedNotices.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()){
                    putBoolean("showRedNotices", b)
                    apply()

                }
            }
        }
        binding.switchYellowNotices.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            val sharedPref = activity?.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()){
                    this.putBoolean("showYellowNotices", b)
                    this.apply()
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}