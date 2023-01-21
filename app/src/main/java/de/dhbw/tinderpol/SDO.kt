package de.dhbw.tinderpol

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.data.NoticeRepository
import java.util.function.Consumer

class SDO {
    companion object {
        private val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        private var currentNoticeIndex = 0
        private var notices : List<Notice> = listOf()
        val emptyNotice = Notice("empty", imgs = listOf(noImg))
        var onUpdate: Consumer<Notice>? = null
        private var isListeningToUpdates = false
        var starredNotices: MutableList<Notice> = mutableListOf()

        /**
         * Gets notices stored in Room and updates Room API on the first call of the day (in the background)
         */
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun syncNotices(sharedPref: SharedPreferences?, forceSync: Boolean = false) {
            Log.i("SDO", "Syncing Notices...")
            if (!isListeningToUpdates) {
                NoticeRepository.listenToUpdates { update(it) }
                isListeningToUpdates = true
            }
            NoticeRepository.syncNotices(sharedPref, forceSync)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun update(newNotices: List<Notice>) {
            Log.i("SDO", "Notices updated...")
            notices = newNotices
            onUpdate?.accept(getCurrentNotice())
            initStarredNotices()
        }

        fun listenToUpdates(callback: Consumer<Notice>?) {
            onUpdate = callback
        }

        private fun initStarredNotices() {
            starredNotices = notices.filter { it.starred }.toMutableList()
        }

        fun getCurrentNotice() : Notice {
            if (notices.isNotEmpty()) Log.i("SDO", notices[currentNoticeIndex].toString())
            if (notices.size <= currentNoticeIndex) {
                // Realistically only the else branch will ever be used here, but we check just in case to prevent any bugs
                if (notices.isNotEmpty()) currentNoticeIndex = notices.size - 1
                else return emptyNotice
            }
            return notices[currentNoticeIndex]
        }

        fun getNotice(id: String? = ""): Notice {
            if(id == "" || id == null){
                return getCurrentNotice()
            }
            val notice = notices.find{it.id == id} ?: emptyNotice
            Log.i("SDO", notice.toString())
            return notice
        }

        fun getNextNotice() : Notice {
            if (notices.isNotEmpty())
                currentNoticeIndex++

            if (currentNoticeIndex >= notices.size) {
                currentNoticeIndex = notices.size -1
                throw Exception("Reached last notice in local cache")
                //TODO create meaningful UI event for after last notice
            }

            Log.i("Notice-Call", currentNoticeIndex.toString())
            if (notices.isNotEmpty())
                Log.i("Notice-Call", notices[currentNoticeIndex].toString())

            return getCurrentNotice()
        }

        fun getPrevNotice() : Notice{
            if (notices.isNotEmpty())
                currentNoticeIndex--
            if (currentNoticeIndex < 0) {
                currentNoticeIndex = 0
                throw Exception("Already on first notice in local cache")
                //TODO create meaningful UI event for before first notice
            }

            Log.i("Notice-Call", currentNoticeIndex.toString())
            if (notices.isNotEmpty())
                Log.i("Notice-Call", notices[currentNoticeIndex].toString())

            return getCurrentNotice()
        }

        fun getImageURL(n: Notice?  = null): String {
            val notice = n ?: getCurrentNotice()
            return if (notice.imgs == null || notice.imgs!!.isEmpty()) noImg
            else notice.imgs!![0]
        }

        fun isNoticeStarred(notice: Notice? = null): Boolean {
            return (notice ?: getCurrentNotice()).starred
        }

        fun toggleStarredNotice(notice: Notice? = null) {
            val n = notice ?: getCurrentNotice()
            n.starred = !n.starred
            if (n.starred) starredNotices.add(n)
            else starredNotices.remove(starredNotices.find{it.id == n.id})
        }

        fun clearStarredNotices(){
            Log.i("SDO", "cleared starred notices")
            // TODO implement
        }

        fun clearSwipeHistory(){
            Log.i("SDO", "cleared swipe history")
            // TODO implement
        }
    }
}