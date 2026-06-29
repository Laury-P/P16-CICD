package com.openclassroom.eventorias.features.profile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayUserProfileDataWhenSuccess() {
        // GIVEN - Un état Success avec des données utilisateur
        val testUser = User(
            id = "123",
            firstname = "Jean",
            lastname = "Dupont",
            email = "jean.dupont@email.com",
            avatar = null
        )
        val successState = UiState.Success(user = testUser)

        // WHEN - On affiche le composant de contenu isolé
        composeTestRule.setContent {
            EventoriasTheme {
                ProfileContent(
                    modifier = Modifier,
                    userState = successState,
                    snackbarHostState = SnackbarHostState(),
                    uploadingState = AvatarUploadingState.Idle, // Remplacer par l'enum ou objet Idle correspondant
                    onAvatarClick = {},
                    onLogoutClick = {},
                    notificationStatus = false,
                    onSwitchClicked = {}
                )
            }
        }

        // THEN - On vérifie que les valeurs s'affichent à l'écran
        composeTestRule.onNodeWithText("Jean Dupont").assertIsDisplayed()
        composeTestRule.onNodeWithText("jean.dupont@email.com").assertIsDisplayed()
    }

    @Test
    fun clickingLogoutShouldTriggerCallback() {
        // GIVEN - L'écran est affiché au statut Success, et on traque le clic
        var wasLogoutClicked = false
        val testUser = User(id = "123", firstname = "Jean", lastname = "Dupont", email = "jean.dupont@email.com", avatar = null)

        composeTestRule.setContent {
            EventoriasTheme {
                ProfileContent(
                    modifier = Modifier,
                    userState = UiState.Success(user = testUser),
                    snackbarHostState = SnackbarHostState(),
                    uploadingState = AvatarUploadingState.Idle,
                    onAvatarClick = {},
                    onLogoutClick = { wasLogoutClicked = true }, // Capture de l'événement
                    notificationStatus = false,
                    onSwitchClicked = {}
                )
            }
        }

        // WHEN - Clic sur le bouton de déconnexion
        composeTestRule.onNodeWithTag("logout_button").performClick()

        // THEN - Le callback a bien été invoqué par le composant
        assert(wasLogoutClicked)
    }

    @Test
    fun togglingNotificationSwitchShouldTriggerCallback() {
        // GIVEN - L'écran s'affiche et on surveille l'interaction avec le switch
        var wasSwitchClicked = false
        val testUser = User(id = "123", firstname = "Jean", lastname = "Dupont", email = "jean.dupont@email.com", avatar = null)

        composeTestRule.setContent {
            EventoriasTheme {
                ProfileContent(
                    modifier = Modifier,
                    userState = UiState.Success(user = testUser),
                    snackbarHostState = SnackbarHostState(),
                    uploadingState = AvatarUploadingState.Idle,
                    onAvatarClick = {},
                    onLogoutClick = {},
                    notificationStatus = false,
                    onSwitchClicked = { wasSwitchClicked = true } // Capture du changement d'état
                )
            }
        }

        // WHEN - Clic sur la ligne contenant le Switch grâce au testTag
        composeTestRule.onNodeWithTag("notification_switch").performClick()

        // THEN - Le composant a bien remonté l'action de l'utilisateur au parent
        assert(wasSwitchClicked)
    }

    @Test
    fun shouldDisplayErrorTextWhenStateIsError() {
        // GIVEN - Un état d'erreur
        val errorState = UiState.Error("User not found")

        // WHEN - Rendu du composant
        composeTestRule.setContent {
            EventoriasTheme {
                ProfileContent(
                    modifier = Modifier,
                    userState = errorState,
                    snackbarHostState = SnackbarHostState(),
                    uploadingState = AvatarUploadingState.Idle,
                    onAvatarClick = {},
                    onLogoutClick = {},
                    notificationStatus = false,
                    onSwitchClicked = {}
                )
            }
        }

        // THEN - Le Loader ou le message d'erreur est affiché

        composeTestRule.onNodeWithTag("user_error_message").assertIsDisplayed()
    }
}