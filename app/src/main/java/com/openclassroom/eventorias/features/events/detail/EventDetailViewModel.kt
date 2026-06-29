package com.openclassroom.eventorias.features.events.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.eventorias.features.events.detail.model.DetailEventUiState
import com.openclassroom.eventorias.features.events.usecases.GetEventDetailUseCase
import com.openclassroom.eventorias.features.events.usecases.SetUserParticipationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    eventDetailUseCase: GetEventDetailUseCase,
    private val setUserParticipationUseCase: SetUserParticipationUseCase,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    sealed interface DetailEffect {
        data class ShowError(val message: String) : DetailEffect
    }

    private val _effect = Channel<DetailEffect>()
    val effect = _effect.receiveAsFlow()

    private val eventId: String? = savedStateHandle["eventId"]

    private val _uiState = MutableStateFlow<DetailEventUiState>(DetailEventUiState.Loading)
    val uiState: StateFlow<DetailEventUiState> = _uiState

    init {
        if (eventId != null) {
            viewModelScope.launch {
                eventDetailUseCase(eventId).collect { result ->
                    _uiState.value = result.fold(
                        onSuccess = { DetailEventUiState.Success(it) },
                        onFailure = { DetailEventUiState.Error(it) }
                    )
                }
            }
        } else {
            _uiState.value = DetailEventUiState.Error(
                IllegalArgumentException("No Id found")
            )
        }

    }

    fun setUserParticipation(newStatus: Boolean, eventId: String) {
        viewModelScope.launch {
            setUserParticipationUseCase(newStatus, eventId)
                .onFailure { exception ->
                    _effect.send(DetailEffect.ShowError(exception.message ?: "An error occurred"))
                }
        }
    }

}