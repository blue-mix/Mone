package com.example.money.pages.dashboard

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.money.components.expenses.ExpenseRowNew
import com.example.money.components.expenses.ExpensesList
import com.example.money.data.models.Expense
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    percentageChange: String,
    percentageColor: Color,
    icon: ImageVector,
    iconBackground: Color = Color(0xFFE8F5E9),
    valueColor: Color = Color.Black,
    backgroundColor: Color = Color.White

) {
    Surface(
        shape = RoundedCornerShape(12.dp),
     color = backgroundColor,
        modifier = modifier
            .height(180.dp)
            .width(100.dp),

        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackground, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,

                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Middle: Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            // Bottom: Value and % change
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = valueColor
                )
                Text(
                    text = percentageChange,
                    style = MaterialTheme.typography.labelMedium,
                    color = percentageColor
                )
            }
        }
    }
}

@Composable
fun FinanceStatsCard(
    incomeData: List<Float>,
    expenseData: List<Float>,
    labels: List<String>,
    selectedPeriod: String = "This Year",
    onPeriodClick: () -> Unit = {}
) {
    val barData = remember {
        labels.indices.map { i ->
            Bars(
                label = labels[i],
                values = listOf(
                    Bars.Data(
                        label = "Income",
                        value = incomeData.getOrElse(i) { 0f }.toDouble(),
                        color = Brush.verticalGradient(
                            listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)) // Green Gradient
                        )
                    ),
                    Bars.Data(
                        label = "Expense",
                        value = expenseData.getOrElse(i) { 0f }.toDouble(),
                        color = Brush.verticalGradient(
                            listOf(Color(0xFFFF8A65), Color(0xFFD32F2F)) // Red/Orange Gradient
                        )
                    )
                )
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = Color(0xFFF8F9FA),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header with dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Finance Statistics", fontWeight = FontWeight.Bold, fontSize = 28.sp)



                Button(
                    onClick = onPeriodClick,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("More", fontSize = 12.sp)
                    Icon(Icons.Default.NorthEast, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar chart
            ColumnChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 8.dp),
                data = barData,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topLeft = 6.dp, topRight = 6.dp),
                    spacing = 4.dp,
                    thickness = 22.dp
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }
}
data class TransactionItem(
    val title: String,
    val subtitle: String,
    val amount: Float,
    val isCredit: Boolean,
    val icon: ImageVector
)




@Composable
fun RecentTransactionsSurface(
    transactions: List<Expense>
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(36.dp),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            HorizontalDivider(
                modifier = Modifier
                    .width(60.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF3C3B3B),
                modifier = Modifier.padding(start = 10.dp, bottom = 12.dp)
            )
ExpensesList(transactions)
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(min = 400.dp)
//            ) {
//                itemsIndexed(transactions) { index, item ->
//
//                    ExpenseRowNew(item, showIcon = true, showDate = true)
//                    if (index < transactions.size - 1) {
//                        HorizontalDivider(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            thickness = 0.5.dp,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
//                        )
//                    }
//                }
//            }
        }
    }
}

@Composable
fun TransactionRow(item: TransactionItem, modifier: Modifier = Modifier) {
    val iconScale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
    }

    val amountScale = remember { Animatable(0.9f) }
    LaunchedEffect(Unit) {
        amountScale.animateTo(1f, animationSpec = tween(600))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp * iconScale.value)
                .background(Color(0xFFADDBA2), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = Color(0xFF3C3B3B))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF3C3B3B))
            Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Text(
            text = if (item.isCredit)
                "+₹${"%,.2f".format(item.amount)}"
            else
                "-₹${"%,.2f".format(item.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.isCredit) Color(0xFF166909) else Color(0xFFEC1C24),
            modifier = Modifier.scale(amountScale.value)
        )
    }
}

