package com.openclassroom.eventorias.features.profile

import android.net.Uri
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.features.profile.usecases.GetUserInfoUseCase
import com.openclassroom.eventorias.features.profile.usecases.UpdateAvatarUseCase
import com.openclassroom.eventorias.util.fakes.FakeAuthRepository
import com.openclassroom.eventorias.util.fakes.FakeUserRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeNotificationRepository: FakeNotificationRepository
    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var getUserInfoUseCase: GetUserInfoUseCase
    private lateinit var updateAvatarUseCase: UpdateAvatarUseCase

    private lateinit var viewModel: ProfileViewModel

    private val mockUri: Uri = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeAuthRepository = FakeAuthRepository()
        fakeUserRepository = FakeUserRepository()
        fakeNotificationRepository = FakeNotificationRepository()

        getUserInfoUseCase = GetUserInfoUseCase(fakeUserRepository, fakeAuthRepository)
        updateAvatarUseCase = UpdateAvatarUseCase(fakeUserRepository, fakeAuthRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Tests sur l'état de l'UI (uiState) ---

    @Test
    fun `init should load user profile when logged in`() = runTest {
        // GIVEN
        val user = User(id = "user_123", firstname = "Alice", lastname = "Merveille")
        fakeAuthRepository.currentUserId = "user_123"
        fakeUserRepository.addUser(user)

        // WHEN
        viewModel = ProfileViewModel(getUserInfoUseCase, updateAvatarUseCase, fakeAuthRepository, fakeNotificationRepository)

        backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {  }
        }

        // THEN
        assertEquals(UiState.Success(user), viewModel.uiState.value)
    }

    @Test
    fun `init should emit Error state when user is not logged in`() = runTest {
        // GIVEN
        fakeAuthRepository.currentUserId = null

        // WHEN
        viewModel = ProfileViewModel(getUserInfoUseCase, updateAvatarUseCase, fakeAuthRepository, fakeNotificationRepository)

        backgroundScope.launch(testDispatcher){
            viewModel.uiState.collect{}
        }

        // THEN
        assertTrue(viewModel.uiState.value is UiState.Error)
        assertEquals("User not logged in", (viewModel.uiState.value as UiState.Error).error)
    }

    // --- Tests sur la déconnexion (logOut) ---

    @Test
    fun `logOut success should emit success event`() = runTest {
        // GIVEN
        viewModel = ProfileViewModel(getUserInfoUseCase, updateAvatarUseCase, fakeAuthRepository, fakeNotificationRepository)

        val events = mutableListOf<Result<Unit>>()
        val collectJob = launch(testDispatcher) {
            viewModel.logoutEvent.collect { events.add(it) }
        }

        // WHEN
        viewModel.logOut()

        // THEN
        assertTrue(events.isNotEmpty())
        assertTrue(events.first().isSuccess)

        collectJob.cancel()
    }

    // --- Tests sur les notifications ---

    @Test
    fun `toggleNotification should forward new status to repository`() = runTest {
        // GIVEN
        viewModel = ProfileViewModel(getUserInfoUseCase, updateAvatarUseCase, fakeAuthRepository, fakeNotificationRepository)

        // WHEN
        viewModel.toggleNotification(true)

        // THEN
        assertEquals(true, fakeNotificationRepository.lastToggledStatus)
    }

    // --- Tests sur l'Avatar ---

    @Test
    fun `updateAvatar success should change state to Success`() = runTest {
        // GIVEN
        fakeAuthRepository.currentUserId = "user_123"
        viewModel = ProfileViewModel(getUserInfoUseCase, updateAvatarUseCase, fakeAuthRepository, fakeNotificationRepository)

        // WHEN
        viewModel.updateAvatar(mockUri)

        // THEN
        assertEquals(AvatarUploadingState.Success, viewModel.uploadingAvatarState.value)
    }

    // --- Fake Repository pour la gestion des notifications ---
    private class FakeNotificationRepository : com.openclassroom.eventorias.core.domain.repository.NotificationRepository {
        val _isSubscribed = MutableStateFlow(false)
        override val isSubscribedToAllNotification = _isSubscribed

        var lastToggledStatus: Boolean? = null

        override suspend fun toggleAllNotification(isSubscribed: Boolean): Result<Unit> {
            lastToggledStatus = isSubscribed
            _isSubscribed.value = isSubscribed
            return Result.success(Unit)
        }
    }
}