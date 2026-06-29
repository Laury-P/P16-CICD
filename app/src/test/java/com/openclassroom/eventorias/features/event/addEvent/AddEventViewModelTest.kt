package com.openclassroom.eventorias.features.event.addEvent


import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.openclassroom.eventorias.core.domain.model.EventCategory
import com.openclassroom.eventorias.core.utils.isNetworkAvailable
import com.openclassroom.eventorias.features.events.add.AddEventViewModel
import com.openclassroom.eventorias.features.events.detail.FormEvent
import com.openclassroom.eventorias.features.events.detail.IsPublishing
import com.openclassroom.eventorias.features.events.detail.PublishError
import com.openclassroom.eventorias.features.events.usecases.AddEventUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime


@OptIn(ExperimentalCoroutinesApi::class)
class AddEventViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val addEventUseCase: AddEventUseCase = mockk()
    private lateinit var viewModel: AddEventViewModel
    private val mockContext: Context = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddEventViewModel(addEventUseCase)
        mockkStatic("com.openclassroom.eventorias.core.utils.NetworkCheckKt")
    }


    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `onAction FormEvents should update newEvent completely`() = runTest {
        val expectedDate = LocalDate.now()
        val expectedTime = LocalTime.now()
        val expectedUri = mockk<android.net.Uri>()

        viewModel.onAction(FormEvent.TitleChanged("Tech Event"))
        viewModel.onAction(FormEvent.DescriptionChanged("Description"))
        viewModel.onAction(FormEvent.AddressChanged("Paris"))
        viewModel.onAction(FormEvent.DateChanged(expectedDate))
        viewModel.onAction(FormEvent.TimeChanged(expectedTime))
        viewModel.onAction(FormEvent.CategoryChanged(EventCategory.ART))
        viewModel.onAction(FormEvent.PhotoSelected(expectedUri))

        val state = viewModel.newEvent.value
        assertEquals("Tech Event", state.title)
        assertEquals("Description", state.description)
        assertEquals("Paris", state.address)
        assertEquals(expectedDate, state.date)
        assertEquals(expectedTime, state.time)
        assertEquals(EventCategory.ART, state.category)
        assertEquals(expectedUri, state.pictureUri)
    }

    @Test
    fun `onAction OnSaveClicked should return NetworkError immediately if no internet`() = runTest {
        // Arrange
        every { isNetworkAvailable(mockContext) } returns false

        // Act
        viewModel.onAction(FormEvent.OnSaveClicked(mockContext))

        // Assert
        val state = viewModel.isPublishing.value
        assertTrue(state is IsPublishing.Error)
        assertEquals(PublishError.NetworkError, (state as IsPublishing.Error).error)
    }

    @Test
    fun `onAction OnSaveClicked should launch addEvent if internet is available`() = runTest {
        // Arrange
        every {isNetworkAvailable(mockContext) } returns true
        coEvery { addEventUseCase(any()) } returns Result.success(Unit)

        // Act
        viewModel.onAction(FormEvent.OnSaveClicked(mockContext))

        // Assert
        assertEquals(IsPublishing.Published, viewModel.isPublishing.value)
    }

    @Test
    fun `addEvent should map IllegalStateException to UserNotLoggedIn`() = runTest {
        // Arrange
        coEvery { addEventUseCase(any()) } returns Result.failure(IllegalStateException("No user"))

        // Act
        viewModel.addEvent()

        // Assert
        val state = viewModel.isPublishing.value
        assertTrue(state is IsPublishing.Error)
        assertEquals(PublishError.UserNotLoggedIn, (state as IsPublishing.Error).error)
    }

    @Test
    fun `addEvent should map FirebaseNetworkException to NetworkError`() = runTest {
        // Arrange
        val mockFirebaseException = mockk<FirebaseNetworkException>()
        coEvery { addEventUseCase(any()) } returns Result.failure(mockFirebaseException)

        // Act
        viewModel.addEvent()

        // Assert
        val state = viewModel.isPublishing.value
        assertTrue(state is IsPublishing.Error)
        assertEquals(PublishError.NetworkError, (state as IsPublishing.Error).error)
    }

    @Test
    fun `addEvent should map any other exception to UnknownError`() = runTest {
        // Arrange
        coEvery { addEventUseCase(any()) } returns Result.failure(RuntimeException("SQL or Firestore crash"))

        // Act
        viewModel.addEvent()

        // Assert
        val state = viewModel.isPublishing.value
        assertTrue(state is IsPublishing.Error)
        assertEquals(PublishError.UnknownError, (state as IsPublishing.Error).error)
    }
}