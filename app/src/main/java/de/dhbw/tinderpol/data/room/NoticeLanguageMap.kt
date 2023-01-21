package de.dhbw.tinderpol.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.dhbw.tinderpol.data.Notice

@Entity(foreignKeys = [ForeignKey(
    entity = Notice::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("noticeId"),
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class NoticeLanguageMap(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var noticeId: String,
    var language: String
) {
    constructor(noticeId: String, language: String): this(0, noticeId, language)
}
