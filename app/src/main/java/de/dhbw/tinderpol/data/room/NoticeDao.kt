package de.dhbw.tinderpol.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoticeDao {
    //TODO figure out which methods are required by defining how db update is done

    @Insert
    fun insertAll(vararg notices: Notice)

    @Query("SELECT * FROM notice")
    fun getAll(): List<Notice>

    //TODO add methods for filtered get

    @Delete
    fun delete(notice: Notice)
}