package com.openclassroom.eventorias.features.events.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.core.utils.toFormattedDateString
import com.openclassroom.eventorias.core.utils.toFormattedTimeString
import java.time.LocalDateTime

@Composable
fun DateTimeItem(modifier: Modifier = Modifier, dateTime: LocalDateTime) {
    val dims = EventoriasTheme.dimensions
    val date = dateTime.toFormattedDateString()
    val time = dateTime.toFormattedTimeString()

    val contentDescription = stringResource(R.string.date_time_description, date, time)

    Column (
        modifier = modifier.clearAndSetSemantics { this.contentDescription = contentDescription },
        verticalArrangement = Arrangement.spacedBy(dims.padding12)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dims.padding12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(dims.detailIconSize),
                tint = MaterialTheme.colorScheme.onBackground
            )



            Text(
                text = date,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(dims.padding12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(dims.detailIconSize),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

        }
    }
}

@Preview
@Composable
fun DateTimeItemPreview() {
    val dateTime = LocalDateTime.of(2026, 6, 11, 10, 30)
    EventoriasTheme {
        DateTimeItem(dateTime = dateTime)
    }
}