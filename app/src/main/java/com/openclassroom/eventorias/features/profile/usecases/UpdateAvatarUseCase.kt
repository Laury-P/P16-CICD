package com.openclassroom.eventorias.features.profile.usecases

import android.net.Uri
import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import jakarta.inject.Inject

class UpdateAvatarUseCase @Inject constructor(
    private val userRepository: UserRepository, private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(newUri: Uri, oldUrl: String?): Result<Unit> = runCatching {
        val userId = authRepository.getUserId()
            ?: throw IllegalStateException("UserId not found")

        userRepository.uploadAvatarPhoto(userId, newUri).getOrThrow()

        oldUrl?.let {
            try {
                userRepository.deleteAvatarByUrl(oldUrl)
            } catch (e: Exception) {
                println("Log: Old avatar wasn't deleted: ${e.message}")
            }
        }
    }
}