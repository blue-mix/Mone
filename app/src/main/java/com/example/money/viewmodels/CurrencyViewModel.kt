package com.example.money.viewmodels


//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.money.data.datastore.UserPreferences
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.launch
//
//class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
//    val selectedCurrency: Flow<String> = UserPreferences.getSelectedCurrency(application)
//
//    fun saveSelectedCurrency(currency: String) {
//        viewModelScope.launch {
//            UserPreferences.saveSelectedCurrency(getApplication(), currency)
//        }
//    }
//}

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedCurrency = MutableStateFlow("USD") // Default currency
    val selectedCurrency = _selectedCurrency.asStateFlow() // Expose as immutable Flow

    init {
        viewModelScope.launch {
            UserPreferences.getSelectedCurrency(application).collect { currency ->
                _selectedCurrency.value = currency // Update when DataStore emits new value
            }
        }
    }

    fun updateCurrency(newCurrency: String) {
        viewModelScope.launch {
            UserPreferences.saveSelectedCurrency(getApplication(), newCurrency)
        }
    }

    fun saveSelectedCurrency(newValue: String) {

    }
}

