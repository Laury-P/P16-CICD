package com.openclassroom.eventorias.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.openclassroom.eventorias.R

val provider= GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.pas_de_certificats
)

val Inter = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.SemiBold),
)

// Set of Material typography styles to start with
val Typography = Typography(
    // Card Title, detail date&time&place,
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium, //500
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // Form text, notification button Text, error text,
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    // Card date, detail description,
    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Screen title, error title
    titleLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold, //600
        fontSize = 20.sp,
        lineHeight = 20.sp, //100%
        letterSpacing = 0.02.em //2%
    ),
    // Button
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold, //600
        fontSize = 16.sp,
        lineHeight = 16.sp, //100%
        letterSpacing = 0.02.em //2%
    ),
    // Nav Text,
    titleSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold, //600
        fontSize = 12.sp,
        lineHeight = 16.sp, //100%
    ),
    // Form label
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

)