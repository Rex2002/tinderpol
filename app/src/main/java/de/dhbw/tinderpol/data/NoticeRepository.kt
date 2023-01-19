package de.dhbw.tinderpol.data

import android.util.Log
import de.dhbw.tinderpol.util.Observer
import de.dhbw.tinderpol.util.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.function.Consumer
import kotlin.coroutines.coroutineContext

class NoticeRepository {
    companion object {
        private val localDataSource = LocalNoticesDataSource()
        private val remoteDataSource = RemoteNoticesDataSource()
        private var lastUpdated: LocalDate = LocalDate.of(2000, 1, 1)
        private const val dataLifetimeInDays: Long = 10
        private var onUpdate: Consumer<List<Notice>>? = null

        fun listenToUpdates(callback: Consumer<List<Notice>>) {
            onUpdate = callback
        }

        suspend fun syncNotices() {
            if (lastUpdated.plusDays(dataLifetimeInDays) < LocalDate.now()) {
                CoroutineScope(coroutineContext).launch {
                    val remoteNotices = remoteDataSource.fetchNotices().getOrDefault(listOf())
                    if (remoteNotices.isNotEmpty()) {
                        updateRemoteNotices(remoteNotices)
                    }
                }
            }
            updateNotices(localDataSource.getAll())
        }

        private suspend fun updateRemoteNotices(notices: List<Notice>) {
            //TODO implement more efficient updating
            Log.i("API-Req", "Received new Remote Notices...")
            localDataSource.deleteAll()
            Log.i("API-Req", "Cleared Local DB...")
            localDataSource.insert(*notices.toTypedArray())
            Log.i("API-Req", "Added new notices to local DB...")
            lastUpdated = LocalDate.now()
            updateNotices(notices)
        }

        private fun updateNotices(notices: List<Notice>) {
            Log.i("API-Req", "Updating Notices...")
            onUpdate?.accept(notices)
        }
    }
}