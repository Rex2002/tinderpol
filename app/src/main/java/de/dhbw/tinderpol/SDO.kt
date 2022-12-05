package de.dhbw.tinderpol

import android.content.Context
import androidx.core.net.toUri
import de.dhbw.tinderpol.data.Notice
import java.net.URL

class SDO {
    companion object {

        var currentNoticeNr = 0;
        val notices : List<Notice> = listOf(
            Notice("2018/46058", "Lastname1", "Firstname1", null, listOf("RU"), listOf("https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063552".toUri())),
            Notice("2019/46058", "Lastname2", "Firstname2", null, listOf("DE"), listOf("https://ws-public.interpol.int/notices/v1/red/2018-46058/images/61071213".toUri())),
            Notice("2017/46058", "Lastname3", "Firstname3", null, listOf("MO"), listOf("https://ws-public.interpol.int/notices/v1/red/2022-77917/images/62602991".toUri())))
        //var images = arrayOf<String>("https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063552", "https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063554","https://ws-public.interpol.int/notices/v1/red/2022-77917/images/62602991")

        fun performWork(c : Context) {
            // perform some network work
            val response = URL("https://ws-public.interpol.int/notices/v1/red").openStream().bufferedReader().use { it.readText() }
            println(response)
        }

        fun getCurrentNotice() : Notice {
            return notices[currentNoticeNr];
        }

        fun getNextNotice() : Notice {
            val notice = notices.get(currentNoticeNr);
            currentNoticeNr = (currentNoticeNr + 1) % notices.size
            return notice;
        }

        fun getCurrentImageURL(): String {
            return notices[currentNoticeNr].imgURIs[0].toString();
        }
    }
}