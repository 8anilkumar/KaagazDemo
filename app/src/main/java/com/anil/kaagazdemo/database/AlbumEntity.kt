package com.anil.kaagazdemo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IMAGE_TABLE")
data class AlbumEntity (val imageListEntity: ImageListEntity,val albumName: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
