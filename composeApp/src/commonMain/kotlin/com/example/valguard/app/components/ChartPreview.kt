/**
 * ChartPreview.kt
 *
 * Provides a compact sparkline chart preview for displaying price trends.
 * Used in coin cards and list items to show at-a-glance price movement.
 *
 * Features:
 * - Deterministic data generation based on seed for consistency
 * - Color-coded line and fill based on trend direction
 * - Smooth line rendering with gradient fill
 * - Lightweight Canvas-based implementation
 *
 * @see SimplePriceChart for a more detailed chart component
 */
package com.example.valguard.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.valguard.theme.LocalCryptoColors
import kotlin.random.Random

/**
 * Compact sparkline chart showing price trend direction.
 *
 * Renders a small line chart with gradient fill, suitable for
 * embedding in cards and list items. Data is generated deterministically
 * based on the seed parameter for consistent display.
 *
 * @param isPositive Whether the trend is positive (green) or negative (red)
 * @param modifier Optional modifier for the chart canvas
 * @param seed Random seed for consistent data generation (same seed = same chart)
 */
@Composable
fun ChartPreview(
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    data: List<Double> = emptyList(),
    seed: Int = 0
) {
    val colors = LocalCryptoColors.current
    val lineColor = if (isPositive) colors.profit else colors.loss
    val fillColor = if (isPositive) {
        Brush.verticalGradient(
            colors = listOf(
                colors.profit.copy(alpha = 0.3f),
                colors.profit.copy(alpha = 0.0f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                colors.loss.copy(alpha = 0.3f),
                colors.loss.copy(alpha = 0.0f)
            )
        )
    }
    
    // Use actual data if available, otherwise generate consistent data points based on seed (fallback)
    val dataPoints = remember(data, seed, isPositive) {
        if (data.isNotEmpty()) {
            data.map { it.toFloat() }
        } else {
            generateChartData(seed, isPositive)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        if (dataPoints.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val minValue = dataPoints.minOrNull() ?: 0f
        val maxValue = dataPoints.maxOrNull() ?: 1f
        val range = (maxValue - minValue).coerceAtLeast(0.01f)
        
        val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)
        
        // Create path for line
        val linePath = Path()
        val fillPath = Path()
        
        dataPoints.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minValue) / range * height)
            
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        
        // Close fill path
        fillPath.lineTo(width, height)
        fillPath.close()
        
        // Draw fill
        drawPath(
            path = fillPath,
            brush = fillColor
        )
        
        // Draw line
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Generates chart data points based on a seed value.
 *
 * Creates a series of values that trend in the specified direction
 * while maintaining some randomness for visual interest.
 *
 * @param seed Random seed for reproducible results
 * @param isPositive Whether the overall trend should be upward
 * @return List of float values representing chart data points
 */
private fun generateChartData(seed: Int, isPositive: Boolean): List<Float> {
    val random = Random(seed)
    val points = mutableListOf<Float>()
    var value = 50f
    
    repeat(12) {
        // Add some randomness but bias toward the trend direction
        val change = random.nextFloat() * 10f - 5f
        val trendBias = if (isPositive) 0.5f else -0.5f
        value += change + trendBias
        value = value.coerceIn(10f, 90f)
        points.add(value)
    }
    
    return points
}
