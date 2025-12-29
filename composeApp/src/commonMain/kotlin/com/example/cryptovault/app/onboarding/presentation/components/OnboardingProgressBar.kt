/**
 * OnboardingProgressBar.kt
 *
 * Progress indicator component for the onboarding flow.
 * Displays step count, segmented progress bar, and percentage completion.
 *
 * Layout: "Step X of 4" | [progress segments] | "XX%"
 *
 * @see OnboardingScreen for usage context
 * @see OnboardingStep for step definitions
 */
package com.example.cryptovault.app.onboarding.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.AppTheme
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Segmented progress bar showing onboarding completion status.
 *
 * Displays the current step indicator, animated progress segments,
 * and percentage completion. Each segment fills with the step's
 * gradient color when active.
 *
 * @param currentStep The current step index (0-3)
 * @param totalSteps Total number of steps (default 4)
 * @param stepGradient Gradient brush for active segments
 * @param modifier Optional modifier for the component
 */
@Composable
fun OnboardingProgressBar(
    currentStep: Int,
    totalSteps: Int = 4,
    stepGradient: Brush,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    val progress = ((currentStep + 1).toFloat() / totalSteps * 100).toInt()
    
    val accessibilityDescription = "Step ${currentStep + 1} of $totalSteps, $progress% complete"
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.cardPadding * 2)
            .semantics { contentDescription = accessibilityDescription }
    ) {
        // Row with "Step X of 4" on left, progress segments in middle, percentage on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Progress segments in the middle
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensions.verticalSpacing),
                horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
            ) {
                repeat(totalSteps) { index ->
                    val isActive = index <= currentStep
                    val animatedWidth by animateFloatAsState(
                        targetValue = if (isActive) 1f else 0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.cardBackground)
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedWidth)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(stepGradient)
                            )
                        }
                    }
                }
            }
            
            // Percentage text on the right
            Text(
                text = "$progress%",
                style = typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

/**
 * Calculates the progress percentage for a given step.
 *
 * @param currentStep The current step index (0-3)
 * @param totalSteps Total number of steps (default 4)
 * @return Progress percentage (25, 50, 75, or 100)
 */
fun calculateProgressPercentage(currentStep: Int, totalSteps: Int = 4): Int {
    return ((currentStep + 1).toFloat() / totalSteps * 100).toInt()
}


