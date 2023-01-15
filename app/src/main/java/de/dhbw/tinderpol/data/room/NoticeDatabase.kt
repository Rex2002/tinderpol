package de.dhbw.tinderpol.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Notice::class], version = 1)
abstract class NoticeDatabase : RoomDatabase() {
    abstract fun noticeDao(): NoticeDao
}