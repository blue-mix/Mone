package com.example.money

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.money.pages.Add
import com.example.money.pages.Categories
import com.example.money.pages.Expenses
import com.example.money.pages.Reports
import com.example.money.pages.Settings
import com.example.money.ui.theme.MoneyTheme
import com.example.money.ui.theme.TopAppBarBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()

                var showBottomBar by rememberSaveable { mutableStateOf(true) }

                // Ensure showBottomBar updates only when the navigation changes
                LaunchedEffect(backStackEntry?.destination?.route) {
                    showBottomBar =
                        backStackEntry?.destination?.route !in listOf("settings/categories")
                }
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController)
                        }
                    },
                    content = { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "expenses",
                            modifier = Modifier.padding(innerPadding) // Apply padding globally
                        ) {
                            composable("expenses") { Expenses(navController) }
                            composable("reports") { Reports() }
                            composable("add") { Add(navController) }
                            composable("settings") { Settings(navController) }
                            composable("settings/categories") { Categories(navController) }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar(containerColor = TopAppBarBackground) {
        NavigationBarItem(
            selected = backStackEntry?.destination?.route == "expenses",
            onClick = { navController.navigate("expenses") },
            label = { Text("Expenses") },
            icon = {
                Icon(painterResource(id = R.drawable.upload), contentDescription = "Upload")
            }
        )
        NavigationBarItem(
            selected = backStackEntry?.destination?.route == "reports",
            onClick = { navController.navigate("reports") },
            label = { Text("Reports") },
            icon = {
                Icon(painterResource(id = R.drawable.bar_chart), contentDescription = "Reports")
            }
        )
        NavigationBarItem(
            selected = backStackEntry?.destination?.route == "add",
            onClick = { navController.navigate("add") },
            label = { Text("Add") },
            icon = {
                Icon(painterResource(id = R.drawable.add), contentDescription = "Add")
            }
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