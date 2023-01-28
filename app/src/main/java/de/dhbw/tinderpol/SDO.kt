package de.dhbw.tinderpol

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import de.dhbw.tinderpol.data.Country
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.data.NoticeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.reflect.Type
import java.net.URL
import java.util.function.Consumer
import java.util.function.Predicate


class SDO {
    companion object {
        private const val noImg = "https://vectorified.com/images/unknown-avatar-icon-7.jpg"
        private val emptyNotice = Notice("0000/00000", imgs = listOf(noImg))

        var onUpdate: Consumer<Notice>? = null
        private var isListeningToUpdates = false

        var offlineFlag: Boolean = true

        private var notices : List<Notice> = listOf()
        var starredNotices: MutableList<Notice> = mutableListOf()
        var localImages: HashMap<String, MutableList<ByteArray>> = hashMapOf()
        private var currentNoticeIndex = 0
        var currentImgIndex = 0

        private var countries: HashMap<String, Country>? = null

        // NOTICE-RELATED METHODS

        /**
         * Gets notices stored in Room and updates Room from backend on the first call of the day (in the background)
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
                persistImages(context)
        }

        private suspend fun persistViewedNotices() {
            val viewedNotices: List<Notice> =
                if (currentNoticeIndex+1<notices.size) notices.subList(0, currentNoticeIndex+1)
                else notices

            NoticeRepository.updateStatus(*viewedNotices.toTypedArray())
            Log.i("SDO", "saved viewed notices' status to Room")
        }

        suspend fun persistStatus(notice: Notice) {
            NoticeRepository.updateStatus(notice)
            Log.i("SDO", "saved status of notice ${notice.id} to Room")
        }

        private suspend fun persistImages(context: Context){
            Log.i("SDO", "persisting notice images to disk")
            val lowerBound = if (currentNoticeIndex-10 >= 0) currentNoticeIndex-10 else 0
            val upperBound = if (currentNoticeIndex+50 < notices.size) currentNoticeIndex+50 else notices.size
            val toPersist: MutableSet<Notice> = notices.subList(lowerBound, upperBound).toMutableSet()
            toPersist.addAll(starredNotices)
            toPersist.add(emptyNotice)

            withContext(Dispatchers.IO){
                val noticeIds: MutableList<String> = mutableListOf()
                val dir = context.getDir("images", Context.MODE_PRIVATE)
                toPersist.forEach {notice ->
                    //notice.id[4] is always '/' which is not allowed in file names. Replaced by '_'.
                    val id: String = notice.id.substring(0,4) + "_" + notice.id.substring(5)

                    if (!notice.imgs.isNullOrEmpty()) {
                        notice.imgs!!.forEachIndexed {index, img ->
                            val file = File(dir,id + "_$index")

                            //skipping existing files because notices are never updated
                            if (!file.exists()) {
                                try {
                                    val imgURL = URL(img)
                                    val inputStream = BufferedInputStream(imgURL.openStream())
                                    val outputStream = ByteArrayOutputStream()
                                    val buffer = ByteArray(90000)
                                    var len: Int
                                    while (-1 != inputStream.read(buffer).also { len = it }) {
                                        outputStream.write(buffer, 0, len)
                                    }
                                    outputStream.close()
                                    inputStream.close()
                                    val response = outputStream.toByteArray()

                                    val fos = FileOutputStream(File(dir, id + "_$index"))
                                    fos.write(response)
                                    fos.close()
                                } catch (e: FileNotFoundException) {
                                    Log.e("SDO",
                                        "encountered FileNotFoundException while getting image $index for notice ${notice.id}"
                                    )
                                    //no exception handling because intended behaviour is that a file is simply not created when an exception occurs
                                }
                            }
                        }
                    }
                    noticeIds.add(id)
                }
                var fileNames: Array<String> = dir.list() ?: arrayOf()
                val numberFiles: Int = fileNames.size
                fileNames = fileNames.filter { !noticeIds.contains(it.substring(0,10)) }.toTypedArray()
                val numberOldFiles: Int = fileNames.size
                fileNames.forEach {
                    File(dir,it).delete()
                }
                Log.i("SDO", "images persisted successfully")
                Log.i("SDO", "images stored: " +  numberFiles.minus(numberOldFiles).toString())
                Log.i("SDO", "images deleted: $numberOldFiles")
            }
        }
        private suspend fun persistStarredNotices() {
            NoticeRepository.updateStatus(*starredNotices.toTypedArray())
            Log.i("SDO", "saved current starred notices to Room")
        }
        // methods that edit SDO
        suspend fun initialize(context: Context, forceRemoteSync: Boolean = false) {
            val res = context.resources
            val sharedPref = context.getSharedPreferences(res.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)

            syncNotices(context, forceRemoteSync)
            initStarredNotices()
            initCurrentNoticeIndex(sharedPref, res)
            if (offlineFlag)
                loadLocalImages(context)
        }

        private fun initStarredNotices() {
            starredNotices = notices.filter { it.starred }.toMutableList()
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
            withContext(Dispatchers.IO) {
                val dir = context.getDir("images", Context.MODE_PRIVATE)
                val fileNames: Array<String> = dir.list() ?: arrayOf()

                fileNames.forEach {
                    val image = ByteArray(90000)
                    val noticeId: String = it.substring(0,4) + "/" + it.substring(5,10)
                    val imgIndex: Int = it.substring(11).toInt()
                    val inputStream = FileInputStream(File(dir, it))
                    inputStream.read(image)
                    inputStream.close()
                    if (localImages[noticeId] == null)
                        localImages[noticeId] = mutableListOf()

                    localImages[noticeId]!!.add(imgIndex, image)
                }
            }
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
                Log.i("SDO", notices[currentNoticeIndex].toString())
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
            val notice = notices.find{it.id == id} ?: emptyNotice
            Log.i("SDO", notice.toString())
            return notice
        }

        fun getNextNotice() : Notice {
            currentImgIndex = 0
            if (notices.isNotEmpty()) currentNoticeIndex++

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

        fun getImage(context: Context, n: Notice? = null): Any {
            val notice = n ?: getCurrentNotice()
            if (offlineFlag) {
                Log.i("SDO", "using local image for notice ${notice.id}")
                if (localImages.isEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadLocalImages(context)
                    }
                }
                return if (localImages[notice.id]?.isNotEmpty() == true)
                    localImages[notice.id]?.get(currentImgIndex) ?: localImages["0000/00000"]!![0]
                else if (!localImages["0000/00000"].isNullOrEmpty())
                    localImages["0000/00000"]!![0]
                else ByteArray(1)
            }
            else {
                return if (notice.imgs == null || notice.imgs!!.isEmpty()) noImg
                else if (currentImgIndex >= notice.imgs!!.size) notice.imgs!!.last()
                else notice.imgs!![currentImgIndex]
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

        private class CountriesDeserializer: JsonDeserializer<Any> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Any? {
                val res: Any?
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
        }

        fun getCountry(countryID: String?): Country? {
            return countries?.get(countryID)
        }
    }
}