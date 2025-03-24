
package com.example.money.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.datastore.OnboardingPrefs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    init {
        viewModelScope.launch {
            OnboardingPrefs.isFirstLaunch(application).collect { isFirstTime ->
                _isFirstLaunch.value = isFirstTime
            }
        }
    }

    fun markOnboardingCompleted() {
        viewModelScope.launch {
            OnboardingPrefs.saveOnboardingCompleted(getApplication())
            _isFirstLaunch.value = false // Ensure UI updates
        }
    }
}

