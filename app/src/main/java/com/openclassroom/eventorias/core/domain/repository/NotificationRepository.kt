package com.openclassroom.eventorias.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    val isSubscribedToAllNotification: Flow<Boolean>
    suspend fun toggleAllNotification(newStatus: Boolean): Result<Unit>
}