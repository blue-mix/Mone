package com.example.money.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.extension.sumOf

//@Composable
//fun RoundedPieChartWithOverlay(
//    data: List<Float>,
//    colors: List<Color>,
//    totalAmount: String,
//    modifier: Modifier = Modifier,
//    gapAngle: Float = 2f
//) {
//    val total = data.sum()
//    val sweepAngles = data.map { (it / total) * (360f - gapAngle * data.size) }
//
//    Box(modifier = modifier, contentAlignment = Alignment.Center) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            val sizePx = size.minDimension
//            val radius = sizePx / 2
//            val center = Offset(size.width / 2, size.height / 2)
//            val capRadius = 16f // controls how round the ends look
//
//            var startAngle = -90f
//
//            sweepAngles.forEachIndexed { index, sweepAngle ->
//                val angleRadStart = Math.toRadians(startAngle.toDouble())
//                val angleRadEnd = Math.toRadians((startAngle + sweepAngle).toDouble())
//
//                // Define pie slice path
//                val path = Path().apply {
//                    moveTo(center.x, center.y)
//
//                    arcTo(
//                        rect = Rect(center = center, radius = radius),
//                        startAngleDegrees = startAngle,
//                        sweepAngleDegrees = sweepAngle,
//                        forceMoveTo = false
//                    )
//
//                    close()
//                }
//
//                drawPath(path, color = colors.getOrElse(index) { Color.Gray })
//
//                // Rounded caps (circle at start and end)
////                val startCap = Offset(
////                    x = center.x + radius * cos(angleRadStart).toFloat(),
////                    y = center.y + radius * sin(angleRadStart).toFloat()
////                )
////                val endCap = Offset(
////                    x = center.x + radius * cos(angleRadEnd).toFloat(),
////                    y = center.y + radius * sin(angleRadEnd).toFloat()
////                )
//                val capOffset = capRadius / 2.5f // tweak this if needed
////
//                val startCap = Offset(
//                    x = center.x + (radius - capOffset) * cos(angleRadStart).toFloat(),
//                    y = center.y + (radius - capOffset) * sin(angleRadStart).toFloat()
//                )
//
//                val endCap = Offset(
//                    x = center.x + (radius - capOffset) * cos(angleRadEnd).toFloat(),
//                    y = center.y + (radius - capOffset) * sin(angleRadEnd).toFloat()
//                )
//
//
//
//                drawCircle(color = colors[index], radius = capRadius, center = startCap)
//                drawCircle(color = colors[index], radius = capRadius, center = endCap)
//
//                startAngle += sweepAngle + gapAngle
//            }
//
//            // Black overlay circle in center (fake donut)
//            drawCircle(
//                color = Color.Black,
//                radius = radius * 0.70f,
//                center = center
//            )
//        }
//
//        // Total Amount Text in Center
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(
//                text = "Total Amount",
//                color = Color.White.copy(alpha = 0.5f),
//                style = MaterialTheme.typography.labelMedium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = totalAmount,
//                color = Color.White,
//                style = MaterialTheme.typography.headlineMedium
//            )
//        }
//    }
//}
data class DonutChartEntry(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun SmoothDonutChart(
    entries: List<DonutChartEntry>,
    totalAmountLabel: String,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 90f,
    gapAngle: Float = 2f
) {
    val total = entries.sumOf { it.value }
    val sweepAngles = entries.map { (it.value / total) * (360f - gapAngle * entries.size) }

    val animatables = remember(entries) {
        List(entries.size) { Animatable(0f) }
    }

    LaunchedEffect(entries) {
        sweepAngles.forEachIndexed { i, target ->
            animatables[i].animateTo(
                target,
                animationSpec = tween(durationMillis = 800, delayMillis = i * 100, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)
            val innerRadius = radius - strokeWidth / 2

            var startAngle = -90f

            animatables.forEachIndexed { index, animatable ->
                val sweepAngle = animatable.value
                drawArc(
                    color = entries.getOrNull(index)?.color ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = Size(innerRadius * 2, innerRadius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle + gapAngle
            }

            // Center black circle
            drawCircle(
                color = Color.Black,
                radius = innerRadius - strokeWidth / 2,
                center = center
            )

        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total Amount",
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = totalAmountLabel,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

