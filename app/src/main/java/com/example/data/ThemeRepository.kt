package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 0: System, 1: Light, 2: Dark
class ThemeRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val _themeFlow = MutableStateFlow(prefs.getInt("theme_mode", 0))
    val themeFlow: StateFlow<Int> = _themeFlow.asStateFlow()

    fun setTheme(mode: Int) {
        prefs.edit().putInt("theme_mode", mode).apply()
        _themeFlow.value = mode
    }
}
