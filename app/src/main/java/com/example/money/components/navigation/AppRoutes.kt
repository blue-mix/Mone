package com.example.money.components.navigation

sealed class Routes(val route: String) {

    // Onboarding & Home
    object Onboarding : Routes("onboarding")
    object Home : Routes("home")

    // Bottom Nav Tabs
    object Expenses : Routes("expenses")
    object Analytics : Routes("analytics")
    object Add : Routes("add")
    object Settings : Routes("settings")

    // Nested Settings
    object Categories : Routes("settings/categories")
    object Keywords : Routes("settings/keyword")
    object Currency : Routes("settings/currency")
}
