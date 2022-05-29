package com.anil.kaagazdemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anil.kaagazdemo.utils.Constants.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class AlbumEntity (val imageListEntity: List<ImageEntity>, val albumName: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
