package com.dariusepure.caractivitylog.ui.theme

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ThemeViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _isDarkMode = MutableStateFlow<Boolean?>(
        if (prefs.contains("is_dark_mode")) prefs.getBoolean("is_dark_mode", false) else null
    )
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleTheme(currentDark: Boolean) {
        val newValue = !currentDark
        _isDarkMode.value = newValue
        prefs.edit().putBoolean("is_dark_mode", newValue).apply()
    }

    fun setDarkMode(enabled: Boolean?) {
        _isDarkMode.value = enabled
        if (enabled == null) {
            prefs.edit().remove("is_dark_mode").apply()
        } else {
            prefs.edit().putBoolean("is_dark_mode", enabled).apply()
        }
    }
}
