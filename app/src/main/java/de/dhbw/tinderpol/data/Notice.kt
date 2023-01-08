package de.dhbw.tinderpol.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Notice(
    //TODO figure out solution for collection types
    @PrimaryKey
    var id: String,
    var lastName: String?,
    var firstName: String?,
    var birthDate: String?,
    @Ignore var nationalities: List<String>?,
    @Ignore var imgs: List<String>?,
    var sex: SexID,
    var birthCountry: String?,
    var birthPlace: String?,
    @Ignore var charges: List<Charge>?,
    var weight: Int?,
    var height: Int?,
) {
    constructor(): this(
        "", "", "", "", listOf(""), listOf(""), SexID.U,
        "", "", listOf(Charge()), 80, 180
    )
}
