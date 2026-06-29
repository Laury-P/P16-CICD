package com.openclassroom.eventorias.features.events.usecases

import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.EventRepository
import jakarta.inject.Inject

class SetUserParticipationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(newStatus: Boolean, eventId: String): Result<Unit> {
        val currentUserId = authRepository.getUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))

        return eventRepository.setParticipationStatus(newStatus, currentUserId, eventId)
    }
}