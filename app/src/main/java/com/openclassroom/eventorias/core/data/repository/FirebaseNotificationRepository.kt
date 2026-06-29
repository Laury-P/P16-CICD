package com.openclassroom.eventorias.core.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.openclassroom.eventorias.core.data.SettingsStorage
import com.openclassroom.eventorias.core.domain.repository.NotificationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FirebaseNotificationRepository @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
    private val settingsStorage: SettingsStorage) :
    NotificationRepository {

    override val isSubscribedToAllNotification: Flow<Boolean> = settingsStorage.isNotificationsEnabled

    override suspend fun toggleAllNotification(newStatus : Boolean): Result<Unit> = runCatching{
        if(newStatus) {
            firebaseMessaging.subscribeToTopic("all").await()
        } else {
            firebaseMessaging.unsubscribeFromTopic("all").await()
        }
        settingsStorage.setNotificationsEnabled(newStatus)
    }

}