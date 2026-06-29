package com.openclassroom.eventorias.features.events.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme

@Composable
fun ParticipationItem(
    modifier: Modifier = Modifier,
    nbrOfParticipant: Int,
    isUserParticipating: Boolean,
    onSwitchClicked: () -> Unit
) {
    val dims = EventoriasTheme.dimensions

    val attendeesText =
        pluralStringResource(R.plurals.attendees_counter, nbrOfParticipant, nbrOfParticipant)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .testTag("participation_switch")
                .toggleable(
                    value = isUserParticipating,
                    onValueChange = { onSwitchClicked() },
                    role = Role.Switch,)
                .semantics(mergeDescendants = true){ }
                .padding(dims.padding8)

        ) {
            Text(
                text =  stringResource(R.string.switch_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Switch(
                checked = isUserParticipating, onCheckedChange = null,
                modifier = Modifier
                    .padding(start = dims.padding12)
                    .clearAndSetSemantics {}
            )
        }
        Text(
            text = attendeesText,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun ParticipationItemPreview() {
    EventoriasTheme {
        ParticipationItem(
            nbrOfParticipant = 12,
            isUserParticipating = true,
            onSwitchClicked = { },
        )
    }
}