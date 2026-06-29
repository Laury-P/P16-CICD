package com.openclassroom.eventorias.features.profile.usecases

import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Result<User>> {
        val userID = authRepository.getUserId()
            ?: return flow { emit(Result.failure(IllegalStateException("User not logged in"))) }

        return userRepository.observeUserById(userID)
            .map { user ->
                if (user != null) Result.success(user)
                else Result.failure(Exception("User info not found"))
            }
            .catch { exception ->
                emit(Result.failure(exception))
            }
    }
}