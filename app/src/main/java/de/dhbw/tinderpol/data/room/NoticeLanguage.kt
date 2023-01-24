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
)], primaryKeys = ["noticeId", "language"])
data class NoticeLanguage(
    var noticeId: String,
    var language: String
)
