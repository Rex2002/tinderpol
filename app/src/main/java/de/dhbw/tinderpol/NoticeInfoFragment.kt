package de.dhbw.tinderpol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.dhbw.tinderpol.data.SexID
import de.dhbw.tinderpol.databinding.FragmentNoticeInfoBinding

class NoticeInfoFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNoticeInfoBinding? = null

    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun isBlankStr(s: String?): Boolean {
        return s == null || s.isBlank()
    }

    private fun isBlankNum(n: Number?): Boolean {
        return n == null || n != 0
    }

    private fun sexToStr(id: SexID?): String {
        return when (id) {
            SexID.F -> "Female"
            SexID.M -> "Male"
            else -> "unknown"
        }
    }

    private fun <T> bindList(uiEl: TextView, list: List<T>?, beforeStr: String? = null, afterStr: String? = null, joinStr: String = ", ") {
        if (list != null && list.isNotEmpty()) {
            var str = if (!isBlankStr(beforeStr)) beforeStr else ""
            str += list.joinToString(joinStr)
            if (!isBlankStr(afterStr)) str += afterStr
            uiEl.visibility = View.VISIBLE
            uiEl.text = str
        } else {
            uiEl.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeInfoBinding.inflate(inflater)
        val notice = SDO.getCurrentNotice()
        val firstName = if (isBlankStr(notice.firstName)) "" else notice.firstName
        val lastName = if (isBlankStr(notice.lastName)) "" else notice.lastName
        val nameSeparator = if (isBlankStr(firstName) || isBlankStr(lastName)) "" else " "
        val sexStr = sexToStr(notice.sex)
        val isNameVisible = !isBlankStr(firstName) || !isBlankStr(lastName)
        val nameText = if (isNameVisible) "Name: ${firstName + nameSeparator + lastName} ($sexStr)" else "Sex: $sexStr"
        binding.nameText.text = nameText

        if (isBlankStr(notice.type)) {
            binding.noticeTypeText.visibility = View.GONE
        } else {
            binding.noticeTypeText.visibility = View.VISIBLE
            val noticeType = "Type of Notice: ${notice.type}"
            binding.noticeTypeText.text = noticeType
        }

        bindList(binding.crimeDescrText, notice.charges?.map { charge -> charge.charge }, "Charges:\n", "", "\n")

        val birthDate = if (isBlankStr(notice.birthDate)) "" else notice.birthDate
        val birthPlace = if (isBlankStr(notice.birthPlace)) "" else notice.birthPlace
        val birthCountry = if (isBlankStr(notice.birthCountry)) "" else notice.birthCountry
        var birthLoc = birthPlace + if(!isBlankStr(birthPlace) && !isBlankStr(birthCountry)) ", " else "" + birthCountry
        if (!isBlankStr(birthLoc)) birthLoc = "in $birthLoc"
        if (isBlankStr(birthDate) && isBlankStr(birthLoc)) {
            binding.birthInfoText.visibility = View.GONE
        } else {
            binding.birthInfoText.visibility = View.VISIBLE
            var birthInfo = "Born"
            if (!isBlankStr(birthDate)) birthInfo += " $birthDate"
            if (!isBlankStr(birthLoc)) birthInfo += " $birthLoc"
            binding.birthInfoText.text = birthInfo
        }

        var physicals = ""
        if (!isBlankNum(notice.height)) physicals += "Height: ${notice.height}cm"
        if (!isBlankNum(notice.weight)) {
            if (!isBlankStr(physicals)) physicals += ", "
            physicals += "Weight: ${notice.weight}kg"
        }
        if (isBlankStr(physicals)) {
            binding.physicalsText.visibility = View.GONE
        } else {
            binding.physicalsText.visibility = View.VISIBLE
            binding.physicalsText.text = physicals
        }

        bindList(binding.nationalitiesText, notice.nationalities, "Nationalities: ")

        bindList(binding.spokenLanguagesText, notice.spokenLanguages, "Spoken Languages: ")

        binding.starNoticeImageButton.setImageResource(
            if (SDO.isNoticeStarred()) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off )
        binding.starNoticeImageButton.setBackgroundResource(0)

        binding.starNoticeImageButton.setOnClickListener{
            SDO.toggleStarredNotice()
            binding.starNoticeImageButton.setImageResource(
                if (SDO.isNoticeStarred()) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off )
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}