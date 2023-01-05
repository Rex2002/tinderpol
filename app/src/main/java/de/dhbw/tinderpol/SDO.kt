package de.dhbw.tinderpol

import androidx.core.net.toUri
import de.dhbw.tinderpol.data.Notice
import java.net.URL
import java.util.*

class SDO {
    companion object {

        var currentNoticeNr = 0
        val notices : List<Notice> = listOf(
            Notice("2018/46058", "Lastname1", "Firstname1", Date(1959, 10, 22), listOf("RU"), listOf("https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063552".toUri()), charge="did something bad", birthPlace = "Sidney", birthCountry = "America"),
            Notice("2019/46058", "Lastname2", "Firstname2", Date(1959, 10, 22), listOf("DE"), listOf("https://ws-public.interpol.int/notices/v1/red/2018-46058/images/61071213".toUri()), charge="did something even worse"),
            Notice("2017/46058", "Lastname3", "Firstname3", Date(1959, 10, 22), listOf("MO"), listOf("https://ws-public.interpol.int/notices/v1/red/2022-77917/images/62602991".toUri()), charge="did the worst"))
        val starredNotices: MutableList<Notice> = mutableListOf()

        /**
         * synchronizes the notices stored in room with the API-available stuff ( in the background)
         */
        fun syncNotices() {
            // currently just testing that internet is working
            val response = URL("https://ws-public.interpol.int/notices/v1/red").openStream().bufferedReader().use { it.readText() }
            println(response)
        }

        fun getCurrentNotice() : Notice {
            return notices[currentNoticeNr]
        }

        fun getNextNotice() : Notice {
            // will probably be different later on, when room is connected
            currentNoticeNr = (currentNoticeNr + 1) % notices.size
            return notices[currentNoticeNr]
        }

        fun getPrevNotice() : Notice{
            currentNoticeNr = (currentNoticeNr + (notices.size - 1)) % notices.size
            return notices[currentNoticeNr]
        }

        fun getCurrentImageURL(): String {
            return notices[currentNoticeNr].imgURIs[0].toString()
        }

        fun toggleStarredNotice(id : String){
            if(starredNotices.any{it.id == id}){
                starredNotices.remove(starredNotices.find{it.id == id})
            }
            else{
                notices.find{it.id == id}?.let { starredNotices.add(it) }
            }
        }

        fun noticeIsStarred(id : String) : Boolean{
            return starredNotices.any{it.id == id}
        }
    }
}