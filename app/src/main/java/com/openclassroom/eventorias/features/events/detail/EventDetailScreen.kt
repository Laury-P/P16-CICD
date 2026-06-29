package com.openclassroom.eventorias.features.events.detail

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.features.events.detail.component.AddressItem
import com.openclassroom.eventorias.features.events.detail.component.DateTimeItem
import com.openclassroom.eventorias.features.events.detail.component.ParticipationItem
import com.openclassroom.eventorias.features.events.detail.model.DetailEventUiModel
import com.openclassroom.eventorias.features.events.detail.model.DetailEventUiState
import com.openclassroom.eventorias.features.events.eventList.component.LoadingComponent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.ramcosta.composedestinations.generated.destinations.EventListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@Destination<RootGraph>(
    deepLinks = [
        DeepLink(uriPattern = "eventorias://details/{eventId}")
    ]
)
@Composable
fun EventDetailScreen(
    @Suppress("unused") eventId: String,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val detailState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EventDetailViewModel.DetailEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    when (val currentState = detailState) {
        is DetailEventUiState.Loading -> LoadingComponent()
        is DetailEventUiState.Error -> {
            LaunchedEffect(currentState) {
                delay(2500)
                navigator.navigate(EventListScreenDestination)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.event_not_found),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        is DetailEventUiState.Success -> {
            EventDetailContent(
                modifier,
                context,
                eventDetail = currentState.eventDetail,
                onSwitchClicked = { status, id ->  viewModel.setUserParticipation(status, id) },
                onNavBack = {navigator.popBackStack()}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailContent(
    modifier: Modifier,
    context: Context,
    eventDetail: DetailEventUiModel,
    onSwitchClicked: (Boolean, String) -> Unit,
    onNavBack: () -> Unit,
) {
    val event = eventDetail.event
    val promoterAvatar = eventDetail.promoterUrl
    val isUserParticipating = eventDetail.isUserParticipating
    val nbrOfParticipants = eventDetail.nbrOfParticipants
    val dims = EventoriasTheme.dimensions

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge
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
                actions = {
                    IconButton(onClick = { shareEvent(event.id, context, event.title) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_event_button_description)
                        )

                    }
                }
            )
        },
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = dims.padding24)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dims.padding24)
        ) {
            if(event.photoUrl.isNotEmpty()) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium),
                    model = event.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                DateTimeItem(dateTime = event.dateTime)
                AsyncImage(
                    modifier = Modifier
                        .size(dims.avatarDetail)
                        .clip(CircleShape),
                    model = promoterAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )

            ParticipationItem(
                nbrOfParticipant = nbrOfParticipants,
                isUserParticipating = isUserParticipating,
                onSwitchClicked = { onSwitchClicked(!isUserParticipating, event.id) }
            )

            AddressItem(
                address = event.location
            )
        }
    }
}


private fun shareEvent(eventId: String, context: Context, eventTitle: String) {
    val deepLink = "eventorias://details/${eventId}"

    val shareMessage = context.getString(R.string.share_message, eventTitle, deepLink)
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }

    val chooserTitle = context.getString(R.string.share_event_chooser_title)
    val shareIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(shareIntent)
}
