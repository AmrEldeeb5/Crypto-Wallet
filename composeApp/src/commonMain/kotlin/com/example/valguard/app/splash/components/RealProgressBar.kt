package com.example.valguard.app.splash.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.valguard.app.splash.domain.InitPhase

/**
 * Real progress bar with phase-specific status text.
 * Shows actual initialization progress, not simulated.
 * 
 * De-emphasized design: progress = reassurance, not center stage.
 */
@Composable
fun RealProgressBar(
    progress: Float,
    phase: InitPhase,
    modifier: Modifier = Modifier
) {
    // Clean, minimal progress bar - no text clutter
    Box(
        modifier = modifier
            .fillMaxWidth(0.72f)
            .height(6.dp)
            .clip(RoundedCornerShape(50)) // Fully rounded (pill shape)
            .background(Color(0xFF1E293B).copy(alpha = 0.45f))
            .semantics {
                progressBarRangeInfo = androidx.compose.ui.semantics.ProgressBarRangeInfo(
                    current = progress,
                    range = 0f..1f
                )
            }
    ) {
        // Gradient fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50)) // Fully rounded (pill shape)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2563EB).copy(alpha = 0.9f),
                            Color(0xFF7C3AED).copy(alpha = 0.9f)
                        )
                    )
                )
        )
    }
}
