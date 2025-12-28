/**
 * OnboardingButton.kt
 *
 * Primary action button for the onboarding flow.
 * Displays "Continue" or "Get Started" based on current step,
 * with gradient background and disabled state support.
 *
 * @see OnboardingScreen for usage context
 */
package com.example.cryptovault.app.onboarding.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Primary action button for onboarding navigation.
 *
 * Displays step-appropriate text ("Continue" or "Get Started")
 * with animated gradient background when enabled, or disabled
 * styling when not actionable.
 *
 * @param currentStep The current step index (affects button text)
 * @param enabled Whether the button is interactive
 * @param gradient Gradient brush for enabled state background
 * @param onClick Callback when button is tapped
 * @param modifier Optional modifier for the component
 */
@Composable
fun OnboardingButton(
    currentStep: Int,
    enabled: Boolean,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val haptic = LocalHapticFeedback.current
    val buttonText = getButtonText(currentStep)
    
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) Color.Transparent else colors.buttonDisabled,
        animationSpec = tween(durationMillis = 200)
    )
    
    val textColor by animateColorAsState(
        targetValue = if (enabled) Color.White else colors.textTertiary,
        animationSpec = tween(durationMillis = 200)
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (enabled) {
                    Modifier.background(gradient)
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .clickable(enabled = enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .semantics {
                role = Role.Button
                contentDescription = if (enabled) buttonText else "$buttonText, disabled"
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buttonText,
                style = typography.labelLarge,
                color = textColor
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            )
        }
    }
}

/**
 * Returns the appropriate button text for the given step.
 *
 * @param currentStep The current step index
 * @return "Get Started" for final step, "Continue" otherwise
 */
fun getButtonText(currentStep: Int): String {
    return if (currentStep == 3) "Get Started" else "Continue"
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun OnboardingButtonPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.background(Color(0xFF0F172A)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingButton(
                currentStep = 0,
                enabled = true,
                gradient = Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFFA855F7))),
                onClick = {}
            )
            OnboardingButton(
                currentStep = 3,
                enabled = true,
                gradient = Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF14B8A6))),
                onClick = {}
            )
            OnboardingButton(
                currentStep = 0,
                enabled = false,
                gradient = Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFFA855F7))),
                onClick = {}
            )
        }
    }
}
