package com.example.money

import BottomNavItem
import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
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
import androidx.core.content.ContextCompat
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
import com.example.money.ui.theme.MoneyTheme
import com.example.money.viewmodels.AddViewModel
import com.example.money.viewmodels.AnalyticsViewModel
import com.example.money.viewmodels.CurrencyViewModel
import com.example.money.viewmodels.ExpensesViewModel
import com.example.money.viewmodels.KeywordMappingViewModel
import com.example.money.viewmodels.OnboardingViewModel

// DataStore for saving onboarding progress
//1
//val Context.dataStore by preferencesDataStore(name = "settings")
//
//class MainActivity : ComponentActivity() {
//    private val SMS_PERMISSION_CODE = 101
//
//    private val requestSmsPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            val granted = permissions.entries.all { it.value }
//            if (granted) {
//                Toast.makeText(this, "SMS Permission Granted ✅", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "SMS Permission Denied ❌", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    private fun requestSmsPermission() {
//        // ✅ Only request permission if it's not already granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
//            != PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            requestSmsPermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.READ_SMS,
//                    Manifest.permission.RECEIVE_SMS
//                )
//            )
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        lifecycleScope.launch {
//            seedDefaultCategoriesIfNeeded(applicationContext)
//            seedDefaultKeywordMappingsIfEmpty(applicationContext)
//        }
//        enableEdgeToEdge()
//
//
//        setContent {
//
//            val navController = rememberNavController()
//            val onboardingViewModel = OnboardingViewModel(application)
//            val currencyViewModel = CurrencyViewModel(application)
//            val selectedCurrency by currencyViewModel.selectedCurrency.collectAsState()
//
//            val isFirstTime by onboardingViewModel.isFirstLaunch.collectAsState()
//            val repository = SmsExpenseRepository(applicationContext)
//            val expensesViewModel = remember {  ExpensesViewModel(repository)}
//
//            MoneyTheme {
//                val rootnavController = navController
//                val startDestination =
//                    if (!isFirstTime) Routes.Home.route else Routes.Onboarding.route
//
//                NavHost(
//                    navController = rootnavController,
//                    startDestination = startDestination
//                ) {
//                    composable(Routes.Onboarding.route) {
//                        OnBoardingScreen(
//                            navController,
//                            onboardingViewModel,
//                            currencyViewModel
//                        )
//                    }
//                    composable(Routes.Home.route) {
//                        MainScaffold(
//                            rootNavController = navController,
//                            currencyViewModel = currencyViewModel,
//                            expensesViewModel = expensesViewModel
//                        )
//                    }
//                    composable(Routes.Categories.route) {
//                        Categories(rootnavController)
//                    }
//                    composable(Routes.Keywords.route) {
//                        KeywordMappingEditor(
//                            rootnavController,
//                            viewModel = KeywordMappingViewModel()
//                        )
//                    }
//                    composable(Routes.Add.route) {
//                        Add(navController, vm = AddViewModel())
//                    }
//                }
//
//            }
//
//        }
//        requestSmsPermission()
//
//    }
//}
//
////  Main Screen with Bottom Navigation
//@Composable
//fun MainScaffold(rootNavController: NavController, currencyViewModel: CurrencyViewModel,expensesViewModel: ExpensesViewModel) {
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = backStackEntry?.destination?.route
//    val expensesList = remember { mutableStateListOf<Expense>() }
//    val context = LocalContext.current
//    val analyticsViewModel = AnalyticsViewModel()
////    LaunchedEffect(Unit) {
////        val fetchedExpenses = readSmsInbox(context)
//////        Log.d("SMS_Expenses", "Fetched transactions: ${fetchedExpenses.size}") // Debug Log
////        fetchedExpenses.forEach { expense ->
////            if (expense !in expensesList) { // ✅ Prevent duplicates manually
////                expensesList.add(expense)
////            }
////        }
////    }
//
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(navController = navController, items = bottomNavItems)
//        },
//        content = { innerPadding ->
//            NavHost(
//                navController = navController,
//                startDestination = Routes.Expenses.route,
//                modifier = Modifier.padding(innerPadding)
//            ) {
//                composable(Routes.Expenses.route) {
//                    Expenses(
//                        rootNavController,
//                        expensesViewModel
//                    )
//                }
//
//                composable(Routes.Add.route) {
//                    Add(rootNavController)
//                }
//
//                composable(Routes.Analytics.route) {
//                    AnalyticsPage(navController, expensesViewModel, analyticsViewModel )
//                }
//
//                composable(Routes.Settings.route) {
//                    Settings(rootNavController)
//                }
//
//            }
//        }
//    )
//}
//
////fun readSmsInbox(context: Context): List<Expense> {
////    val expenses = mutableListOf<Expense>()
////    val uri: Uri = Uri.parse("content://sms/inbox")
////    val projection = arrayOf("_id", "address", "date", "body")
////
////
////    val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, "date DESC")
////    cursor?.use {
////        val indexBody = it.getColumnIndex("body")
////        while (it.moveToNext()) {
////            val smsBody = it.getString(indexBody)
////            val parsedTransaction = SmsParser.parseSms(smsBody)
////
////
////            if (parsedTransaction != null) {
////                val expense = parsedTransaction.date?.let { it1 ->
////                    Expense.create(
////                        amount = if (parsedTransaction.type == "credit") parsedTransaction.amount else -parsedTransaction.amount,
////                        date = it1.atStartOfDay(),  // Convert parsed date if needed
////                        recurrence = Recurrence.None,
////                        note = parsedTransaction.merchant,
////                        category = mapMerchantToCategory(
////                            parsedTransaction.merchant,
////                            db
////                        ) // You can map it dynamically
////                    )
////                }
////                if (expense != null) {
////                    expenses.add(expense)
////                }
////            }
////        }
////    }
////    return expenses
////}

//1
//@Composable
//fun BottomNavigationBar(
//    navController: NavController,
//    items: List<BottomNavItem>
//) {
//    val currentBackStack by navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStack?.destination?.route
//
//    NavigationBar {
//        items.forEach { item ->
//            NavigationBarItem(
//                selected = currentRoute == item.route,
//                onClick = {
//                    if (currentRoute != item.route) {
//                        navController.navigate(item.route) {
//                            popUpTo(navController.graph.startDestinationRoute ?: "") {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                },
//                icon = {
//                    Icon(
//                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
//                        contentDescription = item.title
//                    )
//                },
//                label = { Text(item.title) }
//            )
//        }
//    }
//}

//2
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.slideInVertically
//import androidx.compose.animation.slideOutVertically
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.compose.*
//import com.example.money.pages.*


//class MainActivity : ComponentActivity() {
//
//    @OptIn(ExperimentalMaterial3Api::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        setContent {
//            MoneyTheme {
//                val navController = rememberNavController()
//                val backStackEntry by navController.currentBackStackEntryAsState()
//
//                val bottomBarVisible = rememberSaveable { mutableStateOf(true) }
//                val fabVisible = rememberSaveable { mutableStateOf(true) }
//
//                // 👇 Change visibility based on current route
//                when (backStackEntry?.destination?.route) {
//                    Routes.Expenses.route -> {
//                        bottomBarVisible.value = true
//                        fabVisible.value = true
//                    }
//
//                    Routes.Add.route -> {
//                        bottomBarVisible.value = false
//                        fabVisible.value = false
//                    }
//
//                    Routes.Analytics.route, Routes.Settings.route -> {
//                        bottomBarVisible.value = true
//                        fabVisible.value = false
//                    }
//
//                    Routes.Categories.route, Routes.Keywords.route -> {
//                        bottomBarVisible.value = false
//                        fabVisible.value = false
//                    }
//
//                    else -> {
//                        bottomBarVisible.value = false
//                        fabVisible.value = false
//                    }
//                }
//
//                val items = listOf(
//                    BottomNavItem("Expenses", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, Routes.Expenses.route),
//                    BottomNavItem("Analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics, Routes.Analytics.route),
//                    BottomNavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, Routes.Settings.route)
//                )
//
//                Scaffold(
//                    floatingActionButton = {
//                        AnimatedVisibility(
//                            visible = fabVisible.value,
//                            enter = fadeIn(tween(100)),
//                            exit = fadeOut(tween(100))
//                        ) {
//                            FloatingActionButton(
//                                onClick = { navController.navigate(Routes.Add.route) }
//                            ) {
//                                Icon(Icons.Default.Add, contentDescription = "Add Expense")
//                            }
//                        }
//                    },
//                    bottomBar = {
//                        AnimatedVisibility(
//                            visible = bottomBarVisible.value,
//                            enter = slideInVertically(initialOffsetY = { it }),
//                            exit = slideOutVertically(targetOffsetY = { it })
//                        ) {
//                            NavigationBar {
//                                items.forEach { item ->
//                                    NavigationBarItem(
//                                        selected = backStackEntry?.destination?.route == item.route,
//                                        onClick = {
//                                            navController.navigate(item.route) {
//                                                popUpTo(navController.graph.findStartDestination().id) {
//                                                    saveState = true
//                                                }
//                                                launchSingleTop = true
//                                                restoreState = true
//                                            }
//                                        },
//                                        icon = {
//                                            Icon(
//                                                imageVector = if (backStackEntry?.destination?.route == item.route)
//                                                    item.selectedIcon else item.unselectedIcon,
//                                                contentDescription = item.title
//                                            )
//                                        },
//                                        label = {
//                                            Text(text = item.title)
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                ) {
//                    NavHost(
//                        navController = navController,
//                        startDestination = Routes.Expenses.route,
//                        modifier = Modifier.padding(it),
//                        enterTransition = { fadeIn(tween(150)) },
//                        exitTransition = { fadeOut(tween(150)) }
//                    ) {
//                        composable(Routes.Expenses.route) { Expenses(navController) }
//                        composable(Routes.Add.route) { AddExpense(navController) }
//                        composable(Routes.Analytics.route) { AnalyticsPage(navController) }
//                        composable(Routes.Settings.route) { Settings(navController) }
//                        composable(Routes.Categories.route) { Categories(navController) }
//                        composable(Routes.Keywords.route) { KeywordMappingEditor(navController, viewModel = KeywordMappingViewModel()) }
//                    }
//                }
//            }
//        }
//    }
//}

//3rd

class MainActivity : ComponentActivity() {
    private val smsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            Toast.makeText(
                this,
                if (granted) "SMS Permission Granted ✅" else "SMS Permission Denied ❌",
                Toast.LENGTH_SHORT
            ).show()
        }

    private fun requestSmsPermissions() {
        val permissions = listOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
        val notGranted = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (notGranted) smsPermissionLauncher.launch(permissions.toTypedArray())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestSmsPermissions()

        setContent {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()

            val onboardingViewModel = OnboardingViewModel(application)
            val currencyViewModel = CurrencyViewModel(application)
            val expensesViewModel = remember { ExpensesViewModel(SmsExpenseRepository(application)) }

            val isFirstLaunch by onboardingViewModel.isFirstLaunch.collectAsState()
            val startDestination = if (isFirstLaunch) Routes.Onboarding.route else Routes.Expenses.route

            val showBottomBar = rememberSaveable { mutableStateOf(true) }
            val showFab = rememberSaveable { mutableStateOf(true) }

            // Determine visibility
            when (backStackEntry?.destination?.route) {
                Routes.Expenses.route -> {
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
                else -> {
                    showBottomBar.value = false
                    showFab.value = false
                }
            }

            val items = listOf(
                BottomNavItem("Expenses", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, Routes.Expenses.route),
                BottomNavItem("Analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics, Routes.Analytics.route),
                BottomNavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, Routes.Settings.route)
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
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Routes.Onboarding.route) {
                            OnBoardingScreen(navController, onboardingViewModel, currencyViewModel)
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
                    }
                }
            }
        }
    }
}
