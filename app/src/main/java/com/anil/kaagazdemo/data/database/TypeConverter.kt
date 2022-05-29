package com.anil.kaagazdemo.data.database

import androidx.room.TypeConverter
import com.anil.kaagazdemo.model.ImageEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {
    private val gson = Gson()
    @TypeConverter
    fun imageToString(image: List<ImageEntity>): String {
        return gson.toJson(image)
    }

    @TypeConverter
    fun stringToImage(imageString: String): List<ImageEntity> {
        val objectType = object : TypeToken<List<ImageEntity>>() {}.type
        return gson.fromJson(imageString, objectType)
    }
}