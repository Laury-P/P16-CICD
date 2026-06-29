package com.openclassroom.eventorias.features.auth

import app.cash.turbine.test
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import com.openclassroom.eventorias.util.CoroutinesTestExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class AuthViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: AuthViewModel

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        viewModel = AuthViewModel(userRepository)
    }

    @Test
    fun `initial state should be Idle`() {
        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Nested
    inner class CheckNewUser {

        @Test
        fun `when user does not exist should emit NewUser`() = runTest {
            // Arrange
            val uid = "user_123"
            coEvery { userRepository.getUserById(uid) } returns null

            // Act
            viewModel.checkNewUser(uid)
            advanceUntilIdle()

            // Assert
            assertEquals(UiState.NewUser, viewModel.uiState.value)
            coVerify(exactly = 1) { userRepository.getUserById(uid) }
        }

        @Test
        fun `when user exists should emit UserReady`() = runTest {
            // Arrange
            val uid = "user_123"
            val mockUser = mockk<User>()
            coEvery { userRepository.getUserById(uid) } returns mockUser

            // Act
            viewModel.checkNewUser(uid)
            advanceUntilIdle()

            // Assert
            assertEquals(UiState.UserReady, viewModel.uiState.value)
            coVerify(exactly = 1) { userRepository.getUserById(uid) }
        }
    }

    @Nested
    inner class AddNewUser {

        private val testUser = User(
            id = "123",
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@emaiL.com"
        )

        @Test
        fun `when addUser succeeds should emit Loading then UserReady`() = runTest {
            // Arrange
            coEvery { userRepository.addUser(testUser) } returns Result.success(Unit)

            // 2. Act & Assert combinés grâce à Turbine
            viewModel.uiState.test {
                // Au démarrage, le StateFlow émet immédiatement sa valeur initiale
                assertEquals(UiState.Idle, awaitItem())

                // Act
                viewModel.addNewUser(testUser)

                assertEquals(UiState.Loading, awaitItem())

                assertEquals(UiState.UserReady, awaitItem())

                ensureAllEventsConsumed()
            }
            // Assert
            coVerify(exactly = 1) { userRepository.addUser(testUser) }
        }

        @Test
        fun `when addUser fails with message should emit Error with custom message`() = runTest {
            // Arrange
            val errorMessage = "Network Timeout"
            coEvery { userRepository.addUser(testUser) } returns Result.failure(
                Exception(
                    errorMessage
                )
            )

            // Act
            viewModel.addNewUser(testUser)
            advanceUntilIdle()

            // Assert
            val currentState = viewModel.uiState.value
            assertTrue(currentState is UiState.Error)
            assertEquals(errorMessage, (currentState as UiState.Error).message)
        }

        @Test
        fun `when addUser fails without message should emit Error with Unknown error`() = runTest {
            // Arrange
            coEvery { userRepository.addUser(testUser) } returns Result.failure(Exception())

            // Act
            viewModel.addNewUser(testUser)
            advanceUntilIdle()

            // Assert
            val currentState = viewModel.uiState.value
            assertTrue(currentState is UiState.Error)
            assertEquals("Unknown error", (currentState as UiState.Error).message)
        }
    }
}