package com.anil.kaagazdemo

import androidx.room.TypeConverter
import com.anil.kaagazdemo.database.ImageListEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TypeConverter {
    val gson = Gson()

    @TypeConverter
    fun recipeToString(recipe: ImageListEntity): String {
        return gson.toJson(recipe)
    }

    @TypeConverter
    fun stringToRecipe(recipeString: String): ImageListEntity {
        val objectType = object : TypeToken<ImageListEntity>() {}.type
        return gson.fromJson(recipeString, objectType)
    }
}
class TypeConverterList {
    val gson = Gson()

    @TypeConverter
    fun recipeToString(recipe: ImageListEntity): String {
        return gson.toJson(recipe)
    }

    @TypeConverter
    fun stringToRecipe(recipeString: String): ImageListEntity {
        val objectType = object : TypeToken<ImageListEntity>() {}.type
        return gson.fromJson(recipeString, objectType)
    }
}