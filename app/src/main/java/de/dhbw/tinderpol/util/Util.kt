package de.dhbw.tinderpol.util

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import de.dhbw.tinderpol.data.SexID
import kotlin.reflect.*

class Util {
    companion object{
        fun isBlankStr(s: String?): Boolean {
            return s == null || s.isBlank()
        }

        fun isBlankNum(n: Number?): Boolean {
            return n == null || n != 0
        }

        fun sexToStr(id: SexID?): String {
            return when (id) {
                SexID.F -> "Female"
                SexID.M -> "Male"
                else -> "unknown"
            }
        }

        fun errorView(context: Context, message: String?, title: String="An error occurred"){
            val m = message ?: "Unknown Error"
            Log.i("util", "showing errorView for $title with content $m")
            AlertDialog.Builder(context).setTitle(title).setIcon(com.google.android.material.R.drawable.mtrl_ic_error).setMessage(m).setPositiveButton("Ok"
                ) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.show()
        }

        fun errorView(context: Context, message: String?, onClose: () -> Unit, title: String="An error occurred"){
            val m = message ?: "Unknown Error"
            Log.i("util", "showing errorView with callback for $title with content $m")
            AlertDialog.Builder(context).setTitle(title).setIcon(com.google.android.material.R.drawable.mtrl_ic_error).setMessage(m).setPositiveButton("Ok"
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                onClose()
            }.show()
        }
}
}