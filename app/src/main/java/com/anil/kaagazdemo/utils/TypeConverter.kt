package com.anil.kaagazdemo.utils

import androidx.room.TypeConverter
import com.anil.kaagazdemo.database.ImageListEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {
    val gson = Gson()
    @TypeConverter
    fun imageToString(image: ImageListEntity): String {
        return gson.toJson(image)
    }

    @TypeConverter
    fun stringToImage(imageString: String): ImageListEntity {
        val objectType = object : TypeToken<ImageListEntity>() {}.type
        return gson.fromJson(imageString, objectType)
    }
}