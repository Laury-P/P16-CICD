package com.openclassroom.eventorias.features.events.usecases

import com.openclassroom.eventorias.core.domain.model.Event
import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.EventRepository
import com.openclassroom.eventorias.features.events.add.NewUiEvent
import jakarta.inject.Inject
import java.time.LocalDateTime

class AddEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newEvent: NewUiEvent): Result<Unit> = runCatching {
        val eventId = eventRepository.generateNewId()

        val promoterId = authRepository.getUserId()
            ?: throw (IllegalStateException("User not logged in"))

        val downloadUrl: String = if (newEvent.pictureUri != null) {
            eventRepository.uploadEventPhoto(eventId, newEvent.pictureUri)
                .getOrThrow()
        } else ""

        val dateTime = LocalDateTime.of(newEvent.date, newEvent.time)

        val event = Event(
            id = eventId,
            title = newEvent.title,
            description = newEvent.description,
            dateTime = dateTime,
            location = newEvent.address,
            category = newEvent.category,
            photoUrl = downloadUrl,
            promoterId = promoterId
        )
        eventRepository.addEvent(event)
    }
}