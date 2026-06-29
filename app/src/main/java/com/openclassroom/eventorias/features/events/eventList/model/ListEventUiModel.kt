package com.openclassroom.eventorias.features.events.eventList.model

import com.openclassroom.eventorias.core.domain.model.Event

data class ListEventUiModel(
    val event : Event,
    val promoterAvatarUrl : String
)
