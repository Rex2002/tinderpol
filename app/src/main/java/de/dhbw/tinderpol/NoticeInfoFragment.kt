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
        val nameText = "Name: ${notice.firstName} ${notice.lastName} (${notice.sex})"
        binding.nameText.text = nameText
        val crimeDescr = "Charges: \n${notice.charges?.joinToString(separator = "\n") { charge -> charge.charge }}"
        binding.crimeDescrText.text = crimeDescr
        val birthInfo = "Born ${notice.birthDate} in ${notice.birthPlace}, ${notice.birthCountry}"
        binding.birthInfoText.text = birthInfo
        val physicals = "Height: ${notice.height}cm, Weight: ${notice.weight}kg"
        binding.physicalsText.text = physicals
        val nationalities = "Nationalities: ${notice.nationalities?.joinToString(separator = ", ") }"
        binding.nationalitiesText.text = nationalities

        binding.starNoticeImageButton.setImageResource(
            if (SDO.noticeIsStarred(notice.id)) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off )
        binding.starNoticeImageButton.setBackgroundResource(0)

        binding.starNoticeImageButton.setOnClickListener{
            SDO.toggleStarredNotice(notice.id)
            binding.starNoticeImageButton.setImageResource(
                if (SDO.noticeIsStarred(notice.id)) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off )
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}