package com.example.money.pages.OnboardingScreens

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.money.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object AppPreferences {
    private val CATEGORIES_SEEDED_KEY = booleanPreferencesKey("categories_seeded")

    fun areCategoriesSeeded(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[CATEGORIES_SEEDED_KEY] ?: false
        }
    }

    suspend fun setCategoriesSeeded(context: Context, seeded: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CATEGORIES_SEEDED_KEY] = seeded
        }
    }
}
