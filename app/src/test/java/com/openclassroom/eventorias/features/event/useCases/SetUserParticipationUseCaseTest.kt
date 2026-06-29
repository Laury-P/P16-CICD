package com.openclassroom.eventorias.features.event.useCases

import com.openclassroom.eventorias.util.fakes.FakeAuthRepository
import com.openclassroom.eventorias.util.fakes.FakeEventRepository
import com.openclassroom.eventorias.features.events.usecases.SetUserParticipationUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SetUserParticipationUseCaseTest {
    private var fakeEventRepository: FakeEventRepository = FakeEventRepository()
    private var fakeAuthRepository: FakeAuthRepository = FakeAuthRepository()
    private var useCase: SetUserParticipationUseCase = SetUserParticipationUseCase(fakeAuthRepository, fakeEventRepository)

    @Test
    fun `when user is not logged in, returns failure with IllegalStateException`() = runTest {
        // 1. ARRANGE : L'ID utilisateur est null
        fakeAuthRepository.currentUserId = null

        // 2. ACT
        val result = useCase(newStatus = true, eventId = "event_123")

        // 3. ASSERT
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when user is logged in, forwards correct parameters to repository`() = runTest {
        // 1. ARRANGE
        fakeAuthRepository.currentUserId = "user_789"

        // 2. ACT
        val result = useCase(newStatus = true, eventId = "event_123")

        // 3. ASSERT
        assertTrue(result.isSuccess)
        assertEquals(true, fakeEventRepository.lastSavedStatus)
        assertEquals("user_789", fakeEventRepository.lastSavedUserId)
        assertEquals("event_123", fakeEventRepository.lastSavedEventId)
    }

    @Test
    fun `when repository fails, returns failure`() = runTest {
        // 1. ARRANGE
        fakeAuthRepository.currentUserId = "user_789"
        fakeEventRepository.shouldParticipationUpdateReturnFailure = true

        // 2. ACT
        val result = useCase(newStatus = false, eventId = "event_123")

        // 3. ASSERT
        assertTrue(result.isFailure)
        assertEquals("Firebase Error", result.exceptionOrNull()?.message)
    }
}