package de.dhbw.tinderpol.util

import de.dhbw.tinderpol.data.SexID

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
    }
}