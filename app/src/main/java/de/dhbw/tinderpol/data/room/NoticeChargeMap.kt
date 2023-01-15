package de.dhbw.tinderpol.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeChargeMap(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var noticeId: String,
    var chargeId: Int
) {
    constructor(noticeId: String, chargeId: Int): this(0, noticeId, chargeId)
}
