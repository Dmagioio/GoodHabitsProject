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
    // ProBlack
    displayLarge = baseline.displayLarge.copy(fontFamily = ProBlackFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = ProBlackFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = ProBlackFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = ProBlackFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = ProBlackFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = ProBlackFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = ProBlackFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = ProBlackFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = ProBlackFontFamily),
    // ProItalic
    bodyLarge = baseline.bodyLarge.copy(fontFamily = ProItalicFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = ProItalicFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = ProItalicFontFamily),
    // Вкладки
    labelLarge = baseline.labelLarge.copy(fontFamily = ProItalicFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = ProItalicFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = ProItalicFontFamily),
)
