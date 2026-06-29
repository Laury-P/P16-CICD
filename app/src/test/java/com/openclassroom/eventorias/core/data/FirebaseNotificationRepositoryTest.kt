package com.openclassroom.eventorias.core.data

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.openclassroom.eventorias.core.data.repository.FirebaseNotificationRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class FirebaseNotificationRepositoryTest {


    private val firebaseMessaging: FirebaseMessaging = mockk()
    private val settingsStorage: SettingsStorage = mockk(relaxed = true)
    private val mockTask: Task<Void> = mockk()


    private lateinit var repository: FirebaseNotificationRepository

    @BeforeEach
    fun setUp() {
        repository = FirebaseNotificationRepository(firebaseMessaging, settingsStorage)

        every { mockTask.isComplete } returns true
        every { mockTask.isCanceled } returns false
        every { mockTask.result } returns null
        every { mockTask.exception } returns null
    }

    @Test
    fun toggleAllNotification_whenTrue_shouldSubscribeToTopicAndSave() = runTest {
        // GIVEN
        every { firebaseMessaging.subscribeToTopic("all") } returns mockTask

        // WHEN
        val result = repository.toggleAllNotification(true)

        // THEN
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { firebaseMessaging.subscribeToTopic("all") }
        coVerify(exactly = 1) { settingsStorage.setNotificationsEnabled(true) }
    }

    @Test
    fun toggleAllNotification_whenFalse_shouldUnsubscribeFromTopicAndSave() = runTest {
        // GIVEN
        every { firebaseMessaging.unsubscribeFromTopic("all") } returns mockTask

        // WHEN
        val result = repository.toggleAllNotification(false)

        // THEN
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { firebaseMessaging.unsubscribeFromTopic("all") }
        coVerify(exactly = 1) { settingsStorage.setNotificationsEnabled(false) }
    }

    @Test
    fun toggleAllNotification_whenFirebaseFails_shouldReturnFailureResult() = runTest {

        val mockFailedTask: Task<Void> = mockk()
        val exception = RuntimeException("Firebase network error")
        every { mockFailedTask.isComplete } returns true
        every { mockFailedTask.isCanceled } returns false
        every { mockFailedTask.exception } returns exception
        every { mockFailedTask.result } throws exception

        every { firebaseMessaging.subscribeToTopic("all") } returns mockFailedTask

        // WHEN
        val result = repository.toggleAllNotification(true)

        // THEN
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { settingsStorage.setNotificationsEnabled(any()) }
    }
}