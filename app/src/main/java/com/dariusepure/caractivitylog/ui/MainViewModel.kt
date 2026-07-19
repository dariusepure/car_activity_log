package com.dariusepure.caractivitylog.ui

import androidx.lifecycle.ViewModel
import com.dariusepure.caractivitylog.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val isUserSignedIn: Boolean
        get() = authRepository.isCurrentlySignedIn
}
