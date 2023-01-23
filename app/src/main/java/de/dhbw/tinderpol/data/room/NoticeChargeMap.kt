package de.dhbw.tinderpol.data.room

import androidx.room.Entity

@Entity(primaryKeys = ["noticeId", "chargeId"])
data class NoticeChargeMap(
    var noticeId: String,
    var chargeId: String
)
