package de.dhbw.tinderpol.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

@Dao
interface NoticeDao {
    //TODO figure out which methods are required by defining how db update is done

    fun insertAll(vararg notices: Notice){
        insertNotices(*notices)
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
                val chargeId: Int = getChargeId(it.country, it.charge)
                if (chargeId != null){
                    insertChargeMaps(NoticeChargeMap(notice.id, chargeId))
                } else {
                    insertCharges(it)
                    insertChargeMaps(NoticeChargeMap(notice.id, getChargeId(it.country, it.charge)))
                }
                //TODO reduce db calls by making primary key of charge concatenation of country & charge
            }
        }
        insertLanguages(*languageMaps.toTypedArray())
        insertImages(*imageMaps.toTypedArray())
        insertNationalities(*nationalityMaps.toTypedArray())
    }

    fun deleteAll(){
        deleteAllCharges()
        deleteAllImages()
        deleteAllLanguages()
        deleteAllChargeMaps()
        deleteAllNotices()
        deleteAllNationalities()
    }

    @Insert
    fun insertNotices(vararg notices: Notice)

    @Insert
    fun insertLanguages(vararg languages: NoticeLanguageMap)

    @Insert
    fun insertNationalities(vararg nationalities: NoticeNationalityMap)

    @Insert
    fun insertImages(vararg images: NoticeImageMap)

    @Insert
    fun insertChargeMaps(vararg chargeMaps: NoticeChargeMap)

    @Insert
    fun insertCharges(vararg charges: Charge)

    @Query("SELECT id FROM charge WHERE country is :country AND charge is :charge")
    fun getChargeId(country: String, charge: String): Int

    @Query("SELECT * FROM notice")
    fun getAll(): List<Notice>

    //TODO add methods for filtered get

    @Delete
    fun delete(notice: Notice)

    @Query("DELETE FROM notice")
    fun deleteAllNotices()

    @Query("DELETE FROM charge")
    fun deleteAllCharges()

    @Query("DELETE FROM noticeimagemap")
    fun deleteAllImages()

    @Query("DELETE FROM noticechargemap")
    fun deleteAllChargeMaps()

    @Query("DELETE FROM noticelanguagemap")
    fun deleteAllLanguages()

    @Query("DELETE FROM noticenationalitymap")
    fun deleteAllNationalities()
}