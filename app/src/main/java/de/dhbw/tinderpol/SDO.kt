package de.dhbw.tinderpol

import android.util.Log
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.data.NoticeRepository
import kotlinx.coroutines.runBlocking

class SDO {
    companion object {
        private val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        private var currentNoticeIndex = 0
        private var notices : List<Notice> = listOf()
        var starredNotices: MutableList<Notice> = mutableListOf()

        /**
         * Gets notices stored in Room and updates Room API on the first call of the day (in the background)
         */
        fun loadNotices() {
            return runBlocking {
                notices = NoticeRepository.fetchAllNotices()
                initStarredNotices()
            }
        }

        private fun initStarredNotices() {
            starredNotices = notices.filter { it.starred }.toMutableList()
        }

        fun getCurrentNotice() : Notice {
            if (notices.size <= currentNoticeIndex) {
                // Realistically only the else branch will ever be used here, but it's checked just in case to prevent any bugs
                if (notices.isNotEmpty()) currentNoticeIndex = notices.size - 1
                else return Notice()
            }
            return notices[currentNoticeIndex]
        }

        fun getNextNotice() : Notice {
            if (notices.isNotEmpty())
                currentNoticeIndex++

            if (currentNoticeIndex == notices.size) {
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

        fun getCurrentImageURL(): String {
            val notice = getCurrentNotice()
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
    }
}