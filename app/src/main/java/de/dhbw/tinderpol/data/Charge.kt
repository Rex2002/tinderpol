package de.dhbw.tinderpol.data

import androidx.room.Entity

@Entity(primaryKeys = ["country", "charge"])
data class Charge(
    val country: String = "",
    val charge: String = ""
)
