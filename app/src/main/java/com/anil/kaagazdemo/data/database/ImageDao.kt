package com.anil.kaagazdemo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anil.kaagazdemo.model.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM album_table")
    fun readAlbum(): Flow<List<AlbumEntity>>

    @Insert
    fun insertAlbum(addAlbum: AlbumEntity)
}