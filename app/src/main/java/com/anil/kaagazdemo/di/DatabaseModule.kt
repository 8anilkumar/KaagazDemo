package com.anil.kaagazdemo.di

import android.content.Context
import androidx.room.Room
import com.anil.kaagazdemo.utils.Constants.DB_NAME
import com.anil.kaagazdemo.data.database.DatabaseHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        DatabaseHandler::class.java,
        DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: DatabaseHandler) = database.imageInterface()
}
