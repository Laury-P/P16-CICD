package com.openclassroom.eventorias.features.events.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.component.DatePickerModal
import com.openclassroom.eventorias.core.ui.component.TimePickerModal
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.core.utils.toAddFormFormat
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DateTimeSelector(
    date: LocalDate?,
    time: LocalTime?,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val dims = EventoriasTheme.dimensions

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dims.padding16),

        ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = date?.toAddFormFormat() ?: "",
                onValueChange = { },
                label = { Text(stringResource(R.string.date_label)) },
                shape = MaterialTheme.shapes.small,
                readOnly = true,
                enabled = true,
            )
            val dateDescription = stringResource(R.string.date_description)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker.value = true }
                    .clearAndSetSemantics {
                        contentDescription = dateDescription
                    }
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = time?.toAddFormFormat() ?: "",
                onValueChange = { },
                label = { Text(stringResource(R.string.time_label)) },
                shape = MaterialTheme.shapes.small,
                readOnly = true,
                enabled = true,
            )
            val timeDescription = stringResource(R.string.time_description)
            Box(

                modifier = Modifier
                    .matchParentSize()
                    .clickable { showTimePicker.value = true }
                    .clearAndSetSemantics {
                        contentDescription = timeDescription
                    }
            )
        }

    }

    if(showDatePicker.value) {
        DatePickerModal(
            onDateSelected = {date ->
                onDateSelected(date)
            },
            onDismiss = {showDatePicker.value = false}
        )
    }
    if(showTimePicker.value) {
        TimePickerModal(
            onTimeSelected = { time ->
                onTimeSelected(time)
            },
            onDismiss = {showTimePicker.value = false}
        )
    }


}