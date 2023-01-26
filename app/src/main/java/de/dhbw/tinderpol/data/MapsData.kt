package de.dhbw.tinderpol.data

import android.util.Log

data class MapsData(
    val nationalities: List<String> = listOf(),
    val chargeCountries: List<String> = listOf(),
    val birthCountry: String? = null,
    val birthPlace: String? = null,
): java.io.Serializable {
    companion object {
        fun serialize(data: MapsData): String {
            Log.i("Countries", "Serializing countries data: $data")
            return "${data.nationalities.joinToString("-")}\n${data.chargeCountries.joinToString("-")}\n${data.birthCountry ?: ""}\n${data.birthPlace ?: ""}"
        }

        fun deserialize(str: String?): MapsData {
            if (str == null) return MapsData()
            Log.i("Countries", "Deserializing countries data: $str")

            val rows = str.split("\n")
            val nationalities = rows[0].split("-")
            val chargeCountries = rows[1].split("-")
            val birthCountry = rows[2].ifBlank { null }
            val birthPlace = rows[3].ifBlank { null }
            return MapsData(nationalities, chargeCountries, birthCountry, birthPlace)
        }
    }
}
