package com.openclassroom.eventorias.features.event.eventDetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.openclassroom.eventorias.core.domain.model.Event
import com.openclassroom.eventorias.util.fakes.FakeAuthRepository
import com.openclassroom.eventorias.util.fakes.FakeEventRepository
import com.openclassroom.eventorias.util.fakes.FakeUserRepository
import com.openclassroom.eventorias.features.events.detail.EventDetailViewModel
import com.openclassroom.eventorias.features.events.detail.model.DetailEventUiState
import com.openclassroom.eventorias.features.events.usecases.GetEventDetailUseCase
import com.openclassroom.eventorias.features.events.usecases.SetUserParticipationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository

    private lateinit var getEventDetailUseCase: GetEventDetailUseCase
    private lateinit var setUserParticipationUseCase: SetUserParticipationUseCase

    private lateinit var viewModel: EventDetailViewModel

    private val targetEventId = "event_456"
    private val currentUserId = "user_123"

    @BeforeEach // Version JUnit 5
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeEventRepository = FakeEventRepository()
        fakeUserRepository = FakeUserRepository()
        fakeAuthRepository = FakeAuthRepository()

        getEventDetailUseCase =
            GetEventDetailUseCase(fakeEventRepository, fakeUserRepository, fakeAuthRepository)
        setUserParticipationUseCase =
            SetUserParticipationUseCase(fakeAuthRepository, fakeEventRepository)

        fakeAuthRepository.currentUserId = currentUserId

        // Un faux événement de base pour le init{}
        val fakeEvent = Event(
            id = targetEventId,
            title = "Tech Party",
            promoterId = "promoter_789",
            description = "",
            photoUrl = "",
            dateTime = LocalDateTime.now(),
            location = ""
        )
        fakeEventRepository.emitEvents(listOf(fakeEvent))
        fakeEventRepository.emitParticipants(targetEventId, listOf(currentUserId, "other_user"))

        val savedStateHandle = SavedStateHandle(mapOf("eventId" to targetEventId))

        viewModel = EventDetailViewModel(
            eventDetailUseCase = getEventDetailUseCase,
            setUserParticipationUseCase = setUserParticipationUseCase,
            savedStateHandle = savedStateHandle
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewmodel initializes with valid id, uiState becomes Success`() = runTest {
        // 2. ACT & ASSERT :
        viewModel.uiState.test {
            var currentState = awaitItem()

            if (currentState is DetailEventUiState.Loading) {
                testDispatcher.scheduler.advanceUntilIdle()
                currentState = awaitItem()
            }

            assertTrue(currentState is DetailEventUiState.Success)
            val successState = currentState as DetailEventUiState.Success

            assertEquals("Tech Party", successState.eventDetail.event.title)
            assertEquals(true, successState.eventDetail.isUserParticipating)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when usecase fails on init, uiState becomes Error`() = runTest {
        // 1. ARRANGE :
        fakeEventRepository.emitEvents(emptyList())

        val errorViewModel = EventDetailViewModel(
            eventDetailUseCase = getEventDetailUseCase,
            setUserParticipationUseCase = setUserParticipationUseCase,
            savedStateHandle = SavedStateHandle(mapOf("eventId" to targetEventId))
        )

        // 2. ACT & ASSERT
        errorViewModel.uiState.test {
            var currentState = awaitItem()

            if (currentState is DetailEventUiState.Loading) {
                testDispatcher.scheduler.advanceUntilIdle()
                currentState = awaitItem()
            }

            assertTrue(currentState is DetailEventUiState.Error)
            val errorState = currentState as DetailEventUiState.Error
            assertTrue(errorState.error is NoSuchElementException)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when eventId is missing in savedStateHandle, uiState becomes Error immediately`() =
        runTest {
            // 1. ARRANGE :
            val emptySavedStateHandle = SavedStateHandle()

            val missingIdViewModel = EventDetailViewModel(
                eventDetailUseCase = getEventDetailUseCase,
                setUserParticipationUseCase = setUserParticipationUseCase,
                savedStateHandle = emptySavedStateHandle
            )

            // 2. ACT & ASSERT
            missingIdViewModel.uiState.test {
                val currentState = awaitItem()

                assertTrue(currentState is DetailEventUiState.Error)
                val errorState = currentState as DetailEventUiState.Error
                assertTrue(errorState.error is IllegalArgumentException)
                assertEquals("No Id found", errorState.error.message)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when participation fails, emits ShowError effect`() = runTest {
        fakeEventRepository.shouldParticipationUpdateReturnFailure = true

        viewModel.effect.test {
            viewModel.setUserParticipation(newStatus = true, eventId = targetEventId)

            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is EventDetailViewModel.DetailEffect.ShowError)
            assertEquals(
                "Firebase Error",
                (effect as EventDetailViewModel.DetailEffect.ShowError).message
            )

            cancelAndIgnoreRemainingEvents()
        }
    }
}