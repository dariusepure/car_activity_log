package com.dariusepure.caractivitylog.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {
    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null means system default
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleTheme(currentDark: Boolean) {
        _isDarkMode.value = !currentDark
    }

    fun setDarkMode(enabled: Boolean?) {
        _isDarkMode.value = enabled
    }
}
