package com.example.money

import BottomNavItem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.money.components.navigation.Routes
import com.example.money.data.repository.SmsExpenseRepository
import com.example.money.pages.Add
import com.example.money.pages.AnalyticsPage
import com.example.money.pages.Categories
import com.example.money.pages.Expenses
import com.example.money.pages.KeywordMappingEditor
import com.example.money.pages.OnboardingScreens.OnBoardingScreen
import com.example.money.pages.Settings
import com.example.money.pages.dashboard.DashboardScreen
import com.example.money.pages.spending.SpendingBreakdownScreen
import com.example.money.ui.theme.MoneyTheme
import com.example.money.viewmodels.AddViewModel
import com.example.money.viewmodels.AnalyticsViewModel
import com.example.money.viewmodels.CurrencyViewModel
import com.example.money.viewmodels.DashboardViewModel
import com.example.money.viewmodels.ExpensesViewModel
import com.example.money.viewmodels.KeywordMappingViewModel
import com.example.money.viewmodels.OnboardingViewModel
import com.example.money.viewmodels.SpendingViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()

            val onboardingViewModel = OnboardingViewModel(application)
            val currencyViewModel = CurrencyViewModel(application)
            val expensesViewModel =
                remember { ExpensesViewModel(SmsExpenseRepository(application), application) }

            val isFirstLaunch by onboardingViewModel.isFirstLaunch.collectAsState()
            val startDestination =
                if (isFirstLaunch) Routes.Onboarding.route else Routes.Dashboard.route

            val showBottomBar = rememberSaveable { mutableStateOf(true) }
            val showFab = rememberSaveable { mutableStateOf(true) }

            // Determine visibility
            when (backStackEntry?.destination?.route) {
                Routes.Expenses.route -> {
                    showBottomBar.value = true
                    showFab.value = false
                }

                Routes.Dashboard.route -> {
                    showBottomBar.value = true
                    showFab.value = false
                }

                Routes.Add.route -> {
                    showBottomBar.value = false
                    showFab.value = false
                }

                Routes.Analytics.route, Routes.Settings.route -> {
                    showBottomBar.value = true
                    showFab.value = false
                }

                Routes.Categories.route, Routes.Keywords.route -> {
                    showBottomBar.value = false
                    showFab.value = false
                }

                Routes.Spending.route -> {
                    showBottomBar.value = true
                    showFab.value = false
                }

                else -> {
                    showBottomBar.value = false
                    showFab.value = false
                }
            }

            val items = listOf(
                BottomNavItem(
                    "Dashboard",
                    Icons.Filled.Home,
                    Icons.Outlined.Home,
                    Routes.Dashboard.route
                ),
                BottomNavItem(
                    "Expenses",
                    Icons.Filled.AccountBalanceWallet,
                    Icons.Outlined.AccountBalanceWallet,
                    Routes.Expenses.route
                ),
                BottomNavItem(
                    "Analytics",
                    Icons.Filled.Analytics,
                    Icons.Outlined.Analytics,
                    Routes.Analytics.route
                ),
                BottomNavItem(
                    "Settings",
                    Icons.Filled.Settings,
                    Icons.Outlined.Settings,
                    Routes.Settings.route
                )
            )

            MoneyTheme {
                Scaffold(
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = showFab.value,
                            enter = fadeIn(tween(100)),
                            exit = fadeOut(tween(100))
                        ) {
                            FloatingActionButton(onClick = {
                                navController.navigate(Routes.Add.route)
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
                            }
                        }
                    },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar.value,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            NavigationBar {
                                items.forEach { item ->
                                    NavigationBarItem(
                                        selected = backStackEntry?.destination?.route == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (backStackEntry?.destination?.route == item.route)
                                                    item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        },
                                        label = { Text(item.title) }
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                            .consumeWindowInsets(paddingValues)
                            .padding(paddingValues)
                            .windowInsetsPadding(WindowInsets.systemBars)

                    ) {
                        composable(Routes.Onboarding.route) {
                            OnBoardingScreen(
                                navController,
                                onboardingViewModel,
                                currencyViewModel,
                                expensesViewModel
                            )
                        }
                        composable(Routes.Dashboard.route) {
                            DashboardScreen(navController, DashboardViewModel(expensesViewModel))
                        }
                        composable(Routes.Expenses.route) {
                            Expenses(navController, expensesViewModel)
                        }
                        composable(Routes.Add.route) {
                            Add(navController, AddViewModel())
                        }
                        composable(Routes.Analytics.route) {
                            AnalyticsPage(navController, expensesViewModel, AnalyticsViewModel())
                        }
                        composable(Routes.Settings.route) {
                            Settings(navController)
                        }
                        composable(Routes.Categories.route) {
                            Categories(navController)
                        }
                        composable(Routes.Keywords.route) {
                            KeywordMappingEditor(navController, KeywordMappingViewModel())
                        }
                        composable(Routes.Spending.route) {
                            SpendingBreakdownScreen(
                                navController,
                                SpendingViewModel(expensesViewModel)
                            )
                        }
                    }
                }
            }
        }
    }
}
