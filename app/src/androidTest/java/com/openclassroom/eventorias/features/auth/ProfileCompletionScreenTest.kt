package com.openclassroom.eventorias.features.auth

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassroom.eventorias.R
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileCompletionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val saveButtonText = context.getString(R.string.save_button)

    @Test
    fun button_should_be_disabled_when_fields_are_empty() {
        // Arrange
        composeTestRule.setContent {
            ProfileCompletionScreen(
                displayName = "",
                email = "test@test.com",
                state = UiState.Idle,
                onSave = { _, _, _ -> }
            )
        }


        // Assert
        composeTestRule.onNodeWithText(saveButtonText).assertIsNotEnabled()
    }

    @Test
    fun clicking_save_should_trigger_onSave_callback() {
        // Arrange
        val onSaveMock = mockk<(String, String, String) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            ProfileCompletionScreen(
                displayName = "John Doe",
                email = "john@example.com",
                state = UiState.Idle,
                onSave = onSaveMock
            )
        }

        // Act
        composeTestRule.onNodeWithText(saveButtonText).performClick()

        // Assert
        verify(exactly = 1) { onSaveMock.invoke("John", "Doe", "john@example.com") }
    }
}