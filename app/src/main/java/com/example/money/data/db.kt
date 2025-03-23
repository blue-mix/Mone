package com.example.money.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.money.data.models.Category
import com.example.money.data.models.Expense
import com.example.money.data.models.KeywordMapping
import com.example.money.data.datastore.AppPreferences
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.flow.first


val config = RealmConfiguration.Builder(schema = setOf(Expense::class, Category::class))



// Singleton Realm Database Instance
object Database {
    private val config = RealmConfiguration.Builder(
        schema = setOf(Expense::class, Category::class, KeywordMapping::class)
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

val defaultMappings = listOf(
    // 🛒 Food & Dining
    KeywordMapping.create("zomato", "Food"),
    KeywordMapping.create("swiggy", "Food"),
    KeywordMapping.create("dominos", "Food"),
    KeywordMapping.create("mcdonalds", "Food"),
    KeywordMapping.create("pizza hut", "Food"),
    KeywordMapping.create("ubereats", "Food"),
    KeywordMapping.create("kfc", "Food"),
    KeywordMapping.create("burger king", "Food"),
    KeywordMapping.create("cafecoffee", "Food"),
    KeywordMapping.create("starbucks", "Food"),

    // 🚗 Transport
    KeywordMapping.create("uber", "Transport"),
    KeywordMapping.create("ola", "Transport"),
    KeywordMapping.create("rapido", "Transport"),
    KeywordMapping.create("redbus", "Transport"),
    KeywordMapping.create("blablacar", "Transport"),
    KeywordMapping.create("chalo", "Transport"),
    KeywordMapping.create("irctc", "Transport"),
    KeywordMapping.create("railways", "Transport"),

    // 🛍️ Shopping
    KeywordMapping.create("amazon", "Shopping"),
    KeywordMapping.create("flipkart", "Shopping"),
    KeywordMapping.create("myntra", "Shopping"),
    KeywordMapping.create("ajio", "Shopping"),
    KeywordMapping.create("nykaa", "Shopping"),
    KeywordMapping.create("meesho", "Shopping"),
    KeywordMapping.create("snapdeal", "Shopping"),

    // 🎬 Entertainment
    KeywordMapping.create("netflix", "Entertainment"),
    KeywordMapping.create("hotstar", "Entertainment"),
    KeywordMapping.create("prime video", "Entertainment"),
    KeywordMapping.create("spotify", "Entertainment"),
    KeywordMapping.create("gaana", "Entertainment"),
    KeywordMapping.create("zee5", "Entertainment"),
    KeywordMapping.create("bookmyshow", "Entertainment"),
    KeywordMapping.create("sonyliv", "Entertainment"),

    // 🧾 Bills
    KeywordMapping.create("electricity", "Bills"),
    KeywordMapping.create("recharge", "Bills"),
    KeywordMapping.create("broadband", "Bills"),
    KeywordMapping.create("airtel", "Bills"),
    KeywordMapping.create("jio", "Bills"),
    KeywordMapping.create("bsnl", "Bills"),
    KeywordMapping.create("datacard", "Bills"),
    KeywordMapping.create("tatasky", "Bills"),
    KeywordMapping.create("dth", "Bills"),
    KeywordMapping.create("postpaid", "Bills"),

    // ✈️ Travel
    KeywordMapping.create("makemytrip", "Travel"),
    KeywordMapping.create("goibibo", "Travel"),
    KeywordMapping.create("yatra", "Travel"),
    KeywordMapping.create("airindia", "Travel"),
    KeywordMapping.create("indigo", "Travel"),
    KeywordMapping.create("vistara", "Travel"),
    KeywordMapping.create("cleartrip", "Travel"),

    // 💼 Income
    KeywordMapping.create("salary", "Income"),
    KeywordMapping.create("stipend", "Income"),
    KeywordMapping.create("credit", "Income"),
    KeywordMapping.create("deltecs", "Income"),
    KeywordMapping.create("rebate", "Income"),
    KeywordMapping.create("incentive", "Income"),
    KeywordMapping.create("bonus", "Income"),
    KeywordMapping.create("refund", "Income"),

    // 🏧 ATM & Cash
    KeywordMapping.create("atm", "Cash Withdrawal"),
    KeywordMapping.create("cash", "Cash Withdrawal"),

    // 🔄 Transfers
    KeywordMapping.create("upi", "Transfers"),
    KeywordMapping.create("neft", "Transfers"),
    KeywordMapping.create("rtgs", "Transfers"),
    KeywordMapping.create("imps", "Transfers"),
    KeywordMapping.create("phonepe", "Transfers"),
    KeywordMapping.create("gpay", "Transfers"),
    KeywordMapping.create("google pay", "Transfers"),
    KeywordMapping.create("paytm", "Transfers"),
    KeywordMapping.create("bhim", "Transfers"),

    // 🏥 Health
    KeywordMapping.create("pharmacy", "Health"),
    KeywordMapping.create("medplus", "Health"),
    KeywordMapping.create("apollo", "Health"),
    KeywordMapping.create("practo", "Health"),
    KeywordMapping.create("1mg", "Health"),
    KeywordMapping.create("pharmeasy", "Health"),

    // 🧑‍🎓 Scholarship
    KeywordMapping.create("govt", "Scholarship"),

    // 🏠 Rent
    KeywordMapping.create("rent", "Rent"),
    KeywordMapping.create("no broker", "Rent"),
    KeywordMapping.create("housing.com", "Rent")
    // Add more as needed
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

suspend fun seedDefaultKeywordMappingsIfEmpty(context: Context) {
    val seeded = AppPreferences.areKeywordMappingsSeeded(context).first()
    if (!seeded) {
    val realm = Database.db
    val existing = realm.query(KeywordMapping::class).find()
    if (existing.isEmpty()) {
        realm.writeBlocking {
            defaultMappings.forEach {
                copyToRealm(it)
            }
        }
        AppPreferences.setKeywordMappingsSeeded(context, true)
    }
    }
}

