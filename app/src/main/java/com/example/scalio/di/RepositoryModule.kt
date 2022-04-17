package com.example.scalio.di

import com.example.scalio.repository.UsersRepository
import com.example.scalio.repository.UsersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(usersRepositoryImpl: UsersRepositoryImpl): UsersRepository
}
