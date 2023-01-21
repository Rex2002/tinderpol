package de.dhbw.tinderpol.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Notice(
    @PrimaryKey
    var id: String,
    var type: String? = "",
    var lastName: String? = "",
    var firstName: String? = "",
    var birthDate: String? = "",
    @Ignore var nationalities: List<String>? = listOf(),
    @Ignore var imgs: List<String>? = listOf(),
    var sex: SexID = SexID.U,
    var birthCountry: String? = "",
    var birthPlace: String? = "",
    @Ignore var charges: List<Charge>? = listOf(Charge()),
    @Ignore var spokenLanguages: List<String>? = listOf(),
    var weight: Double? = 0.0,
    var height: Double? = 0.0,
    var starred: Boolean = false,
) {
    constructor(): this(
        "", "", "", "", "", listOf(""), listOf(""), SexID.U,
        "", "", listOf(Charge()), listOf(), 0.0, 0.0, false
    )

    override fun toString(): String {
        return "Notice(id='$id', lastName=$lastName, firstName=$firstName, birthDate=$birthDate, nationalities=$nationalities, imgs=$imgs, sex=$sex, birthCountry=$birthCountry, birthPlace=$birthPlace, charges=$charges, weight=$weight, height=$height)"
    }
}
