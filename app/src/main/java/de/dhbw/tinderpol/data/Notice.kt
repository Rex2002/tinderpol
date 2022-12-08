package de.dhbw.tinderpol.data

import android.net.Uri
import java.util.Date

data class Notice(
    val id: String,
    val lastName: String? = null,
    val firstName: String? = null,
    val birthDate: Date? = null,
    val nationalities: List<String> = listOf(),
    val imgURIs: List<Uri> = listOf(),
    val sex: SexID = SexID.U,
    val birthCountry: String? = null,
    val birthPlace: String? = null,
    val charge: String? = null,
    val weight: Int? = 60,
    val height: Int? = 180,
)
