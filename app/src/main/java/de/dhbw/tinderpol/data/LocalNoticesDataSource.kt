package de.dhbw.tinderpol.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import de.dhbw.tinderpol.data.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.function.Predicate

class LocalNoticesDataSource {
    companion object {
        lateinit var dao : NoticeDao
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun getAll(filter: Predicate<Notice>): List<Notice> {
        return withContext(Dispatchers.IO) {
            Log.i("API-Req", "Retrieving all local notices...")
            val noticesWithLists: List<NoticeWithLists> = dao.getNoticesWithLists()
            val notices: MutableList<Notice> = mutableListOf()
            noticesWithLists.forEach {
                it.notice.spokenLanguages = it.getLanguages()
                it.notice.imgs = it.getImages()
                it.notice.nationalities = it.getNationalities()
                it.notice.charges = it.charges
                if (filter.test(it.notice))
                    notices.add(it.notice)
            }
            return@withContext notices
        }
    }

    suspend fun insert(vararg notices: Notice){
        withContext(Dispatchers.IO) {
            dao.insertNotices(*notices)
            val languageMaps: ArrayList<NoticeLanguageMap> = ArrayList()
            val imageMaps: ArrayList<NoticeImageMap> = ArrayList()
            val nationalityMaps: ArrayList<NoticeNationalityMap> = ArrayList()
            val chargeMaps: ArrayList<NoticeChargeMap> = ArrayList()
            val charges: ArrayList<Charge> = ArrayList()
            notices.forEach {notice ->
                notice.spokenLanguages?.forEach {
                    languageMaps.add(NoticeLanguageMap(notice.id, it))
                }
                notice.imgs?.forEach{
                    imageMaps.add(NoticeImageMap(notice.id, it))
                }
                notice.nationalities?.forEach {
                    nationalityMaps.add(NoticeNationalityMap(notice.id, it))
                }
                notice.charges?.forEach {
                    val chargeId: String = it.country + it.charge
                    chargeMaps.add(NoticeChargeMap(notice.id, chargeId))
                    charges.add(it)
                }
            }
            dao.insertLanguages(*languageMaps.toTypedArray())
            dao.insertImages(*imageMaps.toTypedArray())
            dao.insertNationalities(*nationalityMaps.toTypedArray())
            dao.insertChargeMaps(*chargeMaps.toTypedArray())
            dao.insertCharges(*charges.toTypedArray())
        }
    }

    suspend fun updateAll(vararg notices: Notice){
        withContext(Dispatchers.IO){
            dao.update(*notices)
        }
    }
    suspend fun deleteAll(){
        withContext(Dispatchers.IO){
            dao.deleteAllCharges()
            dao.deleteAllImages()
            dao.deleteAllLanguages()
            dao.deleteAllChargeMaps()
            dao.deleteAllNotices()
            dao.deleteAllNationalities()
        }
    }
}