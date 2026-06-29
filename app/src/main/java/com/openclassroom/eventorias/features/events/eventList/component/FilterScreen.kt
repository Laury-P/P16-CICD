package com.openclassroom.eventorias.features.events.eventList.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.EventCategory
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.core.ui.component.DatePickerModal
import com.openclassroom.eventorias.core.utils.toFormattedDateString
import java.time.LocalDate

@Composable
fun FilterScreen(
    modifier: Modifier,
    selectedCategory: EventCategory?,
    selectedDate: LocalDate?,
    onChipsSelected: (EventCategory?) -> Unit,
    onDateSelected: (LocalDate?) -> Unit,
) {
    val dims = EventoriasTheme.dimensions

    var isDatePickerOpen = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.sort_date_text),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = dims.padding8)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.padding8)
        ) {
            FilterChip(
                selected = selectedDate == null,
                onClick = { onDateSelected(null) },
                label = { Text(stringResource(R.string.all_chip_label)) }
            )

            val isCustomDateActive = selectedDate != null
            FilterChip(
                selected = isCustomDateActive,
                onClick = { isDatePickerOpen.value = true },
                label = {
                    Text(
                        if (isCustomDateActive) {
                            selectedDate.toFormattedDateString()
                        } else stringResource(R.string.choose_date_label)
                    )
                }
            )
        }
        Text(
            text = stringResource(R.string.sort_category_text),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = dims.padding16, bottom = dims.padding8)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.padding8),
            verticalArrangement = Arrangement.spacedBy(dims.padding4)
        ) {
            CategoryFilterChip(
                modifier = Modifier,
                currentCategory = selectedCategory,
                chipsCategory = null,
                onChipsSelected = { onChipsSelected(null) }
            )
            EventCategory.entries.forEach { category ->
                CategoryFilterChip(
                    modifier = Modifier.testTag("${category}_filter_button"),
                    currentCategory = selectedCategory,
                    chipsCategory = category,
                    onChipsSelected = { onChipsSelected(category) }
                )
            }
        }
    }

    if (isDatePickerOpen.value) {
        DatePickerModal(
            onDateSelected = { onDateSelected(it)},
            onDismiss = { isDatePickerOpen.value = false}
        )
    }

}