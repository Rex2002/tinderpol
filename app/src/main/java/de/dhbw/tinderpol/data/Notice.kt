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
    val weight: Number? = 60,
    val height: Number? = 180,
    var starred: Boolean = false,
){
    override fun toString(): String {
        return "Notice(id='$id', lastName=$lastName, firstName=$firstName, birthDate=$birthDate, nationalities=$nationalities, imgs=$imgs, sex=$sex, birthCountry=$birthCountry, birthPlace=$birthPlace, charges=$charges, weight=$weight, height=$height)"
    }
}
