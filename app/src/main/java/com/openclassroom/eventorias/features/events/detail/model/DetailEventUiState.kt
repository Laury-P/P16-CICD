package com.openclassroom.eventorias.features.events.detail.model

sealed interface DetailEventUiState {
    data object Loading : DetailEventUiState
    data class Success (val eventDetail : DetailEventUiModel) : DetailEventUiState
    data class Error (val error: Throwable) : DetailEventUiState
}