package com.openclassroom.eventorias.features.profile.usecase

import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.features.profile.usecases.GetUserInfoUseCase
import com.openclassroom.eventorias.util.fakes.FakeAuthRepository
import com.openclassroom.eventorias.util.fakes.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserInfoUseCaseTest {

    // 1. Déclaration de nos Fakes (Remplacer par le nom exact de vos classes Fakes existantes)
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository

    private lateinit var getUserInfoUseCase: GetUserInfoUseCase

    @BeforeEach
    fun setUp() {
        // Initialisation de vos Fakes
        fakeUserRepository = FakeUserRepository()
        fakeAuthRepository = FakeAuthRepository()

        getUserInfoUseCase = GetUserInfoUseCase(fakeUserRepository, fakeAuthRepository)
    }

    @Test
    fun `invoke when user is not logged in should return failure`() = runTest {
        // GIVEN - L'utilisateur n'est pas connecté
        fakeAuthRepository.currentUserId = null

        // WHEN - On appelle le Use Case
        val result = getUserInfoUseCase().first()

        // THEN - On vérifie que c'est un échec avec la bonne exception
        assertTrue(result.isFailure)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke when user is logged in but profile not found should return failure`() = runTest {
        // GIVEN - Connecté, mais aucune donnée utilisateur dans le UserRepository
        fakeAuthRepository.currentUserId = "user_123"

        // WHEN
        val result = getUserInfoUseCase().first()

        // THEN
        assertTrue(result.isFailure)
        assertEquals("User info not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke when user exists should return success with user data`() = runTest {
        // GIVEN - L'utilisateur est connecté et ses données existent
        val expectedUser = User(id = "user_123", firstname = "Alice", lastname = "Merveille")
        fakeAuthRepository.currentUserId = "user_123"
        fakeUserRepository.addUser(expectedUser)

        // WHEN
        val result = getUserInfoUseCase().first()

        // THEN
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
    }
}