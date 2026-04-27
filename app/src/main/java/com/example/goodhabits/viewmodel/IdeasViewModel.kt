package com.example.goodhabits.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import com.example.goodhabits.domain.model.IdeaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class IdeasViewModel @Inject constructor() : ViewModel() {

    private val _categories = MutableStateFlow<List<IdeaCategory>>(emptyList())
    val categories: StateFlow<List<IdeaCategory>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _categories.value = listOf(
            IdeaCategory(
                id = "food",
                title = "Їжа",
                icon = Icons.Default.Eco,
                description = "Фрукти, трави, щоденник...",
                ideas = listOf(
                    "Вести щоденник калорій",
                    "День без солодкого",
                    "День без алкоголю",
                    "День без кави",
                    "Новий рецепт кожен день",
                    "Їсти свіжі фрукти"
                )
            ),
            IdeaCategory(
                id = "self_care",
                title = "Самопіклування",
                icon = Icons.Default.FavoriteBorder,
                description = "Сон, масаж, мрії...",
                ideas = listOf(
                    "Година перед сном без телефону",
                    "Спати принаймні 8 годин",
                    "Лягати спати до півночі",
                    "День без нікотину",
                    "Масаж",
                    "Прогулянка перед сном",
                    "Басейн",
                    "Залишатися вдома",
                )
            ),
            IdeaCategory(
                id = "perfect_morning",
                title = "Ідеальний ранок",
                icon = Icons.Default.WbSunny,
                description = "Сніданок, душ, вправи та інше...",
                ideas = listOf(
                    "Ранкова розминка",
                    "Ранній підйом",
                    "Склянка води вранці",
                    "Читати книгу в ліжку 15 хвилин",
                    "Холодний і гарячий душ",
                    "Сніданок за новим рецептом",
                    "Планувати свій день"
                )
            ),
            IdeaCategory(
                id = "self_development",
                title = "Саморозвиток",
                icon = Icons.Default.Casino,
                description = "Читання, медитація та інше...",
                ideas = listOf(
                    "Читати книгу 30 хвилин",
                    "Послухати 1 подкаст",
                    "Вивчити 10 іноземних слів",
                    "Переглянути 1 урок онлайн",
                    "Медитація",
                    "Йога",
                    "Планувати завдання",
                    "Грати в шахи",
                    "Переглянути фільм"
                )
            ),
            IdeaCategory(
                id = "family",
                title = "Сім'я",
                icon = Icons.Default.SentimentSatisfied,
                description = "Батьки, діти, танець та інше...",
                ideas = listOf(
                    "Вечеря з сім'єю",
                    "Зателефонувати батькам",
                    "Гра з дітьми",
                    "Спільна прогулянка",
                    "Ділитись успіхами дня"
                )
            )
        )
    }

    fun getCategoryById(id: String): IdeaCategory? {
        return _categories.value.find { it.id == id }
    }
}
