package com.openclassroom.eventorias.features.event.eventList

import app.cash.turbine.test
import com.openclassroom.eventorias.core.domain.model.Event
import com.openclassroom.eventorias.core.domain.model.EventCategory
import com.openclassroom.eventorias.features.events.eventList.EventListViewModel
import com.openclassroom.eventorias.features.events.eventList.model.ListEventUiModel
import com.openclassroom.eventorias.features.events.eventList.model.ListEventUiState
import com.openclassroom.eventorias.features.events.usecases.GetUiEventListUseCase
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EventListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetUiEventListUseCase = mockk()
    private lateinit var viewModel: EventListViewModel

    private val testEvents = listOf(
        createMockUiEvent("Concert de Rock", EventCategory.MUSIC, LocalDate.of(2026, 6, 25)),
        createMockUiEvent("Expo Peinture", EventCategory.ART, LocalDate.of(2026, 6, 25)),
        createMockUiEvent("Match de Foot", EventCategory.SPORTS, LocalDate.of(2026, 7, 1))
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { useCase() } returns flowOf(Result.success(testEvents))
        viewModel = EventListViewModel(useCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `init - states are empty or null`() = runTest {
        assertEquals("", viewModel.searchQuery.value)
        assertEquals(null, viewModel.selectedCategory.value)
        assertEquals(null, viewModel.selectedDate.value)
    }

    @Test
    fun `given viewModel, when interacting with search, then updates searchQuery correctly`() = runTest {

        assertEquals("", viewModel.searchQuery.value)

        viewModel.setSearchQuery("Concert")
        assertEquals("Concert", viewModel.searchQuery.value)

        viewModel.setSearchQuery("")
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `setSearchQuery - filters list by title ignoring case`() = runTest {
        viewModel.uiState.test {
            // Given : initial state
            assertEquals(ListEventUiState.Loading, awaitItem())

            val initialSuccess = awaitItem() as ListEventUiState.Success
            assertEquals(3, initialSuccess.listEvent.size)

            // When
            viewModel.setSearchQuery("rock")

            advanceTimeBy(201)

            // Then
            val filteredSuccess = awaitItem() as ListEventUiState.Success
            assertEquals(1, filteredSuccess.listEvent.size)
            assertEquals("Concert de Rock", filteredSuccess.listEvent.first().event.title)
        }
    }

    @Test
    fun `setCategoryFilter - filters list by category`() = runTest {
        viewModel.uiState.test {
            // Given
            awaitItem() // Loading
            awaitItem() // Liste complète

            // When
            viewModel.setCategoryFilter(EventCategory.SPORTS)

            // Then
            val successState = awaitItem() as ListEventUiState.Success
            assertEquals(1, successState.listEvent.size)
            assertEquals(EventCategory.SPORTS, successState.listEvent.first().event.category)
        }
    }

    @Test
    fun `setDateFilter - filters list by local date`() = runTest {
        viewModel.uiState.test {
            //Given
            awaitItem() // Loading
            awaitItem() // Liste complète

            // When
            viewModel.setDateFilter(LocalDate.of(2026, 6, 25))

            // Then :  On a deux événements le 25 juin 2026 dans nos mocks
            val successState = awaitItem() as ListEventUiState.Success
            assertEquals(2, successState.listEvent.size)
        }
    }

    @Test
    fun `multiple filters - combines search, category and date`() = runTest {
        viewModel.uiState.test {
            // Given
            awaitItem() // Loading
            awaitItem() // Liste complète

            // When : On applique les 3 filtres en même temps
            viewModel.setSearchQuery("Concert")
            viewModel.setCategoryFilter(EventCategory.MUSIC)
            viewModel.setDateFilter(LocalDate.of(2026, 6, 25))

            advanceTimeBy(201) // Pour le debounce

            // Then
            val successState = awaitItem() as ListEventUiState.Success
            assertEquals(1, successState.listEvent.size)
            assertEquals("Concert de Rock", successState.listEvent.first().event.title)
        }
    }

    @Test
    fun `reset filter - clears selected date and updates list`() = runTest {
        viewModel.uiState.test {
            // Given : On filtre par date
            awaitItem() // Loading
            awaitItem() // Liste complète
            viewModel.setDateFilter(LocalDate.of(2026, 7, 1))
            val filteredState = awaitItem() as ListEventUiState.Success
            assertEquals(1, filteredState.listEvent.size)

            // When : On réinitialise à null
            viewModel.setDateFilter(null)

            // Then La liste redevient complète
            val resetState = awaitItem() as ListEventUiState.Success
            assertEquals(3, resetState.listEvent.size)
        }
    }

    @Test
    fun `usecase returns empty list - emits success state with empty list`() = runTest {
        // Given
        every { useCase() } returns flowOf(Result.success(emptyList()))
        val emptyViewModel = EventListViewModel(useCase)

        // When
        emptyViewModel.uiState.test {
            assertEquals(ListEventUiState.Loading, awaitItem()) // Loading

            // Then
            val successState = awaitItem() as ListEventUiState.Success
            assertTrue(successState.listEvent.isEmpty())
        }
    }

    @Test
    fun `search query matches nothing - emits success state with empty list`() = runTest {
        viewModel.uiState.test {
            // Given
            awaitItem() // Loading
            awaitItem() // Liste complète (3 éléments)

            // When
            viewModel.setSearchQuery("Zumba intergalactique")
            advanceTimeBy(201) // Pour le debounce

            // Then
            val successState = awaitItem() as ListEventUiState.Success
            assertTrue(successState.listEvent.isEmpty())
        }
    }

    @Test
    fun `usecase failure - emits error state`() = runTest {
        // Given On change le comportement du mock pour ce test précis
        every { useCase() } returns flowOf(Result.failure(Exception("Firebase Error")))
        val errorViewModel = EventListViewModel(useCase)

        errorViewModel.uiState.test {
            assertEquals(ListEventUiState.Loading, awaitItem())

            // Then
            val errorState = awaitItem() as ListEventUiState.Error
            assertEquals("Firebase Error", errorState.message)
        }
    }

    private fun createMockUiEvent(title: String, category: EventCategory, date: LocalDate): ListEventUiModel {
        val event = mockk<Event>()
        every { event.title } returns title
        every { event.category } returns category
        every { event.dateTime } returns date.atStartOfDay()

        val uiEvent = mockk<ListEventUiModel>()
        every { uiEvent.event } returns event
        return uiEvent
    }

}