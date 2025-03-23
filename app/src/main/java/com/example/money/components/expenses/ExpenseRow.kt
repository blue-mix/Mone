package com.example.money.components.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.money.components.custom.CategoryBadge
import com.example.money.data.models.Expense
import com.example.money.ui.theme.Typography

@Composable
fun ExpenseRow(expense: Expense, modifier: Modifier = Modifier) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
                color = if (expense.amount < 0)  Color(0xFFD32F2F) else Color(0xFF2E7D32)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryBadge(category = expense.category!!)

        }
    }
}