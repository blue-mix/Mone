package com.example.money

import android.app.Application
import com.example.money.data.seedDefaultCategoriesIfNeeded
import com.example.money.data.seedDefaultKeywordMappingsIfEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Fire off one-time seeding
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultCategoriesIfNeeded(applicationContext)
            seedDefaultKeywordMappingsIfEmpty(applicationContext)
        }
    }
}
