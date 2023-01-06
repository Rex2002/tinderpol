package de.dhbw.tinderpol.data

import androidx.core.net.toUri
import kotlinx.coroutines.delay

class RemoteNoticesDataSource {
    suspend fun fetchNotices(): List<Notice> {
        delay(2000)
        return listOf(Notice("2018/46058", "Lastname", "Firstname", null, listOf("RU"), listOf("https://ws-public.interpol.int/notices/v1/red/2018-46058/images/61071213".toUri())))
    }
}