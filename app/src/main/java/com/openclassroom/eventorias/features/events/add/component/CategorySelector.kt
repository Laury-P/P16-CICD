package com.openclassroom.eventorias.features.events.add.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.EventCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selectedCategory: EventCategory,
    onCategorySelected: (EventCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedState = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandedState.value,
        onExpandedChange = {expandedState.value = it},
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            readOnly = true,
            value = stringResource(selectedCategory.labelResId),
            onValueChange = {},
            label = { Text(stringResource(R.string.category_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState.value) },
            shape = MaterialTheme.shapes.small
        )

        ExposedDropdownMenu(
            expanded = expandedState.value,
            onDismissRequest = { expandedState.value = false },
            shape = MaterialTheme.shapes.small
        ) {
            EventCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(stringResource(category.labelResId)) },
                    onClick = {
                        onCategorySelected(category)
                        expandedState.value = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}