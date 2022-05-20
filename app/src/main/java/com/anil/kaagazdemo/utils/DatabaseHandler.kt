package com.anil.kaagazdemo.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anil.kaagazdemo.database.AlbumEntity
import com.anil.kaagazdemo.TypeConverter

@Database(entities = [AlbumEntity::class], exportSchema = false, version = 1)
@TypeConverters(TypeConverter::class)
abstract class DatabaseHandler : RoomDatabase() {
    abstract fun imageInterface(): ImageDao?

    companion object {
        private const val DBNAME = "myvideo"
        private const val DBPDFNAME = "mypdf"
        private val instanse: DatabaseHandler? = null
    }

}