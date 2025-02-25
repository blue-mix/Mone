package com.example.money

import com.example.money.models.Category
import com.example.money.models.Expense
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

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
