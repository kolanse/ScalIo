package com.example.scalio.di

import android.content.Context
import com.example.scalio.data.local.AppDataBase
import com.example.scalio.data.local.dao.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase {
        return AppDataBase.getDatabase(context)
    }

    @Provides
    fun provideUserDao(appDataBase: AppDataBase): UsersDao {
        return appDataBase.usersDao()
    }
}
