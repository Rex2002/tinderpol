package de.dhbw.tinderpol.data

import android.util.Log
import de.dhbw.tinderpol.data.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalNoticesDataSource {
    companion object {
        lateinit var dao : NoticeDao
    }

    suspend fun getAll(): List<Notice> {
        return withContext(Dispatchers.IO) {
            Log.i("API-Req", "Retrieving all local notices...")
            val notices: List<Notice> = dao.getAllNotices()
            Log.i("API-Req", "Amount of local notices: " + notices.size)

            notices.forEach {
                it.nationalities = dao.getNationalitiesByNoticeId(it.id)
                it.imgs = dao.getImagesByNoticeId(it.id)
                it.charges = dao.getChargesByIds(dao.getChargeIdsByNoticeId(it.id))
                it.spokenLanguages = dao.getLanguagesByNoticeId(it.id)
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
                    val chargeId: Int = dao.getChargeId(it.country, it.charge)
                    if (chargeId != null){
                        dao.insertChargeMaps(NoticeChargeMap(notice.id, chargeId))
                    } else {
                        dao.insertCharges(it)
                        dao.insertChargeMaps(NoticeChargeMap(notice.id, dao.getChargeId(it.country, it.charge)))
                    }
                    //TODO reduce db calls by making primary key of charge concatenation of country & charge
                }
            }
            dao.insertLanguages(*languageMaps.toTypedArray())
            dao.insertImages(*imageMaps.toTypedArray())
            dao.insertNationalities(*nationalityMaps.toTypedArray())
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