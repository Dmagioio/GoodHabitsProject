package com.example.goodhabits.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class IdeaCategory(
    val id: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    @StringRes val descriptionRes: Int,
    @StringRes val ideasRes: List<Int>
)
