package com.openclassroom.eventorias.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.openclassroom.eventorias.R

@Composable
fun RetryButton(modifier : Modifier = Modifier, onRetryClick : () -> Unit) {
    TextButton(
        onClick = onRetryClick,
        modifier = modifier
            .width(159.dp)
            .height(40.dp),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 10.dp,
            start = 12.dp,
            end = 12.dp
        ),
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onTertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Text(
            text = stringResource(R.string.retry_button),
            style = MaterialTheme.typography.titleMedium
        )
    }
}