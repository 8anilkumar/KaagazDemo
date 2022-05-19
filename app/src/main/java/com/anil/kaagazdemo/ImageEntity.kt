package com.anil.kaagazdemo

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IMAGE_TABLE")
data class ImageEntity (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,
    val imgPath:String? = null,
    val timeStamp:String? = null

   )
/*
albym- list
alum -list

 */