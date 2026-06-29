package com.openclassroom.eventorias.features.events.eventList.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.component.RetryButton
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme

@Composable
fun ErrorScreen(onRetryClick: () -> Unit) {
    val dims = EventoriasTheme.dimensions
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(dims.errorIconSize)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.extraSmall
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.error_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        Text(
            modifier = Modifier.padding(top = dims.padding24),
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.padding(top = dims.padding4, bottom = dims.padding12),
            text = stringResource(R.string.error_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        RetryButton(modifier = Modifier.padding(top = dims.padding24), onRetryClick = onRetryClick)
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    EventoriasTheme {
        ErrorScreen(onRetryClick = {})
    }
}