package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 0: System, 1: Light, 2: Dark
class ThemeRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeFlow = MutableStateFlow(prefs.getInt("theme_mode", 0))
    val themeFlow: StateFlow<Int> = _themeFlow.asStateFlow()

    // 0: System, 1: English, 2: Spanish
    private val _languageFlow = MutableStateFlow(prefs.getInt("language_mode", 0))
    val languageFlow: StateFlow<Int> = _languageFlow.asStateFlow()

    private val _showEmotionsFlow = MutableStateFlow(prefs.getBoolean("show_emotions", true))
    val showEmotionsFlow: StateFlow<Boolean> = _showEmotionsFlow.asStateFlow()

    // 0 = A la izquierda (pequeño), 1 = Ocupando todo el alto
    private val _emotionPositionFlow = MutableStateFlow(prefs.getInt("emotion_position", 0))
    val emotionPositionFlow: StateFlow<Int> = _emotionPositionFlow.asStateFlow()

    fun setTheme(mode: Int) {
        prefs.edit().putInt("theme_mode", mode).apply()
        _themeFlow.value = mode
    }

    fun setLanguage(mode: Int) {
        prefs.edit().putInt("language_mode", mode).apply()
        _languageFlow.value = mode
        val tags = when (mode) {
            1 -> "en"
            2 -> "es"
            else -> ""
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tags))
    }

    fun setShowEmotions(show: Boolean) {
        prefs.edit().putBoolean("show_emotions", show).apply()
        _showEmotionsFlow.value = show
    }

    fun setEmotionPosition(position: Int) {
        prefs.edit().putInt("emotion_position", position).apply()
        _emotionPositionFlow.value = position
    }
}
