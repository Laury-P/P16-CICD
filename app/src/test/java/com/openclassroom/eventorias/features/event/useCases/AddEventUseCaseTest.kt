package com.openclassroom.eventorias.features.event.useCases

import android.net.Uri
import com.openclassroom.eventorias.core.domain.model.EventCategory
import com.openclassroom.eventorias.util.fakes.FakeAuthRepository // À adapter selon ton package
import com.openclassroom.eventorias.util.fakes.FakeEventRepository
import com.openclassroom.eventorias.features.events.add.NewUiEvent
import com.openclassroom.eventorias.features.events.usecases.AddEventUseCase
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AddEventUseCaseTest {

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var addEventUseCase: AddEventUseCase

    @BeforeEach
    fun setUp() {
        fakeEventRepository = FakeEventRepository()
        fakeAuthRepository = FakeAuthRepository()
        addEventUseCase = AddEventUseCase(fakeEventRepository, fakeAuthRepository)
    }

    @Test
    fun `invoke should return success and send correct event data to repository`() = runTest {
        // Arrange
        val expectedUserId = "user_123"
        fakeAuthRepository.currentUserId = expectedUserId

        val newEventDto = NewUiEvent(
            title = "Awesome Concert",
            description = "A great music event",
            date = LocalDate.of(2026, 7, 20),
            time = LocalTime.of(20, 0),
            address = "Paris",
            pictureUri = null,
            category = EventCategory.MUSIC
        )

        // Act
        val result = addEventUseCase(newEventDto)

        // Assert
        assertTrue(result.isSuccess)

        val savedEvents = fakeEventRepository.getListEvent().first()
        assertEquals(1, savedEvents.size)

        val finalEvent = savedEvents.first()

        assertEquals(newEventDto.title, finalEvent.title)
        assertEquals(newEventDto.description, finalEvent.description)
        assertEquals(newEventDto.address, finalEvent.location)
        assertEquals(newEventDto.category, finalEvent.category)
        assertEquals(expectedUserId, finalEvent.promoterId)
        assertEquals("", finalEvent.photoUrl)
        
        val expectedDateTime = LocalDateTime.of(newEventDto.date, newEventDto.time)
        assertEquals(expectedDateTime, finalEvent.dateTime)
    }

    @Test
    fun `invoke should return failure when user is not logged in`() = runTest {
        // Arrange
        fakeAuthRepository.currentUserId = null

        val newEvent = NewUiEvent(title = "Test Event")

        // Act
        val result = addEventUseCase(newEvent)

        // Assert
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User not logged in", exception?.message)
    }

    @Test
    fun `invoke should upload photo and return success when pictureUri is provided`() = runTest {
        // Arrange
        fakeAuthRepository.currentUserId = "user_123"
        fakeEventRepository.shouldUploadPhotoFailed = false

        val fakeUri = mockk<Uri>()
        val newEvent = NewUiEvent(
            title = "Concert with Banner",
            description = "Rock concert",
            date = LocalDate.of(2026, 8, 15),
            time = LocalTime.of(21, 30),
            address = "Lyon",
            pictureUri = fakeUri
        )

        // Act
        val result = addEventUseCase(newEvent)

        // Assert
        assertTrue(result.isSuccess)


        val savedEvents = fakeEventRepository.getListEvent().first()
        assertEquals(1, savedEvents.size)
        assertEquals("pictureUrl", savedEvents.first().photoUrl)
    }

    @Test
    fun `invoke should return failure when photo upload fails`() = runTest {
        // Arrange
        fakeAuthRepository.currentUserId = "user_123"
        fakeEventRepository.shouldUploadPhotoFailed = true


        val fakeUri = mockk<Uri>()
        val newEvent = NewUiEvent(
            title = "Event with Photo",
            pictureUri = fakeUri
        )

        // Act
        val result = addEventUseCase(newEvent)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Firebase Storage crash", result.exceptionOrNull()?.message)
    }


}