package com.example.money.components.navigation

sealed class Routes(val route: String) {
    object Onboarding : Routes("onboarding")
    object Home : Routes("home")
    object Add : Routes("add")
    object Expenses : Routes("expenses")
    object Settings : Routes("settings")
    object Categories : Routes("settings/categories")
    object Keywords : Routes("settings/keyword")
    object Analytics : Routes("analytics")
}
