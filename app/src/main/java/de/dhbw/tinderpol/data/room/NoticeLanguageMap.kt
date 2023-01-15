package de.dhbw.tinderpol.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeLanguageMap(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var noticeId: String,
    var language: String
) {
    constructor(noticeId: String, language: String): this(0, noticeId, language)
}
