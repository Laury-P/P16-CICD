package com.openclassroom.eventorias.features.events.eventDetail

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.Event
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.features.events.detail.EventDetailContent
import com.openclassroom.eventorias.features.events.detail.model.DetailEventUiModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class EventDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    private val fakeEvent = Event(
        id = "event_123",
        title = "Super Concert",
        description = "Une description incroyable du concert.",
        photoUrl = "",
        dateTime = LocalDateTime.of(2026,12,25,20,0),
        location = "10 Rue de Paris, France",
        promoterId = "promoter_1"
    )

    private val fakeUiModel = DetailEventUiModel(
        event = fakeEvent,
        promoterUrl = "",
        nbrOfParticipants = 5,
        isUserParticipating = true
    )

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }


    @Test
    fun verify_all_event_details_are_displayed_correctly() {
        // Arrange
        composeTestRule.setContent {
            EventDetailContent(
                modifier = Modifier,
                context = context,
                eventDetail = fakeUiModel,
                onSwitchClicked = { _, _ -> },
                onNavBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Super Concert").assertIsDisplayed()
        composeTestRule.onNodeWithText("Une description incroyable du concert.").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 Rue de Paris, France").assertIsDisplayed()
    }


    @Test
    fun verify_participation_switch_click_triggers_action() {
        // Arrange
        var capturedStatus: Boolean? = null
        var capturedId: String? = null

        composeTestRule.setContent {
            EventoriasTheme{
                EventDetailContent(
                    modifier = Modifier,
                    context = context,
                    eventDetail = fakeUiModel,
                    onSwitchClicked = { status, id ->
                        capturedStatus = status
                        capturedId = id
                    },
                    onNavBack = {}
                )
            }

        }

        // Act
        composeTestRule.onNodeWithTag("participation_switch").performClick()

        // Assert
        assert(capturedStatus == false)
        assert(capturedId == "event_123")
    }


    @Test
    fun verify_share_button_opens_correct_intent() {
        // Arrange
        val shareDescription = context.getString(R.string.share_event_button_description)

        composeTestRule.setContent {
            EventDetailContent(
                modifier = Modifier,
                context = context,
                eventDetail = fakeUiModel,
                onSwitchClicked = { _, _ -> },
                onNavBack = {}
            )
        }

        // Act
        composeTestRule.onNodeWithContentDescription(shareDescription).performClick()

        // Assert
        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun verify_back_button_triggers_navigation() {
        // Arrange
        var backClicked = false
        val backDescription = context.getString(R.string.back_button)

        composeTestRule.setContent {
            EventDetailContent(
                modifier = Modifier,
                context = context,
                eventDetail = fakeUiModel,
                onSwitchClicked = { _, _ -> },
                onNavBack = { backClicked = true }
            )
        }

        // Act
        composeTestRule.onNodeWithContentDescription(backDescription).performClick()

        // Assert
        assert(backClicked)
    }
}