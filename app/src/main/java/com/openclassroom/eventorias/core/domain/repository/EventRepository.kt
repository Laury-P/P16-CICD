package com.openclassroom.eventorias.core.domain.repository

import android.net.Uri
import com.openclassroom.eventorias.core.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getListEvent() : Flow<List<Event>>

    fun getEventById(id: String) : Flow<Event?>

    suspend fun setParticipationStatus(newStatus : Boolean, userId : String, eventId : String) : Result<Unit>

    fun getParticipantsList(eventId : String) : Flow<List<String>>

    suspend fun addEvent(event: Event) : Result<Unit>

    fun generateNewId(): String

    suspend fun uploadEventPhoto(eventId : String, imageUri: Uri) : Result<String>
}