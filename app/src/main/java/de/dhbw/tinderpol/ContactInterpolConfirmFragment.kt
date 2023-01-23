package de.dhbw.tinderpol

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

        val notice = SDO.getCurrentNotice()
        alertDialogBuilder.setTitle("Report Criminal Sighting")

        alertDialogBuilder.setMessage("You are about to report a sighting for \n ${notice.firstName} ${notice.lastName} \n You will be redirected to a website")

        alertDialogBuilder.setPositiveButton(
            "Continue"
        ) { _, _ ->
            Log.i("contactIntPolConf", "contacting interpol with url: https://www.interpol.int/Contacts/Fugitives-wanted-persons?notice=${
                notice.id.replace(
                    "/",
                    "-"
                )
            }")
            val openUrl = Intent(Intent.ACTION_VIEW)
            // TODO change URL to current notice url!!!!
            openUrl.data = Uri.parse("https://www.interpol.int/Contacts/Fugitives-wanted-persons?notice=${notice.id.replace("/","-")}")
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