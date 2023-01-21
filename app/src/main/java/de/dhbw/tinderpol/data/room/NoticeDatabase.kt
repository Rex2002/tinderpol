package de.dhbw.tinderpol.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

@Database(entities = [
    Notice::class,
    Charge::class,
    NoticeChargeMap::class,
    NoticeImageMap::class,
    NoticeLanguageMap::class,
    NoticeNationalityMap::class
                     ], version = 2)
abstract class NoticeDatabase : RoomDatabase() {
    abstract fun noticeDao(): NoticeDao
}