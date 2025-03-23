package com.example.money.components.charts

import com.dimrnhhh.moneytopia.components.charts.rememberMarker

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
//import com.example.money.models.groupedByMonth
//import com.example.money.ui.theme.LabelSecondary
//import com.example.money.utils.simplifyNumber
//import com.github.tehras.charts.bar.BarChart
//import com.github.tehras.charts.bar.BarChartData
//import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
//import java.time.Month
//
//
//@Composable
//fun YearlyChart(expenses: List<Expense>) {
//    val groupedExpenses = expenses.groupedByMonth()
//
//    BarChart(
//        barChartData = BarChartData(
//            bars = listOf(
//                BarChartData.Bar(
//                    label = Month.JANUARY.name.substring(0, 1),
//                    value = groupedExpenses[Month.JANUARY.name]?.total?.toFloat()
//                        ?: 0f,
//                    color = Color.White,
//                ),
//                BarChartData.Bar(
//                    label = Month.FEBRUARY.name.substring(0, 1),
//                    value = groupedExpenses[Month.FEBRUARY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.MARCH.name.substring(0, 1),
//                    value = groupedExpenses[Month.MARCH.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.APRIL.name.substring(0, 1),
//                    value = groupedExpenses[Month.APRIL.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.MAY.name.substring(0, 1),
//                    value = groupedExpenses[Month.MAY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.JUNE.name.substring(0, 1),
//                    value = groupedExpenses[Month.JUNE.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.JULY.name.substring(0, 1),
//                    value = groupedExpenses[Month.JULY.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.AUGUST.name.substring(0, 1),
//                    value = groupedExpenses[Month.AUGUST.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.SEPTEMBER.name.substring(0, 1),
//                    value = groupedExpenses[Month.SEPTEMBER.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.OCTOBER.name.substring(0, 1),
//                    value = groupedExpenses[Month.OCTOBER.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.NOVEMBER.name.substring(0, 1),
//                    value = groupedExpenses[Month.NOVEMBER.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//                BarChartData.Bar(
//                    label = Month.DECEMBER.name.substring(0, 1),
//                    value = groupedExpenses[Month.DECEMBER.name]?.total?.toFloat() ?: 0f,
//                    color = Color.White
//                ),
//            )
//        ),
//        labelDrawer = LabelDrawer(recurrence = Recurrence.Yearly),
//        yAxisDrawer = SimpleYAxisDrawer(
//            labelTextColor = LabelSecondary,
//            labelValueFormatter = ::simplifyNumber,
//            labelRatio = 7,
//            labelTextSize = 14.sp
//        ),
//        barDrawer = BarDrawer(recurrence = Recurrence.Yearly),
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
import com.example.money.R
import com.example.money.data.models.Expense
import com.example.money.data.models.groupedByMonth
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.Month
@Composable
fun YearlyChart(
    expenses: List<Expense>
) {
    val groupedExpenses = expenses.groupedByMonth()
    val chartEntryModel = entryModelOf(
        groupedExpenses[Month.JANUARY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.FEBRUARY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.MARCH.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.APRIL.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.MAY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.JUNE.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.JULY.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.AUGUST.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.SEPTEMBER.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.OCTOBER.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.NOVEMBER.name]?.total?.toFloat()?: 0f,
        groupedExpenses[Month.DECEMBER.name]?.total?.toFloat()?: 0f,
    )
    val monthsOfYear = listOf(
        stringResource(R.string.January),
        stringResource(R.string.February),
        stringResource(R.string.March),
        stringResource(R.string.April),
        stringResource(R.string.May),
        stringResource(R.string.June),
        stringResource(R.string.July),
        stringResource(R.string.August),
        stringResource(R.string.September),
        stringResource(R.string.October),
        stringResource(R.string.November),
        stringResource(R.string.December)
    )
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> {
            x, _ -> monthsOfYear[x.toInt() % monthsOfYear.size]
    }
    ProvideChartStyle(
        chartStyle = m3ChartStyle()
    ) {
        Chart(
            modifier = Modifier
                .offset(y = (-20).dp),
            chart = lineChart(
                lines = listOf(
                    LineChart.LineSpec(
                        lineColor = MaterialTheme.colorScheme.primary.toArgb(),
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            brush = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                    MaterialTheme.colorScheme.primary.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
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