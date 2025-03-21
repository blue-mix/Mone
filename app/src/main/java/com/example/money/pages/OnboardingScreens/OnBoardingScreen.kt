package com.example.money.pages.OnboardingScreens


//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import com.example.money.R
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.money.data.datastore.OnboardingPrefs
//import com.example.money.viewmodels.CurrencyViewModel
//import com.example.money.viewmodels.OnboardingViewModel
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OnBoardingScreen(navController: NavController,onboardingViewModel: OnboardingViewModel) {
//    val pagerState = rememberPagerState(pageCount = { 3 })
//    val coroutineScope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Welcome to Budget App") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                )
//            )
//        },
//        content = { contentPadding ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(contentPadding)
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                HorizontalPager(
//                    state = pagerState,
//                    modifier = Modifier.weight(1f)
//                ) { page ->
//                    when (page) {
//                        0 -> OnBoardingPage(imageRes = R.drawable.graph, title = "Track your expenses", description = "Stay on top of your budget.")
//                        1 -> CurrencySelectionPage(CurrencyViewModel)
//                        2 -> OnBoardingPage(imageRes = R.drawable.piggy_bank, title = "Sync your data", description = "Backup and sync across devices.")
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = if (pagerState.currentPage == 2) Arrangement.Center else Arrangement.SpaceBetween
//                ) {
//                    if (pagerState.currentPage > 0 && pagerState.currentPage < 2) {
//                        IconButton(onClick = {
//                            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
//                        }) {
//                            Icon(Icons.Filled.ArrowBack, contentDescription = "Previous")
//                        }
//                    } else {
//                        Spacer(modifier = Modifier.size(48.dp)) // Placeholder to balance layout
//                    }
//
//                    if (pagerState.currentPage < 2) {
//                        IconButton(onClick = {
//                            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
//                        }) {
//                            Icon(Icons.Filled.ArrowForward, contentDescription = "Next")
//                        }
//                    } else {
//                        Button(
//                            onClick = {
//                                onboardingViewModel.markOnboardingCompleted()
//                                navController.navigate("home"){popUpTo("onboarding") { inclusive = true }}
//
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(end = 48.dp)
//                        ) {
//                            Text("Get Started")
//                        }
//                    }
//                }
//            }
//        }
//    )
//}

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.money.R
import com.example.money.data.datastore.OnboardingPrefs
import com.example.money.viewmodels.OnboardingViewModel
import com.example.money.viewmodels.CurrencyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to Budget App") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pager for Onboarding Screens
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

                        2 -> OnBoardingPage(
                            imageRes = R.drawable.piggy_bank,
                            title = "Analyze Your Data",
                            description = "Get insights into your spending habits."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = when (pagerState.currentPage) {
                        2 -> Arrangement.Center // Get Started button centered on last page
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
                    }else {
                        Spacer(modifier = Modifier.size(48.dp)) // Placeholder to balance layout
                    }

                    if (pagerState.currentPage < 2) {
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
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(end = 48.dp)
                        ) {
                            Text("Get Started")
                        }
                    }
                }
            }
        }
    )
}

