package com.example.goodhabits.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.goodhabits.R

private val ProBlackFontFamily = FontFamily(
    Font(R.font.pro_black)
)

private val ProItalicFontFamily = FontFamily(
    Font(R.font.pro_italic)
)
val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = ProBlackFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = ProBlackFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = ProBlackFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = ProBlackFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = ProBlackFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = ProBlackFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = ProBlackFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = ProBlackFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = ProBlackFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = ProBlackFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = ProBlackFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = ProBlackFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = ProBlackFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = ProBlackFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = ProBlackFontFamily),
)
