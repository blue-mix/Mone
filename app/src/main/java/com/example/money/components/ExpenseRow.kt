package com.example.money.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.money.models.Expense
import com.example.money.ui.theme.LabelSecondary
import com.example.money.ui.theme.Typography
import com.example.money.viewmodels.CurrencyViewModel
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

@Composable
fun ExpenseRow(expense: Expense, modifier: Modifier = Modifier) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
//            Text(
//                expense.note ?: expense.category!!.name,
//                style = Typography.headlineMedium
//            )
            Text(
                text = expense.note ?: expense.category?.name.orEmpty(),
                style = Typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f) // Take remaining horizontal space
                    .padding(end = 8.dp) // Add space before amount text
            )


            Text(
                text = "₹${String.format("%.2f", expense.amount)}",
//                text = formattedAmount,
                style = Typography.headlineMedium,
                color = if (expense.amount < 0) Color.Red else Color.Green
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryBadge(category = expense.category!!)
            Text(
                expense.date.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = Typography.bodyMedium,
                color = LabelSecondary
            )
        }
    }
}