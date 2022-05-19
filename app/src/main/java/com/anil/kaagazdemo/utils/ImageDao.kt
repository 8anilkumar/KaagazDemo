package com.anil.kaagazdemo.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anil.kaagazdemo.ImageEntity

@Dao
interface ImageDao {

    @Query("SELECT * FROM IMAGE_TABLE")
    fun getAlbumb(): List<ImageEntity>

    @Insert
    fun addImageInAlbumb(videoResponse: Array<ImageEntity>)
}