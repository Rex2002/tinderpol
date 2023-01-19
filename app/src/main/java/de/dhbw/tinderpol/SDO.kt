package de.dhbw.tinderpol

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.data.NoticeRepository
import java.util.function.Consumer

class SDO {
    companion object {
        val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        val emptyNotice = Notice("empty", imgs = listOf(noImg))
        var currentNoticeNr = 0
        var notices : List<Notice> = listOf()
        var onUpdate: Consumer<Notice>? = null
        val starredNotices: MutableList<Notice> = mutableListOf()

        /**
         * synchronizes the notices stored in room with the API-available stuff ( in the background)
         */
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun syncNotices() {
            notices = NoticeRepository.fetchNotices()
            onUpdate?.accept(getCurrentNotice())
        }

        fun onUpdate(callback: Consumer<Notice>?) {
            onUpdate = callback
        }

        fun getCurrentNotice() : Notice {
            Log.i("SDO", currentNoticeNr.toString())
            if (notices.isNotEmpty()) Log.i("SDO", notices[currentNoticeNr].toString())
            if (notices.size <= currentNoticeNr) {
                // Realistically only the else branch will ever be used here, but we check just in case to prevent any bugs
                if (notices.isNotEmpty()) currentNoticeNr = notices.size - 1
                else return emptyNotice
            }
            return notices[currentNoticeNr]
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
            // will probably be different later on, when room is connected
            if (notices.isNotEmpty()) currentNoticeNr = (currentNoticeNr + 1) % notices.size
            return getCurrentNotice()
        }

        fun getPrevNotice() : Notice{
            if (notices.isNotEmpty()) currentNoticeNr = (currentNoticeNr + (notices.size - 1)) % notices.size
            return getCurrentNotice()
        }

        fun getImageURL(n: Notice?  = null): String {
            val notice = n ?: getCurrentNotice()
            return if(notice.imgs == null || notice.imgs.isEmpty()) noImg
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