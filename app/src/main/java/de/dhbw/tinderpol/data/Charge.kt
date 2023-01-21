package de.dhbw.tinderpol.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Charge(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val country: String = "",
    val charge: String = ""
) {
    constructor(country: String, charge: String): this(0, country, charge)
}
