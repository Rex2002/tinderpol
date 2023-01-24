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
        private val localDataSource = LocalDataSource()
        private val remoteDataSource = RemoteDataSource()
        private const val dataLifetimeInMillis: Long = 86400000
        private var onUpdate: Consumer<List<Notice>>? = null

        fun listenToUpdates(callback: Consumer<List<Notice>>) {
            onUpdate = callback
        }

        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun syncNotices(sharedPref: SharedPreferences?, forceRemoteSync: Boolean = false) {
            val lastUpdated: Long = sharedPref?.getLong("lastUpdated", 0) ?: 0

            val redFilter: Boolean = sharedPref?.getBoolean(R.string.show_red_notices_shared_prefs.toString(), true) == true
            val yellowFilter: Boolean = sharedPref?.getBoolean("ShowYellowNoticesSharedPref", true) == true
            val unFilter: Boolean = sharedPref?.getBoolean("ShowUnNoticesSharedPref", true) == true
            val filter: Predicate<Notice> = Predicate {
                (it.type.equals("red") && redFilter) ||
                        (it.type.equals("yellow") && yellowFilter) ||
                        (it.type.equals("un") && unFilter)
            }

            if (forceRemoteSync || System.currentTimeMillis() - lastUpdated > dataLifetimeInMillis) {
                Log.i("NoticeRepository", "syncing notices with remote data source")
                Log.i("NoticeRepository", "forceRemoteSync: $forceRemoteSync")
                Log.i("NoticeRepository", "lastUpdated: $lastUpdated")
                Log.i("NoticeRepository", "time diff: " + (System.currentTimeMillis() - lastUpdated)/3600 + "h")
                CoroutineScope(coroutineContext).launch {
                    val remoteNotices = remoteDataSource.fetchNotices().getOrDefault(listOf())
                    if (remoteNotices.isNotEmpty()) {
                        Log.i("NoticeRepository", "received remote notices...")
                        updateFromRemoteNotices(remoteNotices.toMutableList())
                        sharedPref?.edit()?.putLong("lastUpdated", System.currentTimeMillis())?.apply()
                    }
                }
                Log.i("NoticeRepository", "sync successful")
            }
            updateSDO(localDataSource.getAll(filter))
        }

        /*
            Updates Room db by adding new notices and deleting ones that are not included in remote data.
            Does not currently update data within a notice but skips the ids that already exist locally.
         */
        @RequiresApi(Build.VERSION_CODES.N)
        private suspend fun updateFromRemoteNotices(notices: MutableList<Notice>) {
            val localIds: MutableList<String> = localDataSource.getAllNoticeIds().toMutableList()
            val newNotices: MutableList<Notice> = mutableListOf()
            notices.forEach {
                if (localIds.contains(it.id))
                    localIds.remove(it.id)
                else
                    newNotices.add(it)
            }
            localDataSource.deleteById(*localIds.toTypedArray())
            Log.i("NoticeRepository", "removed obsolete notices")

            localDataSource.insert(*newNotices.toTypedArray())
            Log.i("NoticeRepository", "added new notices to local DB")
        }

        suspend fun updateStatus(vararg notices: Notice) {
            localDataSource.updateAll(*notices)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun updateSDO(notices: List<Notice>) {
            Log.i("NoticeRepository", "Updating SDO")
            onUpdate?.accept(notices)
        }
    }
}
