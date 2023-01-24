package de.dhbw.tinderpol.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import de.dhbw.tinderpol.data.Notice

@Entity(foreignKeys = [ForeignKey(
    entity = Notice::class,
    parentColumns = ["id"],
    childColumns = ["noticeId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)],primaryKeys = ["noticeId", "country", "charge"])
data class NoticeCharge(
    var noticeId: String,
    val country: String = "",
    val charge: String = ""
)
