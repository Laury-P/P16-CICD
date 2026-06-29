package com.openclassroom.eventorias.features.events.eventList.model


sealed interface ListEventUiState {
    data object Loading : ListEventUiState
    data class Success(val listEvent : List<ListEventUiModel>) : ListEventUiState
    data class Error(val message : String) : ListEventUiState
}