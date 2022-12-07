package de.dhbw.tinderpol

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ContactInterpolConfirmFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(
            requireActivity()
        )
        alertDialogBuilder.setTitle("Report Criminal Sighting")

        alertDialogBuilder.setMessage("You are about to report a sighting for \n ${SDO.getCurrentNotice().firstName} ${SDO.getCurrentNotice().lastName} \n You will be redirected to a website")

        alertDialogBuilder.setPositiveButton(
            "Continue"
        ) { dialog, which ->
            val openUrl = Intent(Intent.ACTION_VIEW)
            // TODO change URL to current notice url!!!!
            openUrl.data = Uri.parse(SDO.getCurrentImageURL())
            startActivity(openUrl)
        }

        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
            dialog?.dismiss()
        }



        return alertDialogBuilder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_interpol_confirm, container, false)
    }
}