package com.openclassroom.eventorias.core.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = red,
    onPrimary = white,

    secondary = white,
    onSecondary = grey,

    tertiary = grey,
    onTertiary = white,

    background = black,
    onBackground = white,

    surface = grey,
    onSurface = lightGrey,

)

@Composable
fun EventoriasTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val typography = Typography
    val dimensions = compactDimensions

    CompositionLocalProvider(LocalAppDimensions provides dimensions) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
            shapes = Shapes
        )
    }
}

object EventoriasTheme {
    val dimensions: EventoriasDimensions
    @Composable
    get() = LocalAppDimensions.current
}