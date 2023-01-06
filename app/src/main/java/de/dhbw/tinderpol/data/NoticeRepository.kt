package de.dhbw.tinderpol.data

import android.util.Log

class NoticeRepository {
    companion object {
        private val localDataSource = LocalNoticesDataSource()
        private val remoteDataSource = RemoteNoticesDataSource()

        suspend fun fetchNotices(): List<Notice> {
            val res = remoteDataSource.fetchNotices().getOrDefault(emptyList())
            Log.i("API-Req", res.size.toString())
            if (res.size > 6) Log.i("API-Req", res.subList(0, 5).toString())
            return res
            // if (localDataSource.lastUpdated() > 100) {
            //     localDataSource.update(remoteDataSource.fetchNotices())
            // }
            // return localDataSource.fetchNotices()
        }
    }
}