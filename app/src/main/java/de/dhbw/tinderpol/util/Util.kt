package de.dhbw.tinderpol.util

import android.content.Context
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

    fun errorView(context: Context, message: String, title: String="An error occurred", onClose: KFunction0<Unit> =  object : KFunction0<Unit>{
        override val annotations: List<Annotation>
            get() = listOf()
        override val isAbstract: Boolean
            get() = false
        override val isFinal: Boolean
            get() = false
        override val isOpen: Boolean
            get() = false
        override val name: String
            get() = "empty"
        override val parameters: List<KParameter>
            get() = listOf()
        override val returnType: KType
            get() = object : KType{
                override val annotations: List<Annotation>
                    get() = listOf()
                override val arguments: List<KTypeProjection>
                    get() = listOf()
                override val classifier: KClassifier?
                    get() = null
                override val isMarkedNullable: Boolean
                    get() = false
            }
        override val typeParameters: List<KTypeParameter>
            get() = listOf()
        override val visibility: KVisibility?
            get() = null
        override fun call(vararg args: Any?) {
            return
        }
        override fun callBy(args: Map<KParameter, Any?>) {
            return
        }
        override val isExternal: Boolean
            get() = false
        override val isInfix: Boolean
            get() = false
        override val isInline: Boolean
            get() = true
        override val isOperator: Boolean
            get() = false
        override val isSuspend: Boolean
            get() = false

        override fun invoke() {
            return
        }}){
        val alertDialog : AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.setTitle(title).setIcon(com.google.android.material.R.drawable.mtrl_ic_error).setMessage(message).setPositiveButton("Ok"
        ) { dialogInterface, _ ->
            dialogInterface.dismiss()
            onClose()
        }.show()
    }
}
}