
package com.example.money.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import com.example.money.dataStore
import kotlinx.coroutines.flow.*


object OnboardingPrefs {
    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")

    fun isFirstLaunch(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: true // Default to true (first launch)
        }
    }

    suspend fun saveOnboardingCompleted(context: Context) {
        context.dataStore.edit { settings ->
            settings[ONBOARDING_COMPLETED_KEY] = false
        }
    }
}

