package com.example.money.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val currencyState = MutableStateFlow("INR")
    val onboardingCompleted = MutableStateFlow(false)
}
