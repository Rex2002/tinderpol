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
        return listOf()
    }
}