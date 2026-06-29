package com.openclassroom.eventorias.features.events.eventList.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.EventCategory

@Composable
fun CategoryFilterChip(
    modifier: Modifier,
    currentCategory: EventCategory?,
    chipsCategory: EventCategory?,
    onChipsSelected: (EventCategory?) -> Unit
) {
    val isSelected = currentCategory == chipsCategory

    val labelText =
        if (chipsCategory != null) stringResource(chipsCategory.labelResId)
        else stringResource(R.string.all_chip_label)

    FilterChip(
        modifier = modifier,
        onClick = { onChipsSelected(chipsCategory) },
        label = { Text(text = labelText) },
        selected = isSelected,
        leadingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    modifier = Modifier.size(
                        FilterChipDefaults.IconSize
                    )
                )
            }
        },
    )
}