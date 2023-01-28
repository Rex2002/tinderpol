package de.dhbw.tinderpol.data

import android.content.Context
import android.util.Log
import de.dhbw.tinderpol.R
import java.util.function.Consumer
import java.util.function.Predicate

class NoticeRepository {
    companion object {
        private val localDataSource = LocalDataSource()
        private val remoteDataSource = RemoteDataSource()
        private const val dataLifetimeInMillis: Long = 86400000
        private var onUpdate: Consumer<List<Notice>>? = null

        fun listenToUpdates(callback: Consumer<List<Notice>>) {
            onUpdate = callback
        }

        suspend fun syncNotices(context: Context, filter: Predicate<Notice>, forceRemoteSync: Boolean = false) {
            val res = context.resources
            val sharedPref = context.getSharedPreferences(
                res.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE
            )
            val lastUpdated: Long = sharedPref?.getLong(res.getString(R.string.last_updated_shared_prefs), 0) ?: 0

            if (forceRemoteSync || System.currentTimeMillis() - lastUpdated > dataLifetimeInMillis) {
                Log.i("NoticeRepository", "syncing notices with remote data source")
                Log.i("NoticeRepository", "forceRemoteSync: $forceRemoteSync")
                Log.i("NoticeRepository", "lastUpdated: $lastUpdated")
                Log.i("NoticeRepository", "time diff: " + (System.currentTimeMillis() - lastUpdated)/3600 + "h")
                val remoteNotices = remoteDataSource.fetchNotices().getOrDefault(listOf())
                if (remoteNotices.isNotEmpty()) {
                    Log.i("NoticeRepository", "received remote notices...")
                    Log.i("NoticeRepository", "First remote notice: ${remoteNotices[0]}")
                    updateFromRemoteNotices(remoteNotices.toMutableList())
                    sharedPref?.edit()?.putLong(res.getString(R.string.last_updated_shared_prefs), System.currentTimeMillis())?.apply()
                }
                Log.i("NoticeRepository", "sync successful")
            }
            updateSDO(localDataSource.getAllInFilter(filter))
        }

        /*
            Updates Room db by adding new notices and deleting ones that are not included in remote data.
            Does not currently update data within a notice but skips the ids that already exist locally.
         */
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

        private fun updateSDO(notices: List<Notice>) {
            Log.i("NoticeRepository", "Updating SDO")
            onUpdate?.accept(notices)
        }

        suspend fun getStarredNotices(): List<Notice> {
            return localDataSource.getAllInFilter { it.starred }
        }
    }
}
