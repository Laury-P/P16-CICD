package com.openclassroom.eventorias.features.events.eventList.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import coil.compose.AsyncImage
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.core.utils.toFormattedDateString
import com.openclassroom.eventorias.features.events.eventList.model.ListEventUiModel

@Composable
fun EventItem(uiEvent: ListEventUiModel, onEventClick: () -> Unit) {
    val dims = EventoriasTheme.dimensions

    val event = uiEvent.event
    val promoterAvatar = uiEvent.promoterAvatarUrl

    Surface(
        modifier = Modifier
            .testTag(event.title)
            .fillMaxWidth()
            .padding(top = dims.padding8)
            .height(dims.cardHeight)
            .clickable(enabled = true, onClick = {onEventClick()}, onClickLabel = stringResource(R.string.on_event_click_label))
            .clearAndSetSemantics{
                contentDescription = event.title + " " + event.dateTime.toFormattedDateString()
            },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f)
                    .padding(start = dims.padding16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .padding(top = dims.padding20, bottom = dims.padding20)
                        .size(dims.avatarEventList)
                        .clip(CircleShape),
                    model = promoterAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(dims.padding16))

                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(vertical = dims.padding16),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = event.title, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = event.dateTime.toFormattedDateString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium),
                model = event.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

        }

    }
}