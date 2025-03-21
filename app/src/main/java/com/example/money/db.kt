package com.example.money

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.money.models.Category
import com.example.money.models.Expense
import com.example.money.pages.OnboardingScreens.AppPreferences
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.flow.first

val config = RealmConfiguration.Builder(schema = setOf(Expense::class, Category::class))



// Singleton Realm Database Instance
object Database {
    private val config = RealmConfiguration.Builder(
        schema = setOf(Expense::class, Category::class)
    )
        .deleteRealmIfMigrationNeeded() // Auto-deletes if schema changes (optional)
        .build()


    val db: Realm by lazy { Realm.open(config) }
}

val db = Database.db

val defaultCategories = listOf(
    // 🍔 Essentials
    Category("Food", Color(0xFFE57373)),             // Light Red
    Category("Transport", Color(0xFF64B5F6)),        // Blue
    Category("Shopping", Color(0xFFBA68C8)),         // Purple
    Category("Bills", Color(0xFFFFB74D)),            // Orange

    // 💰 Financial
    Category("Income", Color(0xFF81C784)),           // Green
    Category("Transfers", Color(0xFF4DB6AC)),        // Teal
    Category("Cash Withdrawal", Color(0xFF90A4AE)),  // Blue Gray

    // 🎮 Leisure
    Category("Entertainment", Color(0xFF9575CD)),    // Deep Purple
    Category("Travel", Color(0xFF7986CB)),           // Indigo
    Category("Health", Color(0xFFEF5350)),           // Bright Red
    Category("Education", Color(0xFFFFA726)),        // Deep Orange
    Category("Rent", Color(0xFFBCAAA4)),             // Brownish Gray

    // 💼 Work
    Category("Work", Color(0xFF4FC3F7)),             // Light Blue
    Category("Business", Color(0xFF6D4C41)),         // Dark Brown

    // 🎁 Other
    Category("Scholarship", Color(0xFF8D6E63)),      // Muted Brown
    Category("Utilities", Color(0xFFA1887F)),        // Taupe
    Category("Gifts", Color(0xFFF06292)),            // Pink
    Category("Donations", Color(0xFF81D4FA)),        // Soft Blue

    // ❓ Fallback
    Category("Uncategorized", Color.Gray)
)
suspend fun seedDefaultCategoriesIfNeeded(context: Context) {
    val seeded = AppPreferences.areCategoriesSeeded(context).first()
    if (!seeded) {
        val realm = Database.db
        realm.write {
            defaultCategories.forEach {
                copyToRealm(it)
            }
        }
        AppPreferences.setCategoriesSeeded(context, true)
    }
}

