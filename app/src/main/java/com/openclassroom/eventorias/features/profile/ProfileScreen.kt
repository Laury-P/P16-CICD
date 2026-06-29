package com.openclassroom.eventorias.features.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.features.events.eventList.component.LoadingComponent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AuthScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EventListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userState by viewModel.uiState.collectAsStateWithLifecycle()
    val uploadingState by viewModel.uploadingAvatarState.collectAsStateWithLifecycle()
    val notificationStatus by viewModel.notificationState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.updateAvatar(uri)
            }
        }

    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect { result ->
            result.onSuccess { navigator.navigate(AuthScreenDestination) }
                .onFailure {
                    val snackBarResult = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.logout_failed),
                        actionLabel = context.getString(R.string.retry_button),
                        withDismissAction = true,
                    )
                    if (snackBarResult == SnackbarResult.ActionPerformed) viewModel.logOut()
                }
        }
    }
    LaunchedEffect(uploadingState) {
        if (uploadingState is AvatarUploadingState.Error) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.upload_avatar_failure),
                withDismissAction = true
            )
        }
    }
    LaunchedEffect(userState) {
        if (userState is UiState.Error) {
            delay(2500)
            navigator.navigate(EventListScreenDestination)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleNotification(!notificationStatus)
        } else {
            viewModel.toggleNotification(false)
        }

    }

    ProfileContent(
        modifier = modifier,
        userState = userState,
        snackbarHostState = snackbarHostState,
        uploadingState = uploadingState,
        onAvatarClick = {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        onLogoutClick = {viewModel.logOut()},
        notificationStatus = notificationStatus,
        onSwitchClicked = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermissions = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasPermissions) {
                    permissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else viewModel.toggleNotification(!notificationStatus)
            } else {
                viewModel.toggleNotification(!notificationStatus)
            }
        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    userState: UiState,
    snackbarHostState: SnackbarHostState,
    uploadingState: AvatarUploadingState,
    onAvatarClick: () -> Unit,
    onLogoutClick: () -> Unit,
    notificationStatus : Boolean,
    onSwitchClicked: () -> Unit
) {
    val dims = EventoriasTheme.dimensions
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_screen_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                actions = {
                    if (uploadingState is AvatarUploadingState.Uploading) {
                        CircularProgressIndicator()
                    } else {
                        val avatarUrl = (userState as? UiState.Success)?.user?.avatar
                        IconButton(
                            onClick = { onAvatarClick() }
                        ) {
                            if (avatarUrl == null) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = stringResource(R.string.no_avatar),
                                    modifier = Modifier.size(dims.avatarProfile)
                                )
                            } else {
                                AsyncImage(
                                    model = avatarUrl,
                                    modifier = Modifier
                                        .size(dims.avatarDetail)
                                        .clip(CircleShape),
                                    contentDescription = stringResource(R.string.avatar_description),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                }
            )
        }
    ) { innerPadding ->
        when (userState) {
            is UiState.Success -> {
                val user = userState.user
                SuccessContent(
                    modifier = modifier.padding(innerPadding),
                    user = user,
                    onLogoutClick = { onLogoutClick() },
                    notificationStatus = notificationStatus,
                    onSwitchClicked = { onSwitchClicked()}
                )
            }

            is UiState.Idle -> LoadingComponent()
            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.profile_error),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag("user_error_message")
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    user: User,
    notificationStatus: Boolean,
    onSwitchClicked: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val dims = EventoriasTheme.dimensions
    Column(
        modifier = modifier
            .padding(horizontal = dims.padding24),
        verticalArrangement = Arrangement.spacedBy(dims.padding24)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "${user.firstname} ${user.lastname}",
            onValueChange = {},
            label = { Text(stringResource(R.string.name_label)) },
            shape = MaterialTheme.shapes.small,
            singleLine = true,
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = user.email,
            onValueChange = {},
            label = { Text(stringResource(R.string.email_label)) },
            shape = MaterialTheme.shapes.small,
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dims.padding12),
            modifier = Modifier
                .testTag("notification_switch")
                .toggleable(
                    value = notificationStatus,
                    onValueChange = { onSwitchClicked() },
                    role = Role.Switch,
                )
                .semantics(mergeDescendants = true) { }
                .padding(dims.padding8)
        ) {
            Switch(
                checked = notificationStatus,
                onCheckedChange = null,
                modifier = Modifier.clearAndSetSemantics {}
            )
            Text(
                text = stringResource(R.string.notification_switch_label),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
            )
        }

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("logout_button"),
            onClick = { onLogoutClick() },
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                disabledContainerColor = MaterialTheme.colorScheme.tertiary
            ),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = stringResource(R.string.logout_button),
                style = MaterialTheme.typography.titleMedium
            )
        }

    }

}


@Preview
@Composable
fun SuccessContentPreview() {
    EventoriasTheme(
        {
            SuccessContent(
                user = User(
                    id = "123",
                    email = "adrien.chardon@email.com",
                    firstname = "Adrien",
                    lastname = "Chardon",
                    avatar = "https://firebasestorage.googleapis.com/v0/b/eventorias-15e42.firebasestorage.app/o/52d7360517a4a8f4c1bf360e6a103532ef0b53b8.png?alt=media&token=ba39425d-7035-4184-930c-4082849e4942",
                ),
                onSwitchClicked = { },
                onLogoutClick = {},
                notificationStatus = true,
            )
        }
    )
}