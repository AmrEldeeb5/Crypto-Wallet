package com.example.valguard.app.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.valguard.theme.LocalCryptoColors

/**
 * Represents a single data point in the chart with timestamp and price.
 */
data class ChartPoint(
    val timestamp: Long,
    val price: Double
)

data class BarChartData(
    val values: List<Double>,
    val labels: List<String> = emptyList()
)

@Composable
fun BarChart(
    data: BarChartData,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    isLoading: Boolean = false
) {
    val colors = LocalCryptoColors.current
    val animationProgress = remember { Animatable(0f) }
    
    // Fade labels during loading for visual hierarchy
    val labelAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0.35f else 0.6f,
        animationSpec = tween(300),
        label = "labelAlpha"
    )
    
    // Sample data to max 32 points
    val sampledValues = remember(data.values) {
        sampleData(data.values, 32)
    }
    
    LaunchedEffect(sampledValues, animate) {
        if (animate) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(1f, animationSpec = tween(800))
        } else {
            animationProgress.snapTo(1f)
        }
    }
    
    Column(modifier = modifier) {
        // Chart Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (sampledValues.isEmpty()) return@Canvas
            
            val minPrice = sampledValues.minOrNull() ?: 0.0
            val maxPrice = sampledValues.maxOrNull() ?: 1.0
            val avgPrice = sampledValues.average()
            val range = (maxPrice - minPrice).coerceAtLeast(0.000001)
            
            // Height constraints: Bars occupy 70% of height, centered vertically
            val chartHeight = size.height * 0.70f
            val verticalOffset = (size.height - chartHeight) / 2f
            
            // Draw baseline (Average)
            val baselineY = verticalOffset + chartHeight * (1f - ((avgPrice - minPrice) / range).toFloat().coerceIn(0f, 1f))
            drawLine(
                color = colors.textSecondary.copy(alpha = 0.1f),
                start = Offset(0f, baselineY),
                end = Offset(size.width, baselineY),
                strokeWidth = 1.dp.toPx()
            )
            
            val barCount = sampledValues.size
            val totalSpacing = size.width * 0.4f // 40% of width for spacing
            val totalBarWidth = size.width - totalSpacing
            val barWidth = totalBarWidth / barCount
            val spacing = totalSpacing / (barCount + 1)
            val cornerRadius = (barWidth / 2f).coerceAtMost(8.dp.toPx())
            
            sampledValues.forEachIndexed { index, value ->
                val normalizedHeight = ((value - minPrice) / range).toFloat().coerceIn(0f, 1f)
                val animatedHeight = normalizedHeight * animationProgress.value
                val barHeight = chartHeight * animatedHeight
                
                val x = spacing + (index * (barWidth + spacing))
                val y = verticalOffset + chartHeight - barHeight
                
                // Encode brightness/meaning: higher values = slightly brighter
                val brightnessBoost = (normalizedHeight * 0.15f)
                val opacity = 0.58f + (index.toFloat() / barCount * 0.05f) // Slightly fade older bars
                
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        colors.accentBlue500.copy(alpha = opacity + brightnessBoost),
                        colors.accentPurple500.copy(alpha = (opacity + brightnessBoost) * 0.8f)
                    )
                )
                
                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }
        }
        
        // X-Axis Labels (below chart)
        if (data.labels.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.labels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary.copy(alpha = labelAlpha)
                    )
                }
            }
        }
    }
}

/**
 * Samples data to a maximum number of points using bucket averaging.
 */
private fun sampleData(data: List<Double>, maxPoints: Int): List<Double> {
    if (data.size <= maxPoints) return data
    
    val result = mutableListOf<Double>()
    val bucketSize = data.size.toDouble() / maxPoints
    
    for (i in 0 until maxPoints) {
        val start = (i * bucketSize).toInt()
        val end = ((i + 1) * bucketSize).toInt().coerceAtMost(data.size)
        if (start < end) {
            val bucket = data.subList(start, end)
            result.add(bucket.average())
        }
    }
    
    // Ensure last point is exactly the last data point for visual continuity
    if (result.isNotEmpty() && data.isNotEmpty()) {
        result[result.size - 1] = data.last()
    }
    
    return result
}
