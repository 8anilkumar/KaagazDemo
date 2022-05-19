package com.anil.kaagazdemo.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anil.kaagazdemo.ImageEntity

@Database(entities = [ImageEntity::class], exportSchema = false, version = 1)

abstract class DatabaseHandler : RoomDatabase() {
    abstract fun imageInterface(): ImageDao?

    companion object {
        private const val DBNAME = "myvideo"
        private const val DBPDFNAME = "mypdf"
        private val instanse: DatabaseHandler? = null
    }
}