package de.dhbw.tinderpol.data

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import de.dhbw.tinderpol.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.function.Consumer
import java.util.function.Predicate
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

        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun syncNotices(sharedPref: SharedPreferences?, forceSync: Boolean = false) {
            val lastUpdated: Long = sharedPref?.getLong("lastUpdated", 0) ?: 0

            val redFilter: Boolean = sharedPref?.getBoolean(R.string.show_red_notices_shared_prefs.toString(), true) == true
            val yellowFilter: Boolean = sharedPref?.getBoolean("ShowYellowNoticesSharedPref", true) == true
            val unFilter: Boolean = sharedPref?.getBoolean("ShowUnNoticesSharedPref", true) == true
            val filter: Predicate<Notice> = Predicate {
                (it.type.equals("red") && redFilter) ||
                        (it.type.equals("yellow") && yellowFilter) ||
                        (it.type.equals("un") && unFilter)
            }

            if (forceSync || System.currentTimeMillis() - lastUpdated > dataLifetimeInMillis) {
                Log.i("noticeRepository", "syncing notices with remote data sources")
                Log.i("noticeRepository", "force Sync: $forceSync")
                Log.i("noticeRepository", "lastUpdated: $lastUpdated")
                Log.i("noticeRepository", "diff: " + (System.currentTimeMillis() - lastUpdated)/3600 + "h")
                CoroutineScope(coroutineContext).launch {
                    val remoteNotices = remoteDataSource.fetchNotices().getOrDefault(listOf())
                    if (remoteNotices.isNotEmpty()) {
                        updateFromRemoteNotices(remoteNotices)
                        sharedPref?.edit()?.putLong("lastUpdated", System.currentTimeMillis())?.apply()
                    }
                }
                Log.i("noticeRepository", "sync successful")
            }
            updateNotices(localDataSource.getAll(filter))
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private suspend fun updateFromRemoteNotices(notices: List<Notice>) {
            updateNotices(notices)
            //TODO implement more efficient updating
            Log.i("API-Req", "Received new Remote Notices...")
            localDataSource.deleteAll()
            Log.i("API-Req", "Cleared Local DB...")
            localDataSource.insert(*notices.toTypedArray())
            Log.i("API-Req", "Added new notices to local DB...")
        }

        suspend fun updateStatus(vararg notices: Notice) {
            localDataSource.updateAll(*notices)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun updateNotices(notices: List<Notice>) {
            Log.i("API-Req", "Updating Notices...")
            onUpdate?.accept(notices)
        }
    }
}