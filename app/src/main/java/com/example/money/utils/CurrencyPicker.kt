package com.example.money.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.money.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class CurrencyPickerData(
    val currencyNames: Array<String>,
    val currencyValues: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrencyPickerData

        if (!currencyNames.contentEquals(other.currencyNames)) return false
        if (!currencyValues.contentEquals(other.currencyValues)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currencyNames.contentHashCode()
        result = 31 * result + currencyValues.contentHashCode()
        return result
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPicker(
    defaultCurrencyValue: String,
    currencyPickerData: CurrencyPickerData,
    showBottomSheet: MutableState<Boolean>,
    onCurrencySelected: (String) -> Unit
) {
    val currencyNames = currencyPickerData.currencyNames
    val currencyValues = currencyPickerData.currencyValues

    val defaultCurrencyEntry = currencyNames[currencyValues.indexOf(defaultCurrencyValue)]
    val (selectedCurrencyOption, onCurrencyOptionSelected) = remember {
        mutableStateOf(defaultCurrencyEntry)
    }
    val (searchText, onSearchTextChanged) = remember { mutableStateOf("") }
    val filteredCurrencies = currencyNames.filter { it.contains(searchText, ignoreCase = true) }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    delay(300)
                    withContext(Dispatchers.Main) {
                        showBottomSheet.value = false
                        val choice = currencyValues[currencyNames.indexOf(selectedCurrencyOption)]
                        onCurrencySelected(choice)
                    }
                }
            },
            content = {
                Column {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = onSearchTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Search currency") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }, shape = RoundedCornerShape(12.dp)
                    )
                    Column(
                        modifier = Modifier
                            .selectableGroup()
                            .verticalScroll(rememberScrollState())
                    ) {
                        filteredCurrencies.forEach { text ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .selectable(
                                        selected = (text == selectedCurrencyOption),
                                        onClick = {
                                            onCurrencyOptionSelected(text)
                                        },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 18.dp)) {
                                    RadioButton(
                                        selected = (text == selectedCurrencyOption),
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary,
                                            unselectedColor = MaterialTheme.colorScheme.inversePrimary,
                                            disabledSelectedColor = Color.Black,
                                            disabledUnselectedColor = Color.Black
                                        )
                                    )
                                    Text(
                                        text = text,
                                        modifier = Modifier.padding(start = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            delay(300)
                            withContext(Dispatchers.Main) {
                                showBottomSheet.value = false
                                val choice =
                                    currencyValues[currencyNames.indexOf(selectedCurrencyOption)]
                                onCurrencySelected(choice)
                            }
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.confirm))
                }
            }
        )
    }
}
