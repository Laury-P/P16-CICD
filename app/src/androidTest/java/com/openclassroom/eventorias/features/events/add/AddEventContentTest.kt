package com.openclassroom.eventorias.features.events.add

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.openclassroom.eventorias.features.events.detail.IsPublishing
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AddEventContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun saveButton_shouldBeDisabled_whenFieldsAreEmpty() {
        // GIVEN
        val emptyForm = NewUiEvent()

        // WHEN
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            AddEventContent(
                snackbarHostState = snackbarHostState,
                onNavBack = {},
                newEvent = emptyForm,
                isPublishing = IsPublishing.Idle,
                onAction = {},
                onGalleryClick = {},
                onPhotoClick = {}
            )
        }

        // THEN:
        composeTestRule.onNodeWithTag("save_event_button")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun saveButton_shouldBeEnabled_whenAllRequiredFieldsAreFilled() {
        // GIVEN:
        val completeForm = NewUiEvent(
            title = "Anniversaire",
            description = "Fête d'anniversaire",
            date = LocalDate.now(),
            time = LocalTime.now(),
            address = "10 Rue de Paris"
        )

        // WHEN
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            AddEventContent(
                snackbarHostState = snackbarHostState,
                onNavBack = {},
                newEvent = completeForm,
                isPublishing = IsPublishing.Idle,
                onAction = {},
                onGalleryClick = {},
                onPhotoClick = {}
            )
        }

        // THEN:
        composeTestRule.onNodeWithTag("save_event_button")
            .assertIsEnabled()
    }

    @Test
    fun loadingComponent_shouldBeVisible_whenPublishing() {
        // GIVEN:
        val form = NewUiEvent()

        // WHEN
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            AddEventContent(
                snackbarHostState = snackbarHostState,
                onNavBack = {},
                newEvent = form,
                isPublishing = IsPublishing.Publishing, // Mode chargement actif
                onAction = {},
                onGalleryClick = {},
                onPhotoClick = {}
            )
        }

        // THEN:
        composeTestRule.onNodeWithTag("loading_item").assertIsDisplayed()
    }
}