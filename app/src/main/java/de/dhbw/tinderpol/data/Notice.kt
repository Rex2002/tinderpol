package de.dhbw.tinderpol.data

data class Notice(
    val id: String,
    val type: String? = "",
    val lastName: String? = "",
    val firstName: String? = "",
    val birthDate: String? = "",
    val nationalities: List<String>? = listOf(),
    val imgs: List<String>? = listOf(),
    val sex: SexID = SexID.U,
    val birthCountry: String? = "",
    val birthPlace: String? = "",
    val charges: List<Charge>? = listOf(),
    val spokenLanguages: List<String>? = listOf(),
    val weight: Int? = 60,
    val height: Int? = 180,
)
