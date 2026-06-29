package com.openclassroom.eventorias.features.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firebase.ui.auth.AuthState
import com.firebase.ui.auth.configuration.PasswordRule
import com.firebase.ui.auth.configuration.authUIConfiguration
import com.firebase.ui.auth.configuration.auth_provider.AuthProvider
import com.firebase.ui.auth.configuration.theme.AuthUIAsset
import com.firebase.ui.auth.ui.screens.FirebaseAuthScreen
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AuthScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EventListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(start = true)
@Composable
fun AuthScreen(navigator: DestinationsNavigator) {
    val dims = EventoriasTheme.dimensions
    val context = LocalContext.current
    val configuration = authUIConfiguration {
        this.logo = AuthUIAsset.Resource(R.drawable.logo_eventorias)
        this.context = context
        providers {
            provider(
                AuthProvider.Email(
                    emailLinkActionCodeSettings = null,
                    passwordValidationRules = listOf(
                        PasswordRule.MinimumLength(12),
                        PasswordRule.RequireDigit,
                        PasswordRule.RequireLowercase,
                        PasswordRule.RequireSpecialCharacter,
                        PasswordRule.RequireUppercase
                    ),
                    minimumPasswordLength = 6,
                )
            )
            provider(
                AuthProvider.Google(
                    serverClientId = "806226545135-p2nifuc8ennk3a6feuqt0k6qvsg7m6vt.apps.googleusercontent.com",
                    scopes = emptyList(),
                )
            )
        }
        isMfaEnabled = false
    }

    FirebaseAuthScreen(
        modifier = Modifier.padding(top = dims.logScreenPadding),
        configuration = configuration,
        onSignInSuccess = { _ ->
            navigator.navigate(EventListScreenDestination()) {
                popUpTo(AuthScreenDestination) { inclusive = true }
            }
        },
        onSignInFailure = { _ -> }, // Gerer par FirebaseUi par un popUp
        onSignInCancelled = {},
        authenticatedContent = { state, _ ->
            val user = when (state) {
                is AuthState.Success -> state.user
                is AuthState.RequiresEmailVerification -> state.user
                else -> null
            }

            if (user != null) {
                ProfileGuard(
                    uid = user.uid,
                    displayName = user.displayName ?: "",
                    email = user.email ?: "",
                    onUserReady = { navigator.navigate(EventListScreenDestination()) {
                        popUpTo(AuthScreenDestination) {inclusive = true}
                    } })
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}


@Composable
fun ProfileGuard(
    uid: String,
    displayName: String,
    email: String,
    viewModel: AuthViewModel = hiltViewModel(),
    onUserReady: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(uid) {
        viewModel.checkNewUser(uid)
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            Toast.makeText(context, R.string.error_text, Toast.LENGTH_SHORT).show()
        }
    }

    when (uiState) {
        UiState.Idle, UiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        UiState.NewUser, is UiState.Error -> {
            ProfileCompletionScreen(
                displayName = displayName,
                email = email,
                state = uiState,
                onSave = { firstname, lastname, email ->
                    viewModel.addNewUser(
                        User(
                            id = uid,
                            firstname = firstname,
                            lastname = lastname,
                            email = email
                        )
                    )
                }
            )
        }

        UiState.UserReady -> {
            onUserReady()
        }

    }
}

@Composable
fun ProfileCompletionScreen(
    displayName: String?,
    email: String,
    state: UiState,
    onSave: (String, String, String) -> Unit
) {
    val nameParts = displayName?.split(" ")
    val guessedFirstname = nameParts?.getOrNull(0) ?: ""
    val guessedLastname =
        nameParts?.size?.let { if (it > 1) nameParts.drop(1).joinToString(" ") else "" }

    var firstname by remember { mutableStateOf(guessedFirstname) }
    var lastname by remember { mutableStateOf(guessedLastname ?: "") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.profile_completion_title, email),
            modifier = Modifier
        )

        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text(stringResource(R.string.firstname)) },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text(stringResource(R.string.lastname)) },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = { onSave(firstname, lastname, email) },
            enabled = firstname.isNotBlank() && lastname.isNotBlank(),
        ) {
            Text(
                if (state is UiState.Error) stringResource(R.string.retry_button) else stringResource(
                    R.string.save_button
                )
            )
        }
    }


}