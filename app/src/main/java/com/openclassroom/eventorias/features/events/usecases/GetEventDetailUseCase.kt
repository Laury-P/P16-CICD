package com.openclassroom.eventorias.features.events.usecases

import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.EventRepository
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import com.openclassroom.eventorias.features.events.detail.model.DetailEventUiModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

class GetEventDetailUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(eventId: String): Flow<Result<DetailEventUiModel>> {
        val eventFlow = eventRepository.getEventById(eventId)
        val participantsFlow = eventRepository.getParticipantsList(eventId)
        val currentUserId = authRepository.getUserId()

        return combine(eventFlow, participantsFlow) { event, participantsList ->
            if (event == null) {
                return@combine Result.failure(
                    NoSuchElementException("Event $eventId not found")
                )
            }

            val promoter = userRepository.getUserById(event.promoterId)
            val promoterURL = promoter?.avatar ?: ""

            val isUserParticipating = participantsList.contains(currentUserId)
            val nbrOfParticipants = participantsList.size

            Result.success(
                DetailEventUiModel(
                    event = event,
                    promoterUrl = promoterURL,
                    nbrOfParticipants = nbrOfParticipants,
                    isUserParticipating = isUserParticipating
                )
            )
        }.catch { exception ->
            emit(Result.failure(exception))
        }
    }
}