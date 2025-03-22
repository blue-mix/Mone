package com.example.money

// Setup DataStore for onboarding preference
//val Context.dataStore by preferencesDataStore(name = "settings")
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        lifecycleScope.launch {
//            val isFirstTime = checkIfFirstTime(this@MainActivity)
//
//            setContent {
//                MoneyTheme {
//                    val navController = rememberNavController()
//                    val backStackEntry by navController.currentBackStackEntryAsState()
//
//                    var showBottomBar by rememberSaveable { mutableStateOf(true) }
//
//                    // Ensure showBottomBar updates only when the navigation changes
//                    LaunchedEffect(backStackEntry?.destination?.route) {
//                        showBottomBar =
//                            backStackEntry?.destination?.route !in listOf("settings/categories")
//                    }
//                    Scaffold(
//                        bottomBar = {
//                            if (showBottomBar) {
//                                BottomNavigationBar(navController)
//                            }
//                        },
//                        content = { innerPadding ->
//                            NavHost(
//                                navController = navController,
//                                startDestination = if (isFirstTime) "Onboarding" else "home",
//                                modifier = Modifier.padding(innerPadding) // Apply padding globally
//                            ) {
//                                composable("home") { HomeScreen(navController) }
//                                composable("Onboarding") { OnBoardingScreen(navController) }
////                                composable("expenses") { Expenses(navController) }
////                                composable("reports") { Reports() }
////                                composable("add") { Add(navController) }
////                                composable("settings") { Settings(navController) }
////                                composable("settings/categories") { Categories(navController) }
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    // Function to check if onboarding is completed
//    suspend fun checkIfFirstTime(context: Context): Boolean {
//        val preferences = context.dataStore.data.first()
//        return preferences[booleanPreferencesKey("onboarding_completed")] != true
//    }
//
//    // Function to save onboarding completion state
//    suspend fun saveOnboardingCompletion(context: Context) {
//        context.dataStore.edit { settings ->
//            settings[booleanPreferencesKey("onboarding_completed")] = true
//        }
//    }
//
//    @Composable
//    fun BottomNavigationBar(navController: NavController) {
//        val backStackEntry by navController.currentBackStackEntryAsState()
//
//        NavigationBar(containerColor = TopAppBarBackground) {
//            NavigationBarItem(
//                selected = backStackEntry?.destination?.route == "expenses",
//                onClick = { navController.navigate("expenses") },
//                label = { Text("Expenses") },
//                icon = {
//                    Icon(painterResource(id = R.drawable.upload), contentDescription = "Upload")
//                }
//            )
//            NavigationBarItem(
//                selected = backStackEntry?.destination?.route == "reports",
//                onClick = { navController.navigate("reports") },
//                label = { Text("Reports") },
//                icon = {
//                    Icon(painterResource(id = R.drawable.bar_chart), contentDescription = "Reports")
//                }
//            )
//            NavigationBarItem(
//                selected = backStackEntry?.destination?.route == "add",
//                onClick = { navController.navigate("add") },
//                label = { Text("Add") },
//                icon = {
//                    Icon(painterResource(id = R.drawable.add), contentDescription = "Add")
//                }
//            )
//            NavigationBarItem(
//                selected = backStackEntry?.destination?.route?.startsWith("settings") == true,
//                onClick = { navController.navigate("settings") },
//                label = { Text("Settings") },
//                icon = {
//                    Icon(
//                        painterResource(id = R.drawable.settings_outlined),
//                        contentDescription = "Settings"
//                    )
//                }
//            )
//        }
//    }
//}
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.money.mock.getRandomColor
import com.example.money.models.Category
import com.example.money.models.Expense
import com.example.money.models.Recurrence
import com.example.money.pages.Add
import com.example.money.pages.AnalyticsPage
import com.example.money.pages.Categories
import com.example.money.pages.Expenses
import com.example.money.pages.KeywordMappingEditor
import com.example.money.pages.OnboardingScreens.OnBoardingScreen
import com.example.money.pages.Reports
import com.example.money.pages.Settings
import com.example.money.ui.theme.MoneyTheme
import com.example.money.utils.SmsParser
import com.example.money.utils.mapMerchantToCategory
import com.example.money.viewmodels.CurrencyViewModel
import com.example.money.viewmodels.ExpensesViewModel
import com.example.money.viewmodels.KeywordMappingViewModel
import com.example.money.viewmodels.OnboardingViewModel
import com.example.money.viewmodels.ReportsViewModel
import kotlinx.coroutines.launch


// DataStore for saving onboarding progress
val Context.dataStore by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private val SMS_PERMISSION_CODE = 101

    private val requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                Toast.makeText(this, "SMS Permission Granted ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS Permission Denied ❌", Toast.LENGTH_SHORT).show()
            }
        }


    private fun requestSmsPermission() {
        // ✅ Only request permission if it's not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestSmsPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            seedDefaultCategoriesIfNeeded(applicationContext)
            seedDefaultKeywordMappingsIfEmpty(applicationContext)
        }
        enableEdgeToEdge()


        setContent {

            val navController = rememberNavController()
            val onboardingViewModel = OnboardingViewModel(application)
            val currencyViewModel = CurrencyViewModel(application)
            val selectedCurrency by currencyViewModel.selectedCurrency.collectAsState()

            val isFirstTime by onboardingViewModel.isFirstLaunch.collectAsState()

            MoneyTheme {
                val navController = navController
                val startDestination = if (isFirstTime) "onboarding" else "home"

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("onboarding") {
                        OnBoardingScreen(
                            navController,
                            onboardingViewModel,
                            currencyViewModel
                        )
                    }
                    composable("home") {
                        MainScreen(
                            navController,
                            currencyViewModel
                        )
                    } // Loads Bottom Navigation
                    composable("settings/categories") { Categories(navController) }
                    composable ("settings/keyword"){ KeywordMappingEditor(navController , viewModel = KeywordMappingViewModel())  }
                }

            }

        }
        requestSmsPermission()

    }
}

//  Main Screen with Bottom Navigation
@Composable
fun MainScreen(navController: NavController, currencyViewModel: CurrencyViewModel) {
    val bottomNavController = rememberNavController()
    val backStackEntry by bottomNavController.currentBackStackEntryAsState()

    val expensesList = remember { mutableStateListOf<Expense>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val fetchedExpenses = readSmsInbox(context)
        Log.d("SMS_Expenses", "Fetched transactions: ${fetchedExpenses.size}") // Debug Log
        fetchedExpenses.forEach { expense ->
            if (expense !in expensesList) { // ✅ Prevent duplicates manually
                expensesList.add(expense)
            }
        }
    }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(bottomNavController)
        },
        content = { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = "expenses",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("expenses") {
                    Expenses(
                        navController,
                        expensesList,
                        vm = ExpensesViewModel(),
                        currencyViewModel
                    )
                }
                //composable("reports") { Reports(vm = ReportsViewModel(), currencyViewModel) }
                composable("add") { Add(navController) }
                composable("analytics") {
                    AnalyticsPage(navController = bottomNavController, expenses = expensesList)
                }
                composable("settings") { Settings(navController) }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
        NavigationBarItem(
            selected = backStackEntry?.destination?.route == "expenses",
            onClick = { navController.navigate("expenses") },
            label = { Text("Expenses") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.upload),
                    contentDescription = "Expenses"
                )
            }
        )
//        NavigationBarItem(
//            selected = backStackEntry?.destination?.route == "reports",
//            onClick = { navController.navigate("reports") },
//            label = { Text("Reports") },
//            icon = {
//                Icon(
//                    painterResource(id = R.drawable.bar_chart),
//                    contentDescription = "Reports"
//                )
//            }
//        )
        NavigationBarItem(
            selected = backStackEntry?.destination?.route == "analytics",
            onClick = { navController.navigate("analytics") },
            label = { Text("Analytics") },
            icon = {
                Icon(painterResource(id = R.drawable.analytics), contentDescription = "Analytics")
            }
        )
        NavigationBarItem(
            selected = backStackEntry?.destination?.route == "add",
            onClick = { navController.navigate("add") },
            label = { Text("Add") },
            icon = { Icon(painterResource(id = R.drawable.add), contentDescription = "Add") }
        )
        NavigationBarItem(
            selected = backStackEntry?.destination?.route?.startsWith("settings") == true,
            onClick = { navController.navigate("settings") },
            label = { Text("Settings") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.settings_outlined),
                    contentDescription = "Settings"
                )
            }
        )
    }
}

fun readSmsInbox(context: Context): List<Expense> {
    val expenses = mutableListOf<Expense>()
    val uri: Uri = Uri.parse("content://sms/inbox")
    val projection = arrayOf("_id", "address", "date", "body")


    val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, "date DESC")
    cursor?.use {
        val indexBody = it.getColumnIndex("body")
        while (it.moveToNext()) {
            val smsBody = it.getString(indexBody)
            val parsedTransaction = SmsParser.parseSms(smsBody)


            if (parsedTransaction != null) {
                val expense = parsedTransaction.date?.let { it1 ->
                    Expense.create(
                        amount = if (parsedTransaction.type == "credit") parsedTransaction.amount else -parsedTransaction.amount,
                        date = it1.atStartOfDay(),  // Convert parsed date if needed
                        recurrence = Recurrence.None,
                        note = "Transaction with ${parsedTransaction.merchant}",
                        category = mapMerchantToCategory(
                            parsedTransaction.merchant,
                             db
                        ) // You can map it dynamically
                    )
                }
                if (expense != null) {
                    expenses.add(expense)
                }
            }
        }
    }
    return expenses
}

