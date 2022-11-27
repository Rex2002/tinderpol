package de.dhbw.tinderpol

import android.content.Context
import java.net.URL

class SDO {
    companion object {

        var currentImageUrlNr = 0;
        var images = arrayOf<String>("https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063552", "https://ws-public.interpol.int/notices/v1/red/1972-538/images/53063554","https://ws-public.interpol.int/notices/v1/red/2022-77917/images/62602991")

        fun performWork(c : Context) {
            // perform some network work
            val response = URL("https://ws-public.interpol.int/notices/v1/red").openStream().bufferedReader().use { it.readText() }
            println(response)

        }

        fun getNextImageURL(): String {
            val image = images.get(currentImageUrlNr);
            currentImageUrlNr = (currentImageUrlNr + 1) % images.size;
            return image
        }
    }
}