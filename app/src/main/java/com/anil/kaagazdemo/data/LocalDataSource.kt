package com.anil.kaagazdemo.data

import com.anil.kaagazdemo.data.database.ImageDao
import com.anil.kaagazdemo.model.AlbumEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val imageDao: ImageDao
) {
    fun readDatabase(): Flow<List<AlbumEntity>> {
        return imageDao.readAlbum()
    }
    fun insertRecipes(albumEntity: AlbumEntity) {
        imageDao.insertAlbum(albumEntity)
    }
}