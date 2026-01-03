package com.example.valguard.app.coins.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PerformanceChart(
    modifier: Modifier = Modifier,
    nodes: List<Double>,
    profitColor: Color,
    lossColor: Color,
    symbol: String = "",
    changePercent: Double = 0.0
) {
    if (nodes.isEmpty()) return

    val max = nodes.maxOrNull() ?: return
    val min = nodes.minOrNull() ?: return
    
    // Determine if price went up or down
    val isPositive = nodes.last() > nodes.first()
    
    // Use sparkline color logic if symbol is provided
    val lineColor = if (symbol.isNotEmpty()) {
        com.example.valguard.app.components.getSparklineColor(
            symbol = symbol,
            changePercent = changePercent,
            isPositive = isPositive
        )
    } else {
        // Fallback to original logic if no symbol provided
        if (isPositive) profitColor else lossColor
    }

    val transparentGraphColor = remember(lineColor) {
        lineColor.copy(alpha = 0.5f)
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val path = Path()
        val fillPath = Path()
        
        val width = size.width
        val height = size.height

        nodes.forEachIndexed { index, value ->
            val x = index * (width / (nodes.size - 1))
            val y = height * (1 - ((value - min) / (max - min)).toFloat())

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        
        fillPath.lineTo(width, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height * 0.5f // Fade out halfway down
            )
        )

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}