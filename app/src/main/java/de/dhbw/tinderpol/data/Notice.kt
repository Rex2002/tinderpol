package de.dhbw.tinderpol.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Notice(
    //TODO figure out solution for collection types
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
    @Ignore var charges: List<Charge>? = listOf(),
    var spokenLanguages: List<String>? = listOf(),
    var weight: Number? = 60,
    var height: Number? = 180,
    var starred: Boolean = false,
) {
    constructor(): this(
        "", "", "", "", "", listOf(""), listOf(""), SexID.U,
        "", "", listOf(Charge()), listOf(), 80, 180, false
    )

    override fun toString(): String {
        return "Notice(id='$id', lastName=$lastName, firstName=$firstName, birthDate=$birthDate, nationalities=$nationalities, imgs=$imgs, sex=$sex, birthCountry=$birthCountry, birthPlace=$birthPlace, charges=$charges, weight=$weight, height=$height)"
    }
}
