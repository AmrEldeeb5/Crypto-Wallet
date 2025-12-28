/**
 * SimplePriceChart.kt
 *
 * Canvas-based line chart for displaying price history.
 * Used in coin detail screens to show price trends over time.
 *
 * Features:
 * - Smooth line rendering with rounded caps
 * - Gradient fill under the line
 * - Optional animation on data load
 * - Automatic scaling to fit data
 * - Empty state handling
 *
 * @see ChartPreview for a compact sparkline variant
 */
package com.example.cryptovault.app.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.LocalCryptoColors

/**
 * Line chart component for displaying price history.
 *
 * Renders a smooth line chart with gradient fill, suitable for
 * showing price trends in detail views.
 *
 * @param prices List of price values to plot
 * @param modifier Optional modifier for the chart canvas
 * @param lineColor Custom line color (defaults to accent blue)
 * @param isPositive Whether the trend is positive (affects fill color)
 * @param animate Whether to animate the chart drawing
 */
@Composable
fun SimplePriceChart(
    prices: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color? = null,
    isPositive: Boolean = true,
    animate: Boolean = true
) {
    val colors = LocalCryptoColors.current
    val chartLineColor = lineColor ?: colors.accentBlue400
    val fillColor = if (isPositive) colors.profit else colors.loss
    
    // Animation progress for drawing the chart
    val animationProgress = remember { Animatable(if (animate) 0f else 1f) }
    
    LaunchedEffect(prices) {
        if (animate) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }
    
    if (prices.isEmpty() || prices.size < 2) {
        // Show empty state
        Canvas(modifier = modifier.fillMaxSize()) {
            // Draw a flat line in the middle
            val centerY = size.height / 2
            drawLine(
                color = chartLineColor.copy(alpha = 0.3f),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        return
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val padding = 8.dp.toPx()
        
        // Calculate min/max for normalization
        val minPrice = prices.minOrNull() ?: 0f
        val maxPrice = prices.maxOrNull() ?: 1f
        val priceRange = (maxPrice - minPrice).coerceAtLeast(0.001f)
        
        // Calculate points with padding
        val chartWidth = width - (padding * 2)
        val chartHeight = height - (padding * 2)
        val stepX = chartWidth / (prices.size - 1)
        
        // Normalize price to Y coordinate
        // Higher prices should be at the top (lower Y value)
        fun priceToY(price: Float): Float {
            val normalized = (price - minPrice) / priceRange
            return padding + chartHeight * (1 - normalized)
        }
        
        // Create the line path
        val linePath = Path()
        val fillPath = Path()
        
        // Calculate how many points to draw based on animation progress
        val pointsToDraw = (prices.size * animationProgress.value).toInt().coerceAtLeast(2)
        
        // Start the paths
        val startX = padding
        val startY = priceToY(prices[0])
        linePath.moveTo(startX, startY)
        fillPath.moveTo(startX, height) // Start at bottom
        fillPath.lineTo(startX, startY)
        
        // Draw points
        for (i in 1 until pointsToDraw.coerceAtMost(prices.size)) {
            val x = padding + (i * stepX)
            val y = priceToY(prices[i])
            linePath.lineTo(x, y)
            fillPath.lineTo(x, y)
        }
        
        // Complete the fill path
        val lastX = padding + ((pointsToDraw - 1).coerceAtMost(prices.size - 1) * stepX)
        fillPath.lineTo(lastX, height)
        fillPath.close()
        
        // Draw gradient fill under the line
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(
                fillColor.copy(alpha = 0.3f),
                fillColor.copy(alpha = 0.05f),
                Color.Transparent
            ),
            startY = 0f,
            endY = height
        )
        
        drawPath(
            path = fillPath,
            brush = gradientBrush
        )
        
        // Draw the line with rounded caps
        drawPath(
            path = linePath,
            color = chartLineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
