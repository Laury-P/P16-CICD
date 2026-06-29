package com.openclassroom.eventorias.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.NotificationRepository
import com.openclassroom.eventorias.features.profile.usecases.GetUserInfoUseCase
import com.openclassroom.eventorias.features.profile.usecases.UpdateAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) :
    ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Result<Unit>>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _uploadingAvatarState = MutableStateFlow<AvatarUploadingState>(AvatarUploadingState.Idle)
    val uploadingAvatarState = _uploadingAvatarState.asStateFlow()

    val notificationState : StateFlow<Boolean> = notificationRepository.isSubscribedToAllNotification
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val uiState: StateFlow<UiState> = getUserInfoUseCase()
        .map { result ->
            result.fold(
                onSuccess = { user -> UiState.Success(user) },
                onFailure = { exception -> UiState.Error(exception.message ?: "Unknown error") }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Idle
        )

    fun logOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess { _logoutEvent.emit(Result.success(Unit)) }
                .onFailure { e ->  _logoutEvent.emit(Result.failure(e)) }
        }
    }

    fun updateAvatar(newUri: Uri) {
        val oldUri = (uiState.value as? UiState.Success)?.user?.avatar

        viewModelScope.launch {
            _uploadingAvatarState.value = AvatarUploadingState.Uploading
            updateAvatarUseCase(newUri, oldUri)
                .onSuccess { _uploadingAvatarState.value = AvatarUploadingState.Success }
                .onFailure { _uploadingAvatarState.value = AvatarUploadingState.Error(it.message ?: "Unknown error") }
        }
    }

    fun toggleNotification(newStatus: Boolean) {
        viewModelScope.launch {
            notificationRepository.toggleAllNotification(newStatus)
        }
    }
}

sealed interface UiState {
    data object Idle : UiState
    data class Success(val user: User) : UiState
    data class Error(val error: String) : UiState
}

sealed interface AvatarUploadingState {
    data object Idle : AvatarUploadingState
    data object Uploading : AvatarUploadingState
    data object Success : AvatarUploadingState
    data class Error(val error : String) : AvatarUploadingState
}