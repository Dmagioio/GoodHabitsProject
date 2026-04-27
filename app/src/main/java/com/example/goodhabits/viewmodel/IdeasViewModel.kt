package com.example.goodhabits.viewmodel

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.R
import com.example.goodhabits.domain.model.IdeaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeasViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _categories = MutableStateFlow<List<IdeaCategory>>(emptyList())
    val categories: StateFlow<List<IdeaCategory>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        _categories.value = listOf(
            IdeaCategory(
                id = "food",
                titleRes = R.string.category_food,
                icon = Icons.Default.Eco,
                descriptionRes = R.string.category_food_desc,
                ideasRes = listOf(
                    R.string.idea_food_diary,
                    R.string.idea_no_sweets,
                    R.string.idea_no_alcohol,
                    R.string.idea_no_coffee,
                    R.string.idea_new_recipe,
                    R.string.idea_fresh_fruits
                )
            ),
            IdeaCategory(
                id = "self_care",
                titleRes = R.string.category_self_care,
                icon = Icons.Default.FavoriteBorder,
                descriptionRes = R.string.category_self_care_desc,
                ideasRes = listOf(
                    R.string.idea_no_phone_before_bed,
                    R.string.idea_sleep_8h,
                    R.string.idea_sleep_before_midnight,
                    R.string.idea_no_nicotine,
                    R.string.idea_massage,
                    R.string.idea_walk_before_bed,
                    R.string.idea_pool,
                    R.string.idea_stay_home
                )
            ),
            IdeaCategory(
                id = "perfect_morning",
                titleRes = R.string.category_perfect_morning,
                icon = Icons.Default.WbSunny,
                descriptionRes = R.string.category_perfect_morning_desc,
                ideasRes = listOf(
                    R.string.idea_morning_warmup,
                    R.string.idea_early_wakeup,
                    R.string.idea_water_morning,
                    R.string.idea_read_bed,
                    R.string.idea_contrast_shower,
                    R.string.idea_new_breakfast,
                    R.string.idea_plan_day
                )
            ),
            IdeaCategory(
                id = "self_development",
                titleRes = R.string.category_self_development,
                icon = Icons.Default.Casino,
                descriptionRes = R.string.category_self_development_desc,
                ideasRes = listOf(
                    R.string.idea_read_30m,
                    R.string.idea_listen_podcast,
                    R.string.idea_learn_words,
                    R.string.idea_watch_lesson,
                    R.string.idea_meditation,
                    R.string.idea_yoga,
                    R.string.idea_plan_tasks,
                    R.string.idea_play_chess,
                    R.string.idea_watch_movie
                )
            ),
            IdeaCategory(
                id = "family",
                titleRes = R.string.category_family,
                icon = Icons.Default.SentimentSatisfied,
                descriptionRes = R.string.category_family_desc,
                ideasRes = listOf(
                    R.string.idea_family_dinner,
                    R.string.idea_call_parents,
                    R.string.idea_play_kids,
                    R.string.idea_family_walk,
                    R.string.idea_share_success
                )
            )
        )
    }

    fun getCategoryById(id: String): IdeaCategory? {
        return _categories.value.find { it.id == id }
    }
}
