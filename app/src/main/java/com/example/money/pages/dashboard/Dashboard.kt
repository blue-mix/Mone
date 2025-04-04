package com.example.money.pages.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.money.components.expenses.ExpensesList
import com.example.money.components.navigation.Routes
import com.example.money.mock.mockExpenses
import com.example.money.ui.theme.TopAppBarBackground
import com.example.money.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val state by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Dashboard") },
                actions = { Icon(Icons.Default.MoreVert, null) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = TopAppBarBackground)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {

                StatCard(
                    title = "Total Income",
                    value = "₹${"%,.2f".format(state.totalIncome)}",
                    percentageChange = "↑ ${"%.1f".format(state.incomeChangePercent)}%",
                    percentageColor = Color(0xFF2E7D32),
                    icon = Icons.Default.AttachMoney,
                    backgroundColor = Color(0xFF00E676),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Total Expense",
                    value = "₹${"%,.2f".format(state.totalExpense)}",
                    percentageChange = "↓ ${"%.1f".format(state.expenseChangePercent)}%",
                    percentageColor = Color(0xFFD32F2F),
                    icon = Icons.Default.ShoppingCart,
                    iconBackground = Color.White.copy(alpha = 0.3f),
                    valueColor = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FinanceStatsCard(
                incomeData = state.incomeMonthly,
                expenseData = state.expenseMonthly,
                labels = state.monthLabels
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                TextButton(onClick = { navController.navigate(Routes.Expenses.route) }) {
                    Text("See All")
                }
            }


            ExpensesList(state.recentExpenses)
        }
    }
}

