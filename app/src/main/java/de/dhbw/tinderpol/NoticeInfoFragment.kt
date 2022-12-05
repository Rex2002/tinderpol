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
        // todo: use dataBinding (or another proper method) to set the correct texts in the fragment on create
        //  based on the current SDO state
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_info, container, false)
        _binding = FragmentNoticeInfoBinding.inflate(inflater)
        val notice = SDO.getCurrentNotice();
        binding.nameText.text = notice.firstName.toString() + " " + notice.lastName
        return binding.root;
        //return inflater.inflate(R.layout.fragment_notice_info, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;

    }
}