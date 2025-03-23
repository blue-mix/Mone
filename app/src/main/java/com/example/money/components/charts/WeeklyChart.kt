package com.example.money.components.charts
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.money.models.Expense
//import com.example.money.models.Recurrence
//import com.example.money.models.groupedByDayOfWeek
//import com.example.money.ui.theme.LabelSecondary
//import com.example.money.utils.simplifyNumber
//import com.github.tehras.charts.bar.BarChart
//import com.github.tehras.charts.bar.BarChartData
//import com.github.tehras.charts.bar.BarChartData.Bar
//import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
//import java.time.DayOfWeek
//
//
//@Composable
//fun WeeklyChart(expenses: List<Expense>) {
//    val groupedExpenses = expenses.groupedByDayOfWeek()
//
//    BarChart(
//        barChartData = BarChartData(
//            bars = listOf(
//                Bar(
//                    label = DayOfWeek.MONDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.MONDAY.name]?.total?.toFloat()
//                        ?: 0f,
//                    color = Color.White,
//                ),
//                Bar(
//                    label = DayOfWeek.TUESDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.TUESDAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                Bar(
//                    label = DayOfWeek.WEDNESDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.WEDNESDAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                Bar(
//                    label = DayOfWeek.THURSDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.THURSDAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                Bar(
//                    label = DayOfWeek.FRIDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.FRIDAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                Bar(
//                    label = DayOfWeek.SATURDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.SATURDAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                Bar(
//                    label = DayOfWeek.SUNDAY.name.substring(0, 1),
//                    value = groupedExpenses[DayOfWeek.SUNDAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//            )
//        ),
//        labelDrawer = LabelDrawer(recurrence = Recurrence.Weekly),
//        yAxisDrawer = SimpleYAxisDrawer(
//            labelTextColor = LabelSecondary,
//            labelValueFormatter = ::simplifyNumber,
//            labelRatio = 7,
//            labelTextSize = 14.sp
//        ),
//        barDrawer = BarDrawer(recurrence = Recurrence.Weekly),
//        modifier = Modifier
//            .padding(bottom = 20.dp)
//            .fillMaxSize()
//    )
//}


import android.graphics.Typeface
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimrnhhh.moneytopia.components.charts.rememberMarker
import com.example.money.data.models.Expense
import com.example.money.R
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import java.time.DayOfWeek
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.example.money.data.models.groupedByDayOfWeek

@Composable
fun WeeklyChart(
    expenses: List<Expense>,
) {
    val groupedExpenses = expenses.groupedByDayOfWeek()
    val chartEntryModel = entryModelOf(
        groupedExpenses[DayOfWeek.MONDAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[DayOfWeek.TUESDAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[DayOfWeek.WEDNESDAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[DayOfWeek.THURSDAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[DayOfWeek.FRIDAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[DayOfWeek.SATURDAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[DayOfWeek.SUNDAY.name]?.total?.toFloat()?: 0f
    )
    val daysOfWeek = listOf(
        stringResource(R.string.Monday),
        stringResource(R.string.Tuesday),
        stringResource(R.string.Wednesday),
        stringResource(R.string.Thursday),
        stringResource(R.string.Friday),
        stringResource(R.string.Saturday),
        stringResource(R.string.Sunday),
    )
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> {
            x, _ -> daysOfWeek[x.toInt() % daysOfWeek.size]
    }
    ProvideChartStyle(
        chartStyle = m3ChartStyle(),
    ) {
        Chart(
            modifier = Modifier
                .offset(y = (-20).dp),
            chart = lineChart(
                lines = listOf(
                    LineSpec(
                        lineColor = MaterialTheme.colorScheme.primary.toArgb(),
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            brush = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                    MaterialTheme.colorScheme.primary.copy(com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                                )
                            )
                        )
                    )
                )
            ),
            model = chartEntryModel,
            startAxis = rememberStartAxis(
                label = textComponent{
                    color = MaterialTheme.colorScheme.onBackground.toArgb()
                },
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5)
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = bottomAxisValueFormatter,
                label = textComponent{
                    color = MaterialTheme.colorScheme.onBackground.toArgb()
                    typeface = Typeface.SANS_SERIF
                }
            ),
            isZoomEnabled = false,
            marker = rememberMarker()
        )
    }
}