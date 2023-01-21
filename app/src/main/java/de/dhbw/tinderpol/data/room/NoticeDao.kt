package de.dhbw.tinderpol.data.room

import androidx.room.*
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

@Dao
interface NoticeDao {

    //Insert methods
    @Insert
    fun insertNotices(vararg notices: Notice)

    @Insert
    fun insertLanguages( vararg languages: NoticeLanguageMap)

    @Insert
    fun insertNationalities(vararg nationalities: NoticeNationalityMap)

    @Insert
    fun insertImages(vararg images: NoticeImageMap)

    @Insert
    fun insertChargeMaps(vararg chargeMaps: NoticeChargeMap)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCharges(vararg charges: Charge)

    //Get methods
    @Query("SELECT * FROM notice")
    fun getAllNotices(): List<Notice>

    @Query("SELECT nationality FROM noticeNationalityMap WHERE noticeId IS :id")
    fun getNationalitiesByNoticeId(id: String): List<String>

    @Query("SELECT image FROM noticeImageMap WHERE noticeId IS :id")
    fun getImagesByNoticeId(id: String): List<String>

    @Query("SELECT chargeId FROM noticeChargeMap WHERE noticeId IS :id")
    fun getChargeIdsByNoticeId(id: String): List<String>

    @Query("SELECT * FROM charge WHERE id IN (:ids)")
    fun getChargesByIds(ids: List<Int>): List<Charge>

    @Query("SELECT language FROM noticeLanguageMap WHERE noticeId IS :id")
    fun getLanguagesByNoticeId(id: String): List<String>

    @Query("SELECT * FROM notice WHERE starred")
    fun getStarredNotices(): MutableList<Notice>

    @Transaction
    @Query("SELECT * FROM notice")
    fun getNoticesWithLists(): List<NoticeWithLists>

    //TODO add methods for filtered get

    //Delete methods
    @Delete
    fun delete(notice: Notice)

    @Query("DELETE FROM notice")
    fun deleteAllNotices()

    @Query("DELETE FROM charge")
    fun deleteAllCharges()

    @Query("DELETE FROM noticeImageMap")
    fun deleteAllImages()

    @Query("DELETE FROM noticeChargeMap")
    fun deleteAllChargeMaps()

    @Query("DELETE FROM noticeLanguageMap")
    fun deleteAllLanguages()

    @Query("DELETE FROM noticeNationalityMap")
    fun deleteAllNationalities()
}