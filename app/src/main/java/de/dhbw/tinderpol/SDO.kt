package de.dhbw.tinderpol

import android.util.Log
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.data.NoticeRepository
import kotlinx.coroutines.runBlocking

class SDO {
    companion object {
        val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        val emptyNotice = Notice("empty", imgs = listOf(noImg))
        var currentNoticeNr = 0
        var notices : List<Notice> = listOf()
        val starredNotices: MutableList<Notice> = mutableListOf()

        /**
         * synchronizes the notices stored in room with the API-available stuff ( in the background)
         */
        fun syncNotices() {
            return runBlocking {
                notices = NoticeRepository.fetchNotices()
            }
        }

        fun getCurrentNotice() : Notice {
            Log.i("API-Req", currentNoticeNr.toString())
            if (notices.isNotEmpty()) Log.i("API-Req", notices[currentNoticeNr].toString())
            if (notices.size <= currentNoticeNr) {
                // Realistically only the else branch will ever be used here, but we check just in case to prevent any bugs
                if (notices.isNotEmpty()) currentNoticeNr = notices.size - 1
                else return emptyNotice
            }
            return notices[currentNoticeNr]
        }

        fun getNextNotice() : Notice {
            // will probably be different later on, when room is connected
            currentNoticeNr = (currentNoticeNr + 1) % notices.size
            return getCurrentNotice()
        }

        fun getPrevNotice() : Notice{
            currentNoticeNr = (currentNoticeNr + (notices.size - 1)) % notices.size
            return getCurrentNotice()
        }

        fun getCurrentImageURL(): String {
            val notice = getCurrentNotice()
            return if (notice.imgs == null || notice.imgs.isEmpty()) noImg
            else notice.imgs[0]
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