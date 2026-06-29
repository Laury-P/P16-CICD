package com.openclassroom.eventorias.features.events.detail.model

import com.openclassroom.eventorias.core.domain.model.Event

data class DetailEventUiModel(
    val event: Event,
    val promoterUrl : String,
    val nbrOfParticipants : Int,
    val isUserParticipating : Boolean = false // TODO a implementer
)
