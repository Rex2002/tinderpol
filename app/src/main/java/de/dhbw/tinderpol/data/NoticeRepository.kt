package de.dhbw.tinderpol.data

import android.util.Log
import java.time.LocalDate

class NoticeRepository {
    companion object {
        private val localDataSource = LocalNoticesDataSource()
        private val remoteDataSource = RemoteNoticesDataSource()
        private var lastUpdated: LocalDate = LocalDate.of(2000,1,1)

        suspend fun fetchAllNotices(): List<Notice> {
            if (lastUpdated != LocalDate.now()) {
                val remoteNotices = remoteDataSource.fetchNotices().getOrDefault(listOf())
                Log.i("API-Req", remoteNotices.size.toString())
                if (remoteNotices.size > 6)
                    Log.i("API-Req", remoteNotices.subList(0, 5).toString())
                if (remoteNotices.isNotEmpty()) {
                    updateNotices(remoteNotices)
                    lastUpdated = LocalDate.now()
                }
            }
            return localDataSource.getAll()
        }

        private suspend fun updateNotices(notices: List<Notice>) {
            //TODO implement more efficient updating
            localDataSource.deleteAll()
            localDataSource.insert(*notices.toTypedArray())
        }
    }
}