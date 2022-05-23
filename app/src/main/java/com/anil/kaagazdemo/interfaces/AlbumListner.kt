package com.anil.kaagazdemo.interfaces

import com.anil.kaagazdemo.database.ImageEntity

interface AlbumListner {
    fun albumListener (mutableList: List<ImageEntity>)
}