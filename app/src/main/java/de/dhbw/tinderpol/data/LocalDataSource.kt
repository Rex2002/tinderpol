package de.dhbw.tinderpol.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import de.dhbw.tinderpol.data.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.function.Predicate

class LocalDataSource {
    companion object {
        lateinit var dao : TinderPolDao
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun getAll(filter: Predicate<Notice>): List<Notice> {
        return withContext(Dispatchers.IO) {
            Log.i("LocalDataSource", "Retrieving all local notices...")
            val noticesWithLists: List<NoticeWithLists> = dao.getNoticesWithLists()
            val notices: MutableList<Notice> = mutableListOf()
            noticesWithLists.forEach {
                it.notice.spokenLanguages = it.getLanguages()
                it.notice.imgs = it.getImages()
                it.notice.nationalities = it.getNationalities()
                it.notice.charges = it.getCharges()
                if (filter.test(it.notice))
                    notices.add(it.notice)
            }
            return@withContext notices
        }
    }

    suspend fun getAllNoticeIds(): List<String> {
        return withContext(Dispatchers.IO) {
            Log.i("LocalDataSource", "retrieving all noticeIds")
            return@withContext dao.getAllNoticeIds()
        }
    }

    suspend fun insert(vararg notices: Notice){
        withContext(Dispatchers.IO) {
            dao.insertNotices(*notices)
            val languageMaps: ArrayList<NoticeLanguage> = ArrayList()
            val imageMaps: ArrayList<NoticeImage> = ArrayList()
            val nationalityMaps: ArrayList<NoticeNationality> = ArrayList()
            val chargeMaps: ArrayList<NoticeCharge> = ArrayList()
            notices.forEach {notice ->
                notice.spokenLanguages?.forEach {
                    languageMaps.add(NoticeLanguage(notice.id, it))
                }
                notice.imgs?.forEach{
                    imageMaps.add(NoticeImage(notice.id, it))
                }
                notice.nationalities?.forEach {
                    nationalityMaps.add(NoticeNationality(notice.id, it))
                }
                notice.charges?.forEach {
                    val chargeId: String = it.country + it.charge
                    chargeMaps.add(NoticeCharge(notice.id, chargeId))
                }
            }
            dao.insertLanguages(*languageMaps.toTypedArray())
            dao.insertImages(*imageMaps.toTypedArray())
            dao.insertNationalities(*nationalityMaps.toTypedArray())
            dao.insertChargeMaps(*chargeMaps.toTypedArray())
        }
    }

    suspend fun updateAll(vararg notices: Notice){
        withContext(Dispatchers.IO){
            dao.update(*notices)
        }
    }

    fun deleteById(vararg ids: String) {
        dao.deleteById(*ids)
    }
}