package com.openclassroom.eventorias.core.data.model

data class EventDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val dateTime: String = "",
    val location: String = "",
    val photoUrl: String = "",
    val promoterId: String = ""
)
