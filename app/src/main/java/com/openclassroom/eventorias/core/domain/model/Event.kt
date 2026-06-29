package com.openclassroom.eventorias.core.domain.model

import java.time.LocalDateTime

data class Event(
    val id : String = "",
    val title : String = "",
    val description : String = "",
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val location : String = "",
    val category : EventCategory = EventCategory.DIVERSE,
    val photoUrl : String = "",
    val promoterId : String = "", // ID of the User that create the event.
)
