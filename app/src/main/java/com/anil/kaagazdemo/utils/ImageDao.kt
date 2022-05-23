package com.anil.kaagazdemo.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anil.kaagazdemo.database.AlbumEntity

@Dao
interface ImageDao {

    @Query("SELECT * FROM IMAGE_TABLE")
    fun getAlbum(): List<AlbumEntity>

    @Insert
    fun addImageInAlbum(addAlbum: AlbumEntity)
}