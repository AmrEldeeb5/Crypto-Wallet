package com.example.valguard.app.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.valguard.theme.LocalCryptoColors

data class BarChartData(
    val values: List<Double>,
    val labels: List<String> = emptyList()
)

@Composable
fun BarChart(
    data: BarChartData,
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    val colors = LocalCryptoColors.current
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data, animate) {
        if (animate) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(1f, animationSpec = tween(800))
        } else {
            animationProgress.snapTo(1f)
        }
    }
    
    val gradient = Brush.verticalGradient(
        colors = listOf(colors.accentBlue500, colors.accentPurple500)
    )
    
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        if (data.values.isEmpty()) return@Canvas
        
        val normalizedHeights = normalizeBarHeights(data.values)
        val barWidth = size.width / (data.values.size * 2f)
        val spacing = barWidth
        val maxBarHeight = size.height * 0.85f
        val cornerRadius = 8.dp.toPx()
        
        normalizedHeights.forEachIndexed { index, normalizedHeight ->
            val animatedHeight = normalizedHeight * animationProgress.value
            val barHeight = maxBarHeight * animatedHeight
            val x = spacing + (index * (barWidth + spacing))
            val y = size.height - barHeight
            
            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}

/**
 * Normalizes bar heights to [0, 1] range based on max value
 */
fun normalizeBarHeights(values: List<Double>): List<Float> {
    if (values.isEmpty()) return emptyList()
    val max = values.maxOrNull() ?: 1.0
    if (max == 0.0) return values.map { 0f }
    return values.map { (it / max).toFloat().coerceIn(0f, 1f) }
}
