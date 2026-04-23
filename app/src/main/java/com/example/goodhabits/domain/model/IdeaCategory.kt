package com.example.goodhabits.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class IdeaCategory(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val ideas: List<String>
)
