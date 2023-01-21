package de.dhbw.tinderpol.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeImageMap(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var noticeId: String,
    var image: String
) {
    constructor(noticeId: String, image: String): this(0, noticeId, image)
}
