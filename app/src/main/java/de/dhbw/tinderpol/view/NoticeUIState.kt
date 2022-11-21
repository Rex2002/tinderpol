package de.dhbw.tinderpol.view

import android.net.Uri

data class NoticeUIState(
    val id: String,
    val imgURLs: List<Uri> = listOf(),
    val currentImg: Int = 0,
)