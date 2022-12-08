package de.dhbw.tinderpol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.dhbw.tinderpol.databinding.FragmentNoticeInfoBinding

class NoticeInfoFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNoticeInfoBinding? = null

    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeInfoBinding.inflate(inflater)
        val notice = SDO.getCurrentNotice()
        val nameText = "Name: ${notice.firstName.toString()} ${notice.lastName} (${notice.sex.toString()})"
        binding.nameText.text = nameText
        val crimeDescr = "Charge: ${notice.charge}"
        binding.crimeDescrText.text = crimeDescr
        val birthInfo = "Born ${notice.birthDate.toString()} in ${notice.birthPlace}, ${notice.birthCountry}"
        binding.birthInfoText.text = birthInfo
        val physicals = "Height: ${notice.height}cm, Weight: ${notice.weight}kg"
        binding.physicalsText.text = physicals
        val nationalities = "Nationalities: ${notice.nationalities.joinToString(separator = ", ") }"
        binding.nationalitiesText.text = nationalities

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}