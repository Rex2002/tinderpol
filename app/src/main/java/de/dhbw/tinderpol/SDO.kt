package de.dhbw.tinderpol

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.dhbw.tinderpol.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.function.Consumer
import java.util.function.Predicate


class SDO {
    companion object {
        const val EMPTY_NOTICE_ID = "0000/00000"
        private const val NO_IMG_URL: String = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        private val emptyNotice = Notice(EMPTY_NOTICE_ID, imgs = listOf(NO_IMG_URL))

        var onUpdate: Consumer<Notice>? = null
        private var isListeningToUpdates = false

        var offlineFlag: Boolean = true
        private var localImagesChangedFlag = false


        var starredNotices: MutableList<Notice> = mutableListOf()
        var localImages: HashMap<String, MutableList<ByteArray>> = hashMapOf()
        var currentNoticeIndex = 0
        var currentImgIndex = 0
        private var notices : List<Notice> = listOf()
        private var countries: HashMap<String, Country>? = null

// NOTICE-RELATED METHODS
        private fun updateNoticesInSDO(newNotices: List<Notice>) {
            notices = newNotices
            onUpdate?.accept(getCurrentNotice())
            Log.i("SDO", "Notices updated")
        }

        fun listenToUpdates(callback: Consumer<Notice>?) {
            onUpdate = callback
        }

    //methods that write to Room
        suspend fun persistStatus(context: Context) {
            persistStarredNotices()
            persistViewedNotices()
            val sharedPref = context.getSharedPreferences(
                context.resources.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE
            )

            if (sharedPref != null && notices.isNotEmpty()) {
                with(sharedPref.edit()) {
                    putString(context.resources.getString(R.string.current_noticeId_shared_prefs), notices[currentNoticeIndex].id)
                    apply()
                }
                Log.i("SDO", "saved current noticeId to shared preferences")
            }
            if (!offlineFlag)
                persistCurrentImages(context)
        }

        suspend fun persistStatus(notice: Notice) {
            NoticeRepository.updateStatus(notice)
            Log.i("SDO", "saved status of notice ${notice.id} to Room")
        }

        private suspend fun persistViewedNotices() {
            val viewedNotices: List<Notice> =
                if (currentNoticeIndex+1<notices.size) notices.subList(0, currentNoticeIndex+1)
                else notices

            NoticeRepository.updateStatus(*viewedNotices.toTypedArray())
            Log.i("SDO", "saved viewed notices' status to Room")
        }

        private suspend fun persistStarredNotices() {
            NoticeRepository.updateStatus(*starredNotices.toTypedArray())
            Log.i("SDO", "saved current starred notices to Room")
        }

        private suspend fun persistCurrentImages(context: Context){
            Log.i("SDO", "persisting notice images to disk")
            val lowerBound = if (currentNoticeIndex - 10 >= 0) currentNoticeIndex - 10 else 0
            val upperBound = if (currentNoticeIndex + 50 < notices.size) currentNoticeIndex + 50 else notices.size
            val toPersist: MutableSet<Notice> = notices.subList(lowerBound, upperBound).toMutableSet()
            toPersist.addAll(starredNotices)
            toPersist.add(emptyNotice)

            val dir = context.getDir("images", Context.MODE_PRIVATE)
            Log.i("SDO-persist", "sending ${toPersist.size} notices to be persisted")
            LocalImageSource.persistImages(toPersist, dir)
            with(
                context.getSharedPreferences(
                    context.resources.getString(R.string.shared_preferences_file),
                    Context.MODE_PRIVATE
                ).edit()
            ) {
                putInt(context.resources.getString(R.string.offlineAnchor), currentNoticeIndex)
                apply()
            }
            localImagesChangedFlag = true
        }

    // methods that edit SDO

        /**
         * Gets notices stored in database and updates database from backend if last update is at least 24h ago
         */
        private suspend fun syncNotices(context: Context, forceRemoteSync: Boolean = false) {
            Log.i("SDO", "Syncing Notices...")
            if (!isListeningToUpdates) {
                NoticeRepository.listenToUpdates { updateNoticesInSDO(it) }
                isListeningToUpdates = true
            }
            val res = context.resources
            val sharedPref = context.getSharedPreferences(res.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            val filter: Predicate<Notice> = getCurrentFilter(sharedPref, res)
            NoticeRepository.syncNotices(context, filter, forceRemoteSync)
        }
        private fun getCurrentFilter(sharedPref: SharedPreferences?, res: Resources): Predicate<Notice> {
            val includeRed: Boolean = sharedPref?.getBoolean(res.getString(R.string.show_red_notices_shared_prefs), true) ?: true
            val includeYellow: Boolean = sharedPref?.getBoolean(res.getString(R.string.show_yellow_notices_shared_prefs), true) ?: true
            val includeUN: Boolean = sharedPref?.getBoolean(res.getString(R.string.show_UN_notices_shared_prefs), true) ?: true
            return Predicate {
                (it.type.equals("red") && includeRed) ||
                        (it.type.equals("yellow") && includeYellow) ||
                        (it.type.equals("un") && includeUN)
            }
        }

        private suspend fun initStarredNotices() {
            starredNotices = NoticeRepository.getStarredNotices().toMutableList()
            Log.i("SDO", "initialized starredNotices list")
        }

        private fun initCurrentNoticeIndex(sharedPref: SharedPreferences?, res: Resources) {
            Log.i("SDO", "initializing currentNoticeIndex")
            if (sharedPref != null) {
                val id = sharedPref.getString(res.getString(R.string.current_noticeId_shared_prefs), "")
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
            Log.i("SDO", "initialized currentNoticeIndex with $currentNoticeIndex")
        }

        private suspend fun loadLocalImages(context: Context) {
            val dir = context.getDir("images", Context.MODE_PRIVATE)
            LocalImageSource.loadImages(dir)
        }

        fun toggleStarredNotice(n: Notice? = null) {
            val notice = n ?: getCurrentNotice()
            notice.starred = !notice.starred
            if (notice.starred) starredNotices.add(notice)
            else starredNotices.remove(starredNotices.find{it.id == notice.id})
            Log.i("SDO", "toggled property starred of notice: $n")

        }

    // methods that correspond to button in settings
        suspend fun initialize(context: Context, forceRemoteSync: Boolean = false) {
            val res = context.resources
            val sharedPref = context.getSharedPreferences(res.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)

            syncNotices(context, forceRemoteSync)
            initStarredNotices()
            initCurrentNoticeIndex(sharedPref, res)
            if (offlineFlag)
                loadLocalImages(context)
            else
                persistCurrentImages(context)
        }

        suspend fun clearStarredNotices(){
            starredNotices.forEach { it.starred = false }
            persistStarredNotices()
            initStarredNotices()
            Log.i("SDO", "cleared starred notices")
        }

        suspend fun clearSwipeHistory(sharedPref: SharedPreferences?, res: Resources){
            Log.i("SDO", "clearing swipe history")
            val firstUnviewedIndex: Int = notices.indexOfFirst { it.viewedAt == Long.MAX_VALUE }
            val noticesToClear: List<Notice> =
                if (firstUnviewedIndex != -1) notices.subList(0, firstUnviewedIndex)
                else notices

            noticesToClear.forEach { it.viewedAt = Long.MAX_VALUE }
            NoticeRepository.updateStatus(*noticesToClear.toTypedArray())
            notices = notices.sortedWith(compareByDescending { it.id })
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString(res.getString(R.string.current_noticeId_shared_prefs), "none")
                    apply()
                }
            }
            Log.i("SDO", "successfully cleared swipe history")
        }

    //methods that get data from SDO
        fun getCurrentNotice() : Notice {
            if (notices.isNotEmpty() && currentNoticeIndex < notices.size) {
                notices[currentNoticeIndex].viewedAt = System.currentTimeMillis()
                Log.i("SDO-noticeCall", notices[currentNoticeIndex].toString())
            }
            if (notices.size <= currentNoticeIndex) {
                // Realistically only the else branch will ever be used here, but it's checked anyways to prevent any bugs
                if (notices.isNotEmpty()) currentNoticeIndex = notices.size - 1
                else return emptyNotice
            }
            return notices[currentNoticeIndex]
        }

        fun getNotice(id: String? = ""): Notice {
            if(id == "" || id == null){
                return getCurrentNotice()
            }
            val notice = notices.find{it.id == id} ?: starredNotices.find { it.id == id } ?: emptyNotice
            Log.i("SDO-noticeCall", "get notice from id called with: $notice")
            return notice
        }

        fun getNextNotice() : Notice {
            currentImgIndex = 0
            if (notices.isNotEmpty()) currentNoticeIndex++

            if (currentNoticeIndex >= notices.size) {
                currentNoticeIndex = notices.size -1
                throw Exception("Reached last notice in local cache")
            }

            Log.i("SDO-noticeCall", currentNoticeIndex.toString())
            if (notices.isNotEmpty())
                Log.i("SDO-noticeCall", notices[currentNoticeIndex].toString())

            return getCurrentNotice()
        }

        fun getPrevNotice() : Notice {
            currentImgIndex = 0
            if (notices.isNotEmpty()) currentNoticeIndex--
            if (currentNoticeIndex < 0) {
                currentNoticeIndex = 0
                throw Exception("Already on first notice in local cache")
            }

            Log.i("SDO-noticeCall", currentNoticeIndex.toString())
            if (notices.isNotEmpty())
                Log.i("SDO-noticeCall", notices[currentNoticeIndex].toString())

            return getCurrentNotice()
        }

        fun noticesIsEmpty() : Boolean{
            return notices.isEmpty()
        }

        fun getImage(context: Context, n: Notice? = null, imgNo: Int = -1): Any {
            val notice = n ?: getCurrentNotice()
            val imgIndex = if (imgNo == -1) currentImgIndex else imgNo
            if (offlineFlag) {
                Log.i("SDO-imageCall", "using local image for notice ${notice.id} and index $imgIndex")
                if (localImagesChangedFlag || localImages.isEmpty()) {
                    localImagesChangedFlag = false
                    CoroutineScope(Dispatchers.IO).launch {
                        loadLocalImages(context)
                    }
                }
                return if ((localImages[notice.id]?.isNotEmpty() == true) && localImages[notice.id]?.get(imgIndex) != null)
                    localImages[notice.id]!![imgIndex]
                else if (!localImages[EMPTY_NOTICE_ID].isNullOrEmpty()){
                    localImages[EMPTY_NOTICE_ID]!![0]
                    Log.i("SDO-imageCall", "used default image")
                }

                else ByteArray(1)
            }
            else {
                return if (notice.imgs == null || notice.imgs!!.isEmpty()) NO_IMG_URL
                else if (imgIndex >= notice.imgs!!.size) notice.imgs!!.last()
                else notice.imgs!![imgIndex]
            }
        }

        fun getNextImage(context: Context, n: Notice? = null): Any {
            val notice = n ?: getCurrentNotice()
            if (notice.imgs != null && notice.imgs!!.size - 1 > currentImgIndex) currentImgIndex++
            return getImage(context, notice)
        }

        fun getPrevImage(context: Context, n: Notice? = null): Any {
            val notice = n ?: getCurrentNotice()
            if (notice.imgs != null && currentImgIndex > 0) currentImgIndex--
            return getImage(context, notice)
        }
        fun isNoticeStarred(notice: Notice? = null): Boolean {
            return (notice ?: getCurrentNotice()).starred
        }

// COUNTRY-RELATED METHODS
        fun loadCountriesData(res: Resources) {
            val text = res.openRawResource(R.raw.countries).bufferedReader().use { it.readText() }
            val builder = GsonBuilder()
            builder.registerTypeAdapter(Any::class.java, CountriesDeserializer())
            val gson = builder.create()
            val objectListType = object : TypeToken<HashMap<String, Country>?>() {}.type
            val obj: HashMap<String, Country> = gson.fromJson(text, objectListType)
            countries = obj
            Log.i("SDO", "Loaded country data: $countries")
        }

        fun getCountry(countryID: String?): Country? {
            return countries?.get(countryID)
        }
    }
}