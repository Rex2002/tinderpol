package de.dhbw.tinderpol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.databinding.FragmentNoticeInfoBinding
import de.dhbw.tinderpol.util.Util.Companion.isBlankStr
import de.dhbw.tinderpol.util.Util.Companion.isBlankNum
import de.dhbw.tinderpol.util.Util.Companion.sexToStr


class NoticeInfoFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNoticeInfoBinding? = null

    private val binding get() = _binding!!


    private fun changeTextViewVisibility(view: TextView, parent: LinearLayout, text: String? = null, show: Boolean = text == null) {
        if (show) {
            parent.setPadding(8)
            view.setPadding(8)
            view.text = text ?: ""
            view.textSize = 15F
            view.visibility = View.VISIBLE
        } else {
            parent.setPadding(0)
            view.setPadding(0)
            view.text = ""
            view.textSize = 0F
            view.visibility = View.GONE
        }
    }

    private fun <T> bindList(uiEl: TextView, parent: LinearLayout, list: List<T>?, beforeStr: String? = null, afterStr: String? = null, joinStr: String = ", ") {
        if (list != null && list.isNotEmpty()) {
            var str = if (!isBlankStr(beforeStr)) beforeStr else ""
            str += list.joinToString(joinStr)
            if (!isBlankStr(afterStr)) str += afterStr
            changeTextViewVisibility(uiEl, parent, str, true)
        } else {
            changeTextViewVisibility(uiEl, parent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeInfoBinding.inflate(inflater)
        val notice : Notice = SDO.getNotice(arguments?.getString("notice", ""))

        val firstName = if (isBlankStr(notice.firstName)) "" else notice.firstName
        val lastName = if (isBlankStr(notice.lastName)) "" else notice.lastName
        val nameSeparator = if (isBlankStr(firstName) || isBlankStr(lastName)) "" else " "
        val sexStr = sexToStr(notice.sex)
        val isNameVisible = !isBlankStr(firstName) || !isBlankStr(lastName)
        val nameText = if (isNameVisible) "Name: ${firstName + nameSeparator + lastName} ($sexStr)" else "Sex: $sexStr"
        binding.nameText.text = nameText

        changeTextViewVisibility(binding.noticeTypeText, binding.noticeTypeLinearLayout, "Type of Notice: ${notice.type}", !isBlankStr(notice.type))

        bindList(binding.crimeDescrText, binding.crimeDescrLinearLayout, notice.charges?.map { charge -> charge.charge }, "Charges:\n", "", "\n")

        val birthDate = if (isBlankStr(notice.birthDate)) "" else notice.birthDate
        val birthPlace = if (isBlankStr(notice.birthPlace)) "" else notice.birthPlace
        val birthCountry = if (isBlankStr(notice.birthCountry)) "" else notice.birthCountry
        var birthLoc = birthPlace + if(!isBlankStr(birthPlace) && !isBlankStr(birthCountry)) ", " else "" + birthCountry
        if (!isBlankStr(birthLoc)) birthLoc = "in $birthLoc"
        var birthInfo = "Born"
        if (!isBlankStr(birthDate)) birthInfo += " $birthDate"
        if (!isBlankStr(birthLoc)) birthInfo += " $birthLoc"
        changeTextViewVisibility(binding.birthInfoText, binding.birthInfoLinearLayout, birthInfo, !isBlankStr(birthDate) || !isBlankStr(birthLoc))

        var physicals = ""
        if (!isBlankNum(notice.height)) physicals += "Height: ${notice.height}cm"
        if (!isBlankNum(notice.weight)) {
            if (!isBlankStr(physicals)) physicals += ", "
            physicals += "Weight: ${notice.weight}kg"
        }
        changeTextViewVisibility(binding.physicalsText, binding.physicalsLinearLayout, physicals, !isBlankStr(physicals))

        bindList(binding.nationalitiesText, binding.nationalitiesLinearLayout, notice.nationalities, "Nationalities: ")

        bindList(binding.spokenLanguagesText, binding.spokenLanguagesLinearLayout, notice.spokenLanguages, "Spoken Languages: ")

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

        binding.btnMap.setOnClickListener {
            mapCriminalNational()
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}