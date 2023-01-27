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
        private const val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        private val emptyNotice = Notice("empty", imgs = listOf(noImg))

        var onUpdate: Consumer<Notice>? = null
        private var isListeningToUpdates = false

        private var notices : List<Notice> = listOf()
        var starredNotices: MutableList<Notice> = mutableListOf()
        private var currentNoticeIndex = 0
        var currentImgIndex = 0

        private var countries: HashMap<String, Country>? = null

        // NOTICE-RELATED METHODS

        /**
         * Gets notices stored in Room and updates Room from backend on the first call of the day (in the background)
         */
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun syncNotices(sharedPref: SharedPreferences?, forceRemoteSync: Boolean = false) {
            Log.i("SDO", "Syncing Notices...")
            if (!isListeningToUpdates) {
                NoticeRepository.listenToUpdates { updateNoticesInSDO(it) }
                isListeningToUpdates = true
            }
            NoticeRepository.syncNotices(sharedPref, forceRemoteSync)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun updateNoticesInSDO(newNotices: List<Notice>) {
            notices = newNotices
            onUpdate?.accept(getCurrentNotice())
            Log.i("SDO", "Notices updated")
        }

        fun listenToUpdates(callback: Consumer<Notice>?) {
            onUpdate = callback
        }

        //methods that write to Room
        suspend fun persistStatus(sharedPref: SharedPreferences?) {
            persistStarredNotices()
            persistViewedNotices()
            if (sharedPref != null && notices.isNotEmpty()) {
                with(sharedPref.edit()) {
                    putString(R.string.current_noticeId_shared_prefs.toString(), notices[currentNoticeIndex].id)
                    this.apply()
                }
                Log.i("SDO", "saved current noticeId to shared preferences")
            }
        }

        private suspend fun persistStarredNotices() {
            NoticeRepository.updateStatus(*starredNotices.toTypedArray())
            Log.i("SDO", "saved current starred notices to Room")
        }

        private suspend fun persistViewedNotices() {
            val viewedNotices: List<Notice> =
                if (currentNoticeIndex+1<notices.size) notices.subList(0, currentNoticeIndex+1)
                else notices

            NoticeRepository.updateStatus(*viewedNotices.toTypedArray())
            Log.i("SDO", "saved viewed notices' status to Room")
        }

        // methods that edit SDO
        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun initialize(sharedPref: SharedPreferences?, forceRemoteSync: Boolean = false) {
            syncNotices(sharedPref, forceRemoteSync)
            initStarredNotices()
            initCurrentNoticeIndex(sharedPref)
        }

        private fun initStarredNotices() {
            starredNotices = notices.filter { it.starred }.toMutableList()
            Log.i("SDO", "initialized starredNotices list")
        }

        private fun initCurrentNoticeIndex(sharedPref: SharedPreferences?) {
            Log.i("SDO", "initializing currentNoticeIndex")
            if (sharedPref != null) {
                val id = sharedPref.getString(R.string.current_noticeId_shared_prefs.toString(), "")
                currentNoticeIndex = notices.indexOfFirst { it.id == id}
            }
            if (sharedPref == null || currentNoticeIndex == -1) {
                currentNoticeIndex = notices.indexOfFirst { it.viewedAt == Long.MAX_VALUE } -1
                //notices.indexOfLast {it.viewedAt < Long.MaxValue} would make more sense here but
                // that would check the entire list. This only checks as many notices as necessary.
            }
            if (currentNoticeIndex < 0) {
                currentNoticeIndex = 0
            }
            notices[currentNoticeIndex].viewedAt = System.currentTimeMillis()
            Log.i("SDO", "initialized currentNoticeIndex with $currentNoticeIndex")
        }

        fun toggleStarredNotice(n: Notice? = null) {
            val notice = n ?: getCurrentNotice()
            notice.starred = !notice.starred
            if (notice.starred) starredNotices.add(notice)
            else starredNotices.remove(starredNotices.find{it.id == notice.id})
        }

        // methods that correspond to button in settings
        suspend fun clearStarredNotices(){
            starredNotices.forEach { it.starred = false }
            persistStarredNotices()
            initStarredNotices()
            Log.i("SDO", "cleared starred notices")
        }

        suspend fun clearSwipeHistory(sharedPref: SharedPreferences?){
            Log.i("SDO", "clearing swipe history")
            val firstUnviewedIndex: Int = notices.indexOfFirst { it.viewedAt == Long.MAX_VALUE }
            val noticesToClear: List<Notice> =
                if (firstUnviewedIndex != -1) notices.subList(0, firstUnviewedIndex)
                else notices

            noticesToClear.forEach { it.viewedAt = Long.MAX_VALUE }
            NoticeRepository.updateStatus(*noticesToClear.toTypedArray())
            notices = notices.sortedBy { it.id }
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString(R.string.current_noticeId_shared_prefs.toString(), "")
                    apply()
                }
            }
            Log.i("SDO", "successfully cleared swipe history")
        }

        //methods that get data from SDO
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
            currentImgIndex = 0
            if (notices.isNotEmpty()){
                currentNoticeIndex++
                notices[currentNoticeIndex].viewedAt = System.currentTimeMillis()
            }

            if (currentNoticeIndex >= notices.size) {
                currentNoticeIndex = notices.size -1
                throw Exception("Reached last notice in local cache")
            }

            Log.i("Notice-Call", currentNoticeIndex.toString())
            if (notices.isNotEmpty())
                Log.i("Notice-Call", notices[currentNoticeIndex].toString())

            return getCurrentNotice()
        }

        fun getPrevNotice() : Notice {
            currentImgIndex = 0
            if (notices.isNotEmpty()) currentNoticeIndex--
            if (currentNoticeIndex < 0) {
                currentNoticeIndex = 0
                throw Exception("Already on first notice in local cache")
            }

            Log.i("Notice-Call", currentNoticeIndex.toString())
            if (notices.isNotEmpty())
                Log.i("Notice-Call", notices[currentNoticeIndex].toString())

            return getCurrentNotice()
        }

        fun noticesIsEmpty() : Boolean{
            return notices.isEmpty()
        }

        fun getImageURL(n: Notice? = null): String {
            val notice = n ?: getCurrentNotice()
            return if (notice.imgs == null || notice.imgs!!.isEmpty()) noImg
            else if (currentImgIndex >= notice.imgs!!.size) notice.imgs!!.last()
            else notice.imgs!![currentImgIndex]
        }

        fun getNextImageURL(n: Notice? = null): String {
            val notice = n ?: getCurrentNotice()
            if (notice.imgs != null && notice.imgs!!.size - 1 > currentImgIndex) currentImgIndex++
            return getImageURL(notice)
        }

        fun getPrevImageURL(n: Notice? = null): String {
            val notice = n ?: getCurrentNotice()
            if (notice.imgs != null && currentImgIndex > 0) currentImgIndex--
            return getImageURL(notice)
        }

        fun isNoticeStarred(notice: Notice? = null): Boolean {
            return (notice ?: getCurrentNotice()).starred
        }

        // COUNTRY-RELATED METHODS
        //TODO SDO seems cluttered. does it maybe make sense to split this into one CountrySDO and one NoticeSDO?

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
            Log.i("Countries", "Loaded countries data: $countries")
            //TODO @Jay review whether log adds value in this form: it's very long and it's showing static information
        }

        fun getCountry(countryID: String?): Country? {
            return countries?.get(countryID)
        }
    }
}