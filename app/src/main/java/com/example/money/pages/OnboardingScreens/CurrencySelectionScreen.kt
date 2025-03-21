package com.example.money.pages.OnboardingScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.money.R
import com.example.money.utils.CurrencyPicker
import com.example.money.utils.CurrencyPickerData
import com.example.money.viewmodels.CurrencyViewModel

@Composable
fun CurrencySelectionPage(currencyViewModel: CurrencyViewModel = viewModel()) {
    val context = LocalContext.current
    val currencyDialog = remember { mutableStateOf(false) }
    val currencyNames = context.resources.getStringArray(R.array.currency_names)
    val currencyValues = context.resources.getStringArray(R.array.currency_values)

    // Retrieve stored currency using ViewModel (to avoid direct DataStore calls in UI)
    val savedCurrency by currencyViewModel.selectedCurrency.collectAsState(initial = "USD")

    // Safely get the selected currency name, fallback to USD if index out of range
    val selectedCurrencyName = remember(savedCurrency) {
        mutableStateOf(currencyNames.getOrElse(currencyValues.indexOf(savedCurrency)) { "USD" })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
            modifier = Modifier
                .size(350.dp)
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Choose Your Currency",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = { currencyDialog.value = true }) {
            Text(text = selectedCurrencyName.value)
        }

        // Currency Picker BottomSheet
        CurrencyPicker(
            defaultCurrencyValue = savedCurrency,
            currencyPickerData = CurrencyPickerData(currencyNames, currencyValues),
            showBottomSheet = currencyDialog
        ) { newValue ->
            // Update UI State & Save to DataStore via ViewModel
            selectedCurrencyName.value = currencyNames.getOrElse(currencyValues.indexOf(newValue)) { "USD" }
            currencyViewModel.saveSelectedCurrency(newValue)
        }
    }
}

