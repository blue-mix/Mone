package com.example.money.components.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
@Composable
fun ExpenseRowNew(
    expense: Expense,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
    showDate: Boolean = false
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showIcon) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(expense.category?.color ?: Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = expense.category?.name?.firstOrNull()?.toString() ?: "?",
                            color = Color.White,
                            style = Typography.labelMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.note ?: expense.category?.name.orEmpty(),
                        style = Typography.headlineMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (showDate) {
                        Text(
                            text = expense.date.toLocalDate().toString(),
                            style = Typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Text(
                text = (if (expense.amount < 0) "-₹" else "+₹") + String.format("%.2f", kotlin.math.abs(expense.amount)),
                style = Typography.headlineMedium,
                color = if (expense.amount < 0) Color(0xFFD32F2F) else Color(0xFF2E7D32)
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
