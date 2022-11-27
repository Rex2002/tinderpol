package de.dhbw.tinderpol

import android.content.Context
import java.net.URL

class SDO {
    companion object {

        fun performWork(c : Context) {
            // perform some network work
            val response = URL("https://ws-public.interpol.int/notices/v1/red").openStream().bufferedReader().use { it.readText() }
            println(response)

        }
    }
}