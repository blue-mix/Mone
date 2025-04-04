package com.example.money.pages.spending

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.money.R
import com.example.money.components.charts.DonutChartEntry
import com.example.money.data.models.CategorySpending
import com.example.money.viewmodels.TransactionFilterType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.absoluteValue


@Composable
fun NoArchivedGoals() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult =
            rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.no_results)
            )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value,
            isPlaying = true,
            iterations = 1,
            speed = 1f
        )

        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = compositionResult.value,
            progress = { progressAnimation },
            modifier = Modifier.size(320.dp),
            enableMergePaths = true
        )

        Text(
            text = stringResource(id = R.string.no_data),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .offset(y = (-16).dp)
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}
fun List<CategorySpending>.toDonutChartEntries(): List<DonutChartEntry> {
    return map {
        DonutChartEntry(
            label = it.name,
            value = it.amount.toFloat(),
            color = it.color
        )
    }
}

@Composable
fun CategoryRow(item: CategorySpending, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Animate icon bounce scale
        val iconScale = remember { Animatable(0.8f) }
        LaunchedEffect(Unit) {
            iconScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = {
                        OvershootInterpolator(2f).getInterpolation(it)
                    }
                )
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp * iconScale.value)
                .background(item.color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${item.transactions} Transactions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Animate amount text scale
        val amountScale = remember { Animatable(0.9f) }
        LaunchedEffect(Unit) {
            amountScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }


            Text(
                text = if (item.amount < 0)
                    "-₹${"%,.2f".format(item.amount.absoluteValue)}"
                else
                    "+₹${"%,.2f".format(item.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.amount < 0)
                    Color(0xFFD32F2F) // Red for Debit
                else
                    Color(0xFF2E7D32), // Green for Credit
                modifier = Modifier.scale(amountScale.value)
            )

    }
}


@Composable
fun FilterChipsRow(
    datetext: String,
    selectedType: TransactionFilterType,
    onDateClick: () -> Unit = {},
    onToggleType: (TransactionFilterType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp , vertical = 8.dp)
    ) {
        AssistChip(
            onClick = onDateClick,
            label = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(0.6f)
                ) {
                    Text(datetext)
                }
            },
            leadingIcon = { Icon(Icons.Default.CalendarToday, null) }
        )
        // Toggle Chip for Debit/Credit/All
        AssistChip(
            onClick = {
                val next = when (selectedType) {

                    TransactionFilterType.DEBIT -> TransactionFilterType.CREDIT
                    TransactionFilterType.CREDIT -> TransactionFilterType.DEBIT
                }
                onToggleType(next)
            },
            label = {
                val label = when (selectedType) {
                    TransactionFilterType.DEBIT -> "Debit"
                    TransactionFilterType.CREDIT -> "Credit"
                }
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(0.6f)
                ) {
                    Text(label)
                }
            },
            leadingIcon = { when(selectedType){
                TransactionFilterType.DEBIT -> Icon(Icons.Default.NorthEast, null)
                TransactionFilterType.CREDIT -> Icon(Icons.Default.SouthEast, null)
            } },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun DateRangeButtons(
    onRangeSelected: (from: LocalDate, to: LocalDate) -> Unit
) {
    val today = LocalDate.now()

    // Track the selected range
    var selectedRange by remember { mutableStateOf<String?>("This Month") }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        // Today Button
        AssistChip(
            onClick = {
                selectedRange = "Today"
                onRangeSelected(today, today)
            },
            label = { Text("Today") },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (selectedRange == "Today") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            )
        )

        // This Week Button
        AssistChip(
            onClick = {
                val start = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                val end = start.plusDays(6)
                selectedRange = "This Week"
                onRangeSelected(start, end)
            },
            label = { Text("This Week") },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (selectedRange == "This Week") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            )
        )

        // This Month Button
        AssistChip(
            onClick = {
                val start = today.withDayOfMonth(1)
                val end = today.withDayOfMonth(today.lengthOfMonth())
                selectedRange = "This Month"
                onRangeSelected(start, end)
            },
            label = { Text("This Month") },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (selectedRange == "This Month") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val pickerState = rememberDatePickerState()
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}