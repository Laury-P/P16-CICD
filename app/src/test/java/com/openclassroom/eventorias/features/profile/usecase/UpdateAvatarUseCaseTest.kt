package com.openclassroom.eventorias.features.profile.usecase

import android.net.Uri
import com.openclassroom.eventorias.features.profile.usecases.UpdateAvatarUseCase
import com.openclassroom.eventorias.util.fakes.FakeAuthRepository
import com.openclassroom.eventorias.util.fakes.FakeUserRepository
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateAvatarUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var updateAvatarUseCase: UpdateAvatarUseCase

    private val mockUri: Uri = mockk() // Pas besoin d'un vrai Uri Android en test unitaire grâce au mock

    @BeforeEach
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeAuthRepository = FakeAuthRepository()
        updateAvatarUseCase = UpdateAvatarUseCase(fakeUserRepository, fakeAuthRepository)
    }

    @Test
    fun `invoke when user is not logged in should return failure`() = runTest {
        // GIVEN
        fakeAuthRepository.currentUserId = null

        // WHEN
        val result = updateAvatarUseCase(mockUri, "old_url")

        // THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals("UserId not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke without old url should upload avatar and return success`() = runTest {
        // GIVEN
        fakeAuthRepository.currentUserId = "user_123"

        // WHEN
        val result = updateAvatarUseCase(mockUri, oldUrl = null)

        // THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke with old url should upload new avatar and delete old one`() = runTest {
        // GIVEN
        fakeAuthRepository.currentUserId = "user_123"

        // WHEN
        val result = updateAvatarUseCase(mockUri, oldUrl = "old_url")

        // THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke when old avatar deletion fails should still return success`() = runTest {
        // GIVEN
        fakeAuthRepository.currentUserId = "user_123"
        fakeUserRepository.shouldDeleteAvatarFail = true // On force l'échec de la suppression

        // WHEN
        val result = updateAvatarUseCase(mockUri, oldUrl = "old_url")

        // THEN - Doit réussir car le Use Case attrape l'erreur en interne
        assertTrue(result.isSuccess)
    }
}