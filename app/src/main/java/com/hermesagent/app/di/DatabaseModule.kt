package com.hermesagent.app.di

import android.content.Context
import androidx.room.Room
import com.hermesagent.app.data.db.AppDatabase
import com.hermesagent.app.data.db.ChatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "hermes.db").build()

    @Provides
    fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()
}