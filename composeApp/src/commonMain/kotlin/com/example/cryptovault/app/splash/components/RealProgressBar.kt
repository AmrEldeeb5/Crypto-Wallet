package com.example.cryptovault.app.splash.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cryptovault.app.splash.domain.InitPhase
import kotlin.math.roundToInt

/**
 * Real progress bar with phase-specific status text.
 * Shows actual initialization progress, not simulated.
 */
@Composable
fun RealProgressBar(
    progress: Float,
    phase: InitPhase,
    modifier: Modifier = Modifier
) {
    val percentage = (progress * 100).roundToInt()
    
    // Animated ellipsis for status text liveness
    val infiniteTransition = rememberInfiniteTransition(label = "ellipsis")
    val ellipsisState by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ellipsis"
    )
    
    val ellipsis = when (ellipsisState.toInt()) {
        0 -> ""
        1 -> "."
        2 -> ".."
        else -> "..."
    }
    
    val statusText = when (phase) {
        is InitPhase.SecureStorage -> "Setting up secure storage$ellipsis"
        is InitPhase.Database -> "Preparing database$ellipsis"
        is InitPhase.Network -> "Connecting to network$ellipsis"
        is InitPhase.Config -> "Loading configuration$ellipsis"
        is InitPhase.UIReady -> "Almost ready$ellipsis"
    }
    
    Column(
        modifier = modifier.fillMaxWidth(0.72f), // Wider bar (70-75% of screen)
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress percentage and label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8).copy(alpha = 0.7f) // Slate400 with reduced opacity
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFCBD5E1) // Slate300 - stronger than label
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1E293B)) // Slate800
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
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2563EB), // Blue600
                                Color(0xFF7C3AED)  // Purple600
                            )
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Status text with animated ellipsis
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B) // Slate500
        )
    }
}
