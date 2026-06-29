package com.openclassroom.eventorias.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel(){

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun checkNewUser(uid : String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(uid)
            _uiState.value = if(user == null) UiState.NewUser else UiState.UserReady
        }
    }

    fun addNewUser(user: User) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            userRepository.addUser(user)
                .onSuccess {
                    _uiState.value = UiState.UserReady
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }

        }
    }

}

sealed interface UiState {
    object Idle : UiState
    object NewUser : UiState
    object UserReady : UiState
    object Loading : UiState
    data class Error(val message: String) : UiState
}