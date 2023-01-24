package de.dhbw.tinderpol.data.room

import androidx.room.*
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

@Dao
interface TinderPolDao {

    //Insert methods
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNotices(vararg notices: Notice)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguages( vararg languages: NoticeLanguage)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNationalities(vararg nationalities: NoticeNationality)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertImages(vararg images: NoticeImage)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertChargeMaps(vararg chargeMaps: NoticeCharge)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCharges(vararg charges: Charge)

    //Get methods
    @Query("SELECT * FROM notice")
    fun getAllNotices(): List<Notice>

    @Query("SELECT id FROM notice")
    fun getAllNoticeIds(): List<String>

    @Query("SELECT nationality FROM noticeNationality WHERE noticeId IS :id")
    fun getNationalitiesByNoticeId(id: String): List<String>

    @Query("SELECT image FROM noticeImage WHERE noticeId IS :id")
    fun getImagesByNoticeId(id: String): List<String>

    @Query("SELECT language FROM noticeLanguage WHERE noticeId IS :id")
    fun getLanguagesByNoticeId(id: String): List<String>

    @Transaction
    @Query("SELECT * FROM notice")
    fun getNoticesWithLists(): List<NoticeWithLists>

    //Update methods
    @Update
    fun update(vararg notices: Notice)

    //Delete methods
    @Delete
    fun deleteNotices(vararg notices: Notice)

    @Query("DELETE FROM notice WHERE id IN (:ids)")
    fun deleteById(vararg ids: String)
}
