package de.dhbw.tinderpol.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeNationalityMap(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var noticeId: String,
    var nationality: String
) {
    constructor(noticeId: String, nationality: String): this(0, noticeId, nationality)
}
