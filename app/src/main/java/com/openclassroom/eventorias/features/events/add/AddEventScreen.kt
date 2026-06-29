package com.openclassroom.eventorias.features.events.add

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.features.events.add.component.CategorySelector
import com.openclassroom.eventorias.features.events.add.component.DateTimeSelector
import com.openclassroom.eventorias.features.events.detail.FormEvent
import com.openclassroom.eventorias.features.events.detail.IsPublishing
import com.openclassroom.eventorias.features.events.eventList.component.LoadingComponent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EventListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File

@SuppressLint( "LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun AddEventScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: AddEventViewModel = hiltViewModel()
) {
    val newEvent by viewModel.newEvent.collectAsStateWithLifecycle()
    val isPublishing by viewModel.isPublishing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.onAction(FormEvent.PhotoSelected(uri))
            }
        }
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success)
            tempPhotoUri.value?.let { uri ->
                viewModel.onAction((FormEvent.PhotoSelected(uri)))
            }
    }

    fun launchCameraIntent() {
        val file = File.createTempFile(
            "event_photo_",
            ".jpg",
            context.cacheDir
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        tempPhotoUri.value = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) launchCameraIntent()

    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isPublishing) {
        if (isPublishing is IsPublishing.Published) {
            navigator.navigate(EventListScreenDestination)
        }
        if (isPublishing is IsPublishing.Error) {
            val errorRes = (isPublishing as IsPublishing.Error).error.messageRes

            snackbarHostState.showSnackbar(
                message = context.getString(errorRes),
                withDismissAction = true
            )

        }
    }

    AddEventContent(
        modifier = modifier,
        onNavBack = { navigator.popBackStack() },
        newEvent = newEvent,
        snackbarHostState = snackbarHostState,
        isPublishing = isPublishing,
        onAction = viewModel::onAction,
        onPhotoClick = {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                permission.CAMERA
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) launchCameraIntent()
            else cameraPermissions.launch(permission.CAMERA)
        },
        onGalleryClick = {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventContent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onNavBack: () -> Unit,
    newEvent: NewUiEvent,
    isPublishing: IsPublishing,
    onAction: (FormEvent) -> Unit,
    onGalleryClick: () -> Unit,
    onPhotoClick: () -> Unit
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()){
        if (isPublishing is IsPublishing.Publishing) LoadingComponent()

        Scaffold(
            modifier = modifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.add_screen_title),

                        )
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = { onNavBack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button),
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->

            val dims = EventoriasTheme.dimensions
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = dims.padding24),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(dims.padding24)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newEvent.title,
                        onValueChange = { onAction(FormEvent.TitleChanged(it)) },
                        label = { Text(stringResource(R.string.title_label)) },
                        placeholder = { Text(stringResource(R.string.title_placeholder)) },
                        shape = MaterialTheme.shapes.small,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newEvent.description,
                        onValueChange = { onAction(FormEvent.DescriptionChanged(it)) },
                        label = { Text(stringResource(R.string.description_label)) },
                        placeholder = { Text(stringResource(R.string.description_placeholder)) },
                        shape = MaterialTheme.shapes.small,
                        maxLines = 5
                    )
                    DateTimeSelector(
                        date = newEvent.date,
                        time = newEvent.time,
                        onDateSelected = { onAction(FormEvent.DateChanged(it)) },
                        onTimeSelected = { onAction(FormEvent.TimeChanged(it)) }
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newEvent.address,
                        onValueChange = { onAction(FormEvent.AddressChanged(it)) },
                        label = { Text(stringResource(R.string.address_label)) },
                        placeholder = { Text(stringResource(R.string.address_placeholder)) },
                        shape = MaterialTheme.shapes.small,
                        maxLines = 2,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    )

                    CategorySelector(
                        selectedCategory = newEvent.category,
                        onCategorySelected = { onAction(FormEvent.CategoryChanged(it)) },
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = dims.padding24)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        IconButton(
                            onClick = {
                                onPhotoClick()
                            },
                            colors = IconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                containerColor = MaterialTheme.colorScheme.secondary,
                                disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                                disabledContainerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = stringResource(R.string.take_photo_description),
                            )
                        }
                        Spacer(modifier = Modifier.width(dims.padding16))
                        IconButton(
                            onClick = {
                                onGalleryClick()
                            },
                            colors = IconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                                disabledContainerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = stringResource(R.string.select_from_gallery_description)
                            )
                        }
                    }
                    if (newEvent.pictureUri != null) {
                        AsyncImage(
                            model = newEvent.pictureUri,
                            contentDescription = stringResource(R.string.photo_preview_description),
                            modifier = Modifier.clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                val enabled =
                    (isPublishing != IsPublishing.Publishing) &&
                            (newEvent.date != null && newEvent.time != null &&
                                    newEvent.title.isNotEmpty() && newEvent.description.isNotEmpty() &&
                                    newEvent.address.isNotEmpty())

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_event_button"),
                    onClick = { onAction(FormEvent.OnSaveClicked(context)) },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = MaterialTheme.shapes.small,
                    enabled = enabled
                ) {
                    Text(
                        text =
                            if (isPublishing is IsPublishing.Error) {
                                stringResource(R.string.retry_button)
                            } else stringResource(R.string.add_event_button),
                    )
                }
            }
        }
    }

}



