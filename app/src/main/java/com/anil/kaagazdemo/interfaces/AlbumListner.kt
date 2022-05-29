package com.anil.kaagazdemo.interfaces

import com.anil.kaagazdemo.model.ImageEntity

interface AlbumListner {
    fun albumListener (mutableList: List<ImageEntity>)
}