package de.dhbw.tinderpol.data

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.function.Consumer
import kotlin.coroutines.coroutineContext

class NoticeRepository {
    companion object {
        private val localDataSource = LocalNoticesDataSource()
        private val remoteDataSource = RemoteNoticesDataSource()
        private const val dataLifetimeInMillis: Long = 86400000
        private var onUpdate: Consumer<List<Notice>>? = null

        fun listenToUpdates(callback: Consumer<List<Notice>>) {
            onUpdate = callback
        }

        suspend fun syncNotices(sharedPref: SharedPreferences?, forceSync: Boolean = false) {
            val lastUpdated: Long = sharedPref?.getLong("lastUpdated", 0) ?: 0

            if (forceSync || System.currentTimeMillis() - lastUpdated > dataLifetimeInMillis) {
                Log.i("noticeRepository", "syncing notices with remote data sources")
                Log.i("noticeRepository", "force Sync: $forceSync")
                Log.i("noticeRepository", "lastUpdated: $lastUpdated")
                Log.i("noticeRepository", "diff: " + (System.currentTimeMillis() - lastUpdated))
                CoroutineScope(coroutineContext).launch {
                    val remoteNotices = remoteDataSource.fetchNotices().getOrDefault(listOf())
                    if (remoteNotices.isNotEmpty()) {
                        updateRemoteNotices(remoteNotices)
                        Log.i("noticeRepository", "updating notices due to expired lifetime")
                        sharedPref?.edit()?.putLong("lastUpdated", System.currentTimeMillis())?.apply()
                    }
                }
            }
            updateNotices(localDataSource.getAll())
        }

        private suspend fun updateRemoteNotices(notices: List<Notice>) {
            updateNotices(notices)
            //TODO implement more efficient updating
            Log.i("API-Req", "Received new Remote Notices...")
            localDataSource.deleteAll()
            Log.i("API-Req", "Cleared Local DB...")
            localDataSource.insert(*notices.toTypedArray())
            Log.i("API-Req", "Added new notices to local DB...")
        }

        private fun updateNotices(notices: List<Notice>) {
            Log.i("API-Req", "Updating Notices...")
            onUpdate?.accept(notices)
        }
    }
}