package com.openclassroom.eventorias.core.di

import com.openclassroom.eventorias.core.data.repository.FirebaseAuthRepository
import com.openclassroom.eventorias.core.data.repository.FirebaseEventRepository
import com.openclassroom.eventorias.core.data.repository.FirebaseNotificationRepository
import com.openclassroom.eventorias.core.data.repository.FirebaseUserRepository
import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.EventRepository
import com.openclassroom.eventorias.core.domain.repository.NotificationRepository
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        firebaseUserRepository: FirebaseUserRepository
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        firebaseEventRepository: FirebaseEventRepository,
    ) : EventRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository,
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        firebaseNotificationRepository: FirebaseNotificationRepository
    ) : NotificationRepository
}