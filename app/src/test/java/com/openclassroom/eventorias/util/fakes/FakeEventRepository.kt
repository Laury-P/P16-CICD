package com.openclassroom.eventorias.util.fakes

import android.net.Uri
import com.openclassroom.eventorias.core.domain.model.Event
import com.openclassroom.eventorias.core.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID


class FakeEventRepository : EventRepository {

    private val events = MutableStateFlow<List<Event>>(emptyList())
    private val participants = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    fun emitEvents(newEvents: List<Event>) {
        events.value = newEvents
    }

    fun emitParticipants(eventId: String, userIds: List<String>) {
        participants.value += (eventId to userIds)
    }

    override fun getListEvent(): Flow<List<Event>> {
        return events
    }

    override fun getEventById(id: String): Flow<Event?> {
        return events.map { list -> list.find { it.id == id } }
    }

    override fun getParticipantsList(eventId: String): Flow<List<String>> {
        return participants.map { map -> map[eventId] ?: emptyList() }
    }

    var lastSavedStatus: Boolean? = null
    var lastSavedEventId: String? = null
    var lastSavedUserId: String? = null
    var shouldParticipationUpdateReturnFailure = false

    override suspend fun setParticipationStatus(
        newStatus: Boolean,
        userId: String,
        eventId: String
    ): Result<Unit> {
        if (shouldParticipationUpdateReturnFailure) {
            return Result.failure(Exception("Firebase Error"))
        }
        lastSavedStatus = newStatus
        lastSavedUserId = userId
        lastSavedEventId = eventId

        return Result.success(Unit)
    }

    var shouldAddEventFailed = false

    override suspend fun addEvent(event: Event): Result<Unit> {
        return if (shouldAddEventFailed) {
            Result.failure(Exception("Firestore Firestore crash"))
        } else {
            events.value += event
            Result.success(Unit)
        }
    }

    override fun generateNewId(): String {
        return UUID.randomUUID().toString()
    }

    var shouldUploadPhotoFailed = false

    override suspend fun uploadEventPhoto(eventId: String, imageUri: Uri): Result<String> {
        return if (shouldUploadPhotoFailed) {
            Result.failure(Exception("Firebase Storage crash"))
        } else Result.success("pictureUrl")
    }
}
