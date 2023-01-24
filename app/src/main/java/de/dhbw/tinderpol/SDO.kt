package de.dhbw.tinderpol

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import de.dhbw.tinderpol.data.Country
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.data.NoticeRepository
import java.lang.reflect.Type
import java.util.function.Consumer


class SDO {
    companion object {
        private var countries: HashMap<String, Country>? = null
        private const val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        private var currentNoticeIndex = 0
        private var notices : List<Notice> = listOf()
        private val emptyNotice = Notice("empty", imgs = listOf(noImg))
        var onUpdate: Consumer<Notice>? = null
        private var isListeningToUpdates = false
        var starredNotices: MutableList<Notice> = mutableListOf()

        /**
         * Gets notices stored in Room and updates Room from backend on the first call of the day (in the background)
         */
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun syncNotices(sharedPref: SharedPreferences?, forceRemoteSync: Boolean = false) {
            Log.i("SDO", "Syncing Notices...")
            if (!isListeningToUpdates) {
                NoticeRepository.listenToUpdates { update(it) }
                isListeningToUpdates = true
            }
            NoticeRepository.syncNotices(sharedPref, forceRemoteSync)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun update(newNotices: List<Notice>) {
            Log.i("SDO", "Notices updated...")
            notices = newNotices
            onUpdate?.accept(getCurrentNotice())
        }
        private class CountriesDeserializer: JsonDeserializer<Any> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Any? {
                var res: Any? = null
                val str = json!!.asJsonPrimitive.asString
                res = try {
                    str.toDouble()
                } catch (e: java.lang.Exception) {
                    str
                }
                return res
            }
        }

        fun loadCountriesData(res: Resources) {
            val text = res.openRawResource(R.raw.countries).bufferedReader().use { it.readText() }
            val builder = GsonBuilder()
            builder.registerTypeAdapter(Any::class.java, CountriesDeserializer())
            val gson = builder.create()
            val objectListType = object : TypeToken<HashMap<String, Country>?>() {}.type
            val obj: HashMap<String, Country> = gson.fromJson(text, objectListType)
            countries = obj
            Log.i("Countries", countries.toString())
        }

        fun getLatOfCountry(countryID: String): Double? {
            return countries?.get(countryID)?.lat
        }

        fun getLongOfCountry(countryID: String): Double? {
            return countries?.get(countryID)?.long
        }

        fun getNameOfCountry(countryID: String): String? {
            return countries?.get(countryID)?.name
        }

        fun getCountry(countryID: String?): Country? {
            return countries?.get(countryID)
        }
        suspend fun persistStarredNotices() {
            Log.i("SDO", "saving current starred notices to Room")
            NoticeRepository.updateStatus(*starredNotices.toTypedArray())
        }

        fun listenToUpdates(callback: Consumer<Notice>?) {
            onUpdate = callback
        }

        fun initStarredNotices() {
            starredNotices = notices.filter { it.starred }.toMutableList()
            Log.i("SDO", "initialized starredNotices list")
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

        fun toggleStarredNotice(n: Notice? = null) {
            val notice = n ?: getCurrentNotice()
            notice.starred = !notice.starred
            if (notice.starred) starredNotices.add(notice)
            else starredNotices.remove(starredNotices.find{it.id == notice.id})
        }

        suspend fun clearStarredNotices(){
            starredNotices.forEach { it.starred = false }
            persistStarredNotices()
            initStarredNotices()
            Log.i("SDO", "cleared starred notices")
        }

        fun clearSwipeHistory(){
            Log.i("SDO", "cleared swipe history")
            // TODO implement
        }

    }
}