package com.openclassroom.eventorias.features.event.useCases

import app.cash.turbine.test
import com.openclassroom.eventorias.core.domain.model.Event
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.util.fakes.FakeAuthRepository
import com.openclassroom.eventorias.util.fakes.FakeEventRepository
import com.openclassroom.eventorias.util.fakes.FakeUserRepository
import com.openclassroom.eventorias.features.events.usecases.GetEventDetailUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GetEventDetailUseCaseTest {
    private var fakeEventRepository: FakeEventRepository = FakeEventRepository()
    private var fakeUserRepository: FakeUserRepository = FakeUserRepository()
    private var fakeAuthRepository: FakeAuthRepository = FakeAuthRepository()

    private var useCase: GetEventDetailUseCase = GetEventDetailUseCase(
        eventRepository = fakeEventRepository,
        userRepository = fakeUserRepository,
        authRepository = fakeAuthRepository
    )

    @Test
    fun `when event exists and user participates, returns success with correct mapping`() =
        runTest {
            // 1. ARRANGE
            val eventId = "event_123"
            val userId = "user_456"

            val fakeEvent = Event(
                id = eventId,
                title = "Tech Party",
                promoterId = "promoter_789",
                description = "",
                photoUrl = "",
                dateTime = LocalDateTime.now(),
                location = ""
            )
            val fakePromoter = User(id = "promoter_789", avatar = "url_de_l_avatar")

            fakeEventRepository.emitEvents(listOf(fakeEvent))
            fakeEventRepository.emitParticipants(eventId, listOf(userId, "other_user"))
            fakeUserRepository.addUser(fakePromoter)
            fakeAuthRepository.currentUserId = userId

            // 2. ACT & ASSERT
            useCase(eventId).test {

                val result = awaitItem()

                assertTrue(result.isSuccess)

                val uiModel = result.getOrNull()!!
                assertEquals("Tech Party", uiModel.event.title)
                assertEquals("url_de_l_avatar", uiModel.promoterUrl)
                assertEquals(2, uiModel.nbrOfParticipants) // 2 participants dans notre liste
                assertEquals(true, uiModel.isUserParticipating) // L'utilisateur connecté y est

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when event does not exist, returns failure with NoSuchElementException`() = runTest {
        // 1. ARRANGE :
        val eventId = "fake_event_id"
        fakeEventRepository.emitEvents(emptyList()) // Base vide

        // 2. ACT & ASSERT
        useCase(eventId).test {
            val result = awaitItem()

            assertTrue(result.isFailure)

            val exception = result.exceptionOrNull()
            assertTrue(exception is NoSuchElementException)
            assertEquals("Event fake_event_id not found", exception?.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when user is not participating, returns success with isUserParticipating as false`() =
        runTest {
            // 1. ARRANGE
            val eventId = "event_123"
            val userId = "my_user_id"

            val fakeEvent = Event(
                id = eventId,
                title = "Tech Party",
                promoterId = "promoter_789",
                description = "",
                photoUrl = "",
                dateTime = LocalDateTime.now(),
                location = ""
            )
            fakeEventRepository.emitEvents(listOf(fakeEvent))
            fakeEventRepository.emitParticipants(eventId, listOf("other_user_1", "other_user_2"))
            fakeAuthRepository.currentUserId = userId

            // 2. ACT & ASSERT
            useCase(eventId).test {
                val result = awaitItem()
                val uiModel = result.getOrNull()!!

                assertEquals(2, uiModel.nbrOfParticipants)
                assertEquals(false, uiModel.isUserParticipating)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when promoter does not exist, returns success with empty promoterUrl`() = runTest {
        // 1. ARRANGE
        val eventId = "event_123"
        val fakeEvent = Event(
            id = eventId,
            title = "Tech Party",
            promoterId = "unknown_promoter",
            description = "",
            photoUrl = "",
            dateTime = LocalDateTime.now(),
            location = ""
        )

        fakeEventRepository.emitEvents(listOf(fakeEvent))

        // 2. ACT & ASSERT
        useCase(eventId).test {
            val result = awaitItem()
            val uiModel = result.getOrNull()!!

            assertEquals("", uiModel.promoterUrl)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when participants list updates, useCase emits new updated data`() = runTest {
        // ARRANGEMENT INITIAL
        val eventId = "event_123"
        val userId = "user_456"
        val fakeEvent = Event(
            id = eventId,
            title = "Tech Party",
            promoterId = "p_789",
            description = "",
            photoUrl = "",
            dateTime = LocalDateTime.now(),
            location = ""
        )

        fakeEventRepository.emitEvents(listOf(fakeEvent))
        fakeEventRepository.emitParticipants(
            eventId,
            listOf("other_user")
        ) // 1 seul participant au début
        fakeAuthRepository.currentUserId = userId

        useCase(eventId).test {
            // 1. Premier élément : 1 seul participant, l'utilisateur ne participe pas
            val firstResult = awaitItem().getOrNull()!!
            assertEquals(1, firstResult.nbrOfParticipants)
            assertEquals(false, firstResult.isUserParticipating)

            // ACT :
            fakeEventRepository.emitParticipants(eventId, listOf("other_user", userId))

            // ASSERT
            val secondResult = awaitItem().getOrNull()!!
            assertEquals(2, secondResult.nbrOfParticipants)
            assertEquals(true, secondResult.isUserParticipating)

            cancelAndIgnoreRemainingEvents()
        }
    }
}


