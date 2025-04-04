package com.example.money.pages.OnboardingScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.money.R
import com.example.money.components.navigation.Routes
import com.example.money.viewmodels.OnboardingViewModel
import com.example.money.viewmodels.CurrencyViewModel
import com.example.money.viewmodels.ExpensesViewModel
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel,
    currencyViewModel: CurrencyViewModel,
    expensesViewModel: ExpensesViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 4 }) // 4 Pages now
    val coroutineScope = rememberCoroutineScope()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> OnBoardingPage(
                        imageRes = R.drawable.graph,
                        title = "Track Your Expenses",
                        description = "Stay on top of your budget."
                    )
                    1 -> CurrencySelectionPage(currencyViewModel)
                    2 -> PermissionRequestPage(

                        onPermissionGranted = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },expensesViewModel)
                    3 -> OnBoardingPage(
                        imageRes = R.drawable.piggy_bank,
                        title = "Analyze Your Data",
                        description = "Get insights into your spending habits."
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = when (pagerState.currentPage) {
                    3 -> Arrangement.Center
                    else -> Arrangement.SpaceBetween
                }
            ) {
                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Previous")
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                if (pagerState.currentPage < 3) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Next")
                    }
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                onboardingViewModel.markOnboardingCompleted()
                                navController.navigate(Routes.Expenses.route) {
                                    popUpTo(Routes.Onboarding.route) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 48.dp)
                    ) {
                        Text("Get Started")
                    }
                }
            }
        }
    }
}
