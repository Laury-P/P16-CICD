package com.openclassroom.eventorias.features.events.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.openclassroom.eventorias.core.domain.model.EventCategory
import com.openclassroom.eventorias.core.utils.isNetworkAvailable
import com.openclassroom.eventorias.features.events.detail.FormEvent
import com.openclassroom.eventorias.features.events.detail.IsPublishing
import com.openclassroom.eventorias.features.events.detail.PublishError
import com.openclassroom.eventorias.features.events.usecases.AddEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@HiltViewModel
class AddEventViewModel @Inject constructor(private val addEventUseCase: AddEventUseCase): ViewModel() {

    private val _newEvent = MutableStateFlow(NewUiEvent())
    val newEvent: StateFlow<NewUiEvent> = _newEvent

    private val _isPublishing = MutableStateFlow<IsPublishing>(IsPublishing.Idle)
    val isPublishing = _isPublishing.asStateFlow()

    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.DateChanged -> {
                _newEvent.value = _newEvent.value.copy(date = formEvent.date)
            }

            is FormEvent.AddressChanged -> {
                _newEvent.value = _newEvent.value.copy(address = formEvent.address)
            }

            is FormEvent.DescriptionChanged -> {
                _newEvent.value = _newEvent.value.copy(description = formEvent.description)
            }

            is FormEvent.PhotoSelected -> {
                _newEvent.value = _newEvent.value.copy(pictureUri = formEvent.uri)
            }

            is FormEvent.TimeChanged -> {
                _newEvent.value = _newEvent.value.copy(time = formEvent.time)
            }

            is FormEvent.TitleChanged -> {
                _newEvent.value = _newEvent.value.copy(title = formEvent.title)
            }

            is FormEvent.CategoryChanged -> {
                _newEvent.value = _newEvent.value.copy(category = formEvent.category)
            }

            is FormEvent.OnSaveClicked -> {
                if(!isNetworkAvailable(formEvent.context)) {
                    _isPublishing.value = IsPublishing.Error(PublishError.NetworkError)
                } else addEvent()
            }
        }
    }

    fun addEvent() {
        viewModelScope.launch{
            _isPublishing.value = IsPublishing.Publishing
            addEventUseCase(newEvent.value)
                .onSuccess { _isPublishing.value = IsPublishing.Published }
                .onFailure { exception ->
                    val publishError = when(exception) {
                        is IllegalStateException -> PublishError.UserNotLoggedIn
                        is FirebaseNetworkException -> PublishError.NetworkError
                        else -> PublishError.UnknownError
                    }
                    _isPublishing.value = IsPublishing.Error(publishError)
                }
        }
    }
}

data class NewUiEvent(
    val title: String = "",
    val description: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val address: String = "",
    val category: EventCategory = EventCategory.DIVERSE,
    val pictureUri: Uri? = null,
)