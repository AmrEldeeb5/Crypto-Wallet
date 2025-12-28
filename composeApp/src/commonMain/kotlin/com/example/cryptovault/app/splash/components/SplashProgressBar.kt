/**
 * SplashProgressBar.kt
 *
 * Custom progress indicator for the CryptoVault splash screen.
 * Displays loading progress with percentage text, a gradient-filled
 * progress bar, and status text.
 *
 * The progress bar uses a linear gradient (Blue → Purple → Pink)
 * that matches the app's branding and provides visual feedback
 * during the 3-second loading sequence.
 */
package com.example.cryptovault.app.splash.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.*

/**
 * Progress bar component for splash screen loading indication.
 *
 * Displays a horizontal progress bar with:
 * - "Loading..." text on the left
 * - Percentage text on the right (e.g., "47%")
 * - Gradient-filled progress bar (Blue600 → Purple600 → Pink500)
 * - Status text below the bar
 *
 * The progress bar fills from left to right based on the progress value,
 * providing clear visual feedback of loading completion.
 *
 * @param progress Float value from 0.0 to 1.0 representing completion percentage
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun SplashProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(300.dp)
    ) {
        // Progress text row (Loading... | 47%)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Slate300
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress bar container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Slate800.copy(alpha = 0.5f))
        ) {
            // Progress fill with gradient
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Blue600, Purple600, Pink500)
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Status text
        Text(
            text = "Initializing secure connection...",
            style = MaterialTheme.typography.labelSmall,
            color = Slate500,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
