package de.dhbw.tinderpol.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

@Database(entities = [
    Notice::class,
    Charge::class,
    NoticeCharge::class,
    NoticeImage::class,
    NoticeLanguage::class,
    NoticeNationality::class
                     ], version = 9)
abstract class TinderPolDatabase : RoomDatabase() {
    abstract fun getDao(): TinderPolDao
}