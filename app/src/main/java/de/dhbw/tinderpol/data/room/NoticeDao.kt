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

    //Insert methods
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

    //Get methods
    @Query("SELECT id FROM charge WHERE country is :country AND charge is :charge")
    fun getChargeId(country: String, charge: String): Int

    @Query("SELECT * FROM notice")
    fun getAllNotices(): List<Notice>

    @Query("SELECT nationality FROM noticenationalitymap WHERE noticeId IS :id")
    fun getNationalitiesByNoticeId(id: String): List<String>

    @Query("SELECT image FROM noticeimagemap WHERE noticeId IS :id")
    fun getImagesByNoticeId(id: String): List<String>

    @Query("SELECT chargeId FROM noticechargemap WHERE noticeId IS :id")
    fun getChargeIdsByNoticeId(id: String): List<Int>

    @Query("SELECT * FROM charge WHERE id IN (:ids)")
    fun getChargesByIds(ids: List<Int>): List<Charge>

    @Query("SELECT language FROM noticelanguagemap WHERE noticeId IS :id")
    fun getLanguagesByNoticeId(id: String): List<String>

    @Query("SELECT * FROM notice WHERE starred")
    fun getStarredNotices(): MutableList<Notice>

    //TODO add methods for filtered get

    //Delete methods
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