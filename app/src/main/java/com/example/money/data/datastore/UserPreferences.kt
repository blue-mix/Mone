package com.example.money.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.money.dataStore

object UserPreferences {
    private val CURRENCY_KEY = stringPreferencesKey("selected_currency")

    fun getSelectedCurrency(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[CURRENCY_KEY] ?: "USD" // Default to USD
        }
    }

    suspend fun saveSelectedCurrency(context: Context, currency: String) {
        context.dataStore.edit { settings ->
            settings[CURRENCY_KEY] = currency
        }
    }
}

