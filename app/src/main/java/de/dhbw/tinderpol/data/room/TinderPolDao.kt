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
    @Query("SELECT id FROM notice")
    fun getAllNoticeIds(): List<String>

    @Transaction
    @Query("SELECT * FROM notice ORDER BY viewedAt ASC")
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
