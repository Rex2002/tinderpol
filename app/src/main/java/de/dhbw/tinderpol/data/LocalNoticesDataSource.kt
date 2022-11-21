package de.dhbw.tinderpol.data

import androidx.core.net.toUri

class LocalNoticesDataSource {
    fun lastUpdated(): Int {
        return 10
    }

    fun update(notices: List<Notice>) {
        // TODO
    }

    fun fetchNotices(): List<Notice> {
        return listOf(Notice("2018/46058", "Lastname", "Firstname", null, listOf("RU"), listOf("https://ws-public.interpol.int/notices/v1/red/2018-46058/images/61071213".toUri())))
    }
}