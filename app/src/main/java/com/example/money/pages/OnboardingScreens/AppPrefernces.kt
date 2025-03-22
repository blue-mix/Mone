package com.example.money.pages.OnboardingScreens
//
//import android.content.Context
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.preferencesDataStore
//import com.example.money.dataStore
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//
//val Context.dataStore by preferencesDataStore(name = "settings")
//object AppPreferences {
//    private val CATEGORIES_SEEDED_KEY = booleanPreferencesKey("categories_seeded")
//    private val KEY_KEYWORD_MAPPINGS_SEEDED = booleanPreferencesKey("keyword_mappings_seeded")
//    fun areCategoriesSeeded(context: Context): Flow<Boolean> {
//        return context.dataStore.data.map { prefs ->
//            prefs[CATEGORIES_SEEDED_KEY] ?: false
//        }
//    }
//
//    suspend fun setCategoriesSeeded(context: Context, seeded: Boolean) {
//        context.dataStore.edit { prefs ->
//            prefs[CATEGORIES_SEEDED_KEY] = seeded
//        }
//    }
//    fun areKeywordMappingsSeeded(context: Context): Flow<Boolean> {
//        return context.dataStore.data.map { prefs ->
//            prefs[KEY_KEYWORD_MAPPINGS_SEEDED] ?: false
//        }
//    }
//
//    suspend fun setKeywordMappingsSeeded(context: Context, value: Boolean) {
//        context.dataStore.edit { prefs ->
//            prefs[KEY_KEYWORD_MAPPINGS_SEEDED] = value
//        }
//    }
//}
