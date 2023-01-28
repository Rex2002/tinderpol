package de.dhbw.tinderpol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.dhbw.tinderpol.data.MapsData
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.databinding.FragmentNoticeInfoBinding
import de.dhbw.tinderpol.util.Util.Companion.isBlankStr
import de.dhbw.tinderpol.util.Util.Companion.isBlankNum
import de.dhbw.tinderpol.util.Util.Companion.sexToStr
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NoticeInfoFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNoticeInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var notice:Notice


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
        Log.i("noticeInfo", "create view called")
        _binding = FragmentNoticeInfoBinding.inflate(inflater)
        notice = SDO.getNotice(arguments?.getString("notice", ""))

        val firstName = if (isBlankStr(notice.firstName)) "" else notice.firstName
        val lastName = if (isBlankStr(notice.lastName)) "" else notice.lastName
        val nameSeparator = if (isBlankStr(firstName) || isBlankStr(lastName)) "" else " "
        val sexStr = sexToStr(notice.sex)
        val isNameVisible = !isBlankStr(firstName) || !isBlankStr(lastName)
        val nameText = if (isNameVisible) "${firstName + nameSeparator + lastName} ($sexStr)" else "Sex: $sexStr"
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
            if (SDO.isNoticeStarred(notice)) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off )
        binding.starNoticeImageButton.setBackgroundResource(0)

        binding.starNoticeImageButton.setOnClickListener{
            Log.i("noticeInfo", "starNotice button clicked")
            SDO.toggleStarredNotice(notice)
            binding.starNoticeImageButton.setImageResource(
                if (SDO.isNoticeStarred(notice)) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off )
        }

        binding.btnMap.setOnClickListener {
            Log.i("noticeInfo", "viewMap button clicked")
            val intentionalStuffHappening = Intent(context, MapsActivity::class.java)
            val data = MapsData(notice.nationalities ?: listOf(), notice.charges?.map { it.country } ?: listOf(), notice.birthCountry, notice.birthPlace)
            intentionalStuffHappening.putExtra("data", MapsData.serialize(data))
            startActivity(intentionalStuffHappening)
        }
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i("noticeInfo", "killing notice info fragment")
        // if argument is set it is implied that the calling activity is the main activity
        if(arguments?.getString("notice", null) != null && activity != null) {
            (activity as MainActivity).updateStarredNoticesList()
            GlobalScope.launch{
                SDO.persistStatus(notice)
            }
        }
        _binding = null
    }
}