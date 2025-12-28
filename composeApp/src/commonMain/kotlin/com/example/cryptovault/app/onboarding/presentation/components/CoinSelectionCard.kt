/**
 * CoinSelectionCard.kt
 *
 * Selectable card component for cryptocurrency selection during onboarding.
 * Displays coin information with animated selection state and gradient theming.
 *
 * Features:
 * - Scale animation on selection
 * - Gradient background when selected
 * - Checkmark indicator for selected state
 * - Haptic feedback on interaction
 * - Accessibility support with content descriptions
 *
 * @see CoinSelectionStep for usage context
 * @see OnboardingCoin for the data model
 */
package com.example.cryptovault.app.onboarding.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptovault.app.onboarding.domain.OnboardingCoin
import com.example.cryptovault.theme.LocalCryptoAccessibility
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Selectable card for cryptocurrency selection.
 *
 * Displays a coin with its icon, symbol, and name. When selected,
 * shows a gradient background with checkmark. Includes scale animation
 * and haptic feedback for better user experience.
 *
 * @param coin The cryptocurrency to display
 * @param isSelected Whether this coin is currently selected
 * @param onToggle Callback when the card is tapped
 * @param modifier Optional modifier for the component
 */
@Composable
fun CoinSelectionCard(
    coin: OnboardingCoin,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val haptic = LocalHapticFeedback.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    // Scale animation - disabled when reduce motion is enabled
    val scale = if (reduceMotion) {
        if (isSelected) 1.05f else 1f
    } else {
        val animatedScale by animateFloatAsState(
            targetValue = if (isSelected) 1.05f else 1f,
            animationSpec = spring(dampingRatio = 0.6f)
        )
        animatedScale
    }
    
    val accessibilityDescription = "${coin.name}, ${if (isSelected) "selected" else "not selected"}"
    // React: slate-800 = #1E293B, slate-700 = #334155
    val slateBackground = Color(0xFF1E293B).copy(alpha = 0.2f)
    val slateBorder = Color(0xFF334155).copy(alpha = 0.5f)
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) {
                    // Full gradient background when selected
                    Modifier.background(
                        Brush.linearGradient(coin.gradientColors)
                    )
                } else {
                    // React: border-slate-700/50 bg-slate-800/30
                    Modifier
                        .background(slateBackground)
                        .border(2.dp, slateBorder, RoundedCornerShape(16.dp))
                }
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggle()
            }
            .padding(16.dp)
            .semantics {
                role = Role.Checkbox
                contentDescription = accessibilityDescription
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top row with icon and checkmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Coin icon with gradient/white background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(Color.White.copy(alpha = 0.2f))
                            } else {
                                Modifier.background(Brush.linearGradient(coin.gradientColors))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = coin.icon,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Checkmark when selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = coin.gradientColors.first(),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Coin symbol
            Text(
                text = coin.symbol,
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else colors.textPrimary,
                modifier = Modifier.align(Alignment.Start)
            )
            
            // Coin name
            Text(
                text = coin.name,
                style = typography.bodySmall,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else colors.textSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun CoinSelectionCardPreview() {
    val btc = com.example.cryptowallet.app.onboarding.domain.popularCoins[0]
    val eth = com.example.cryptowallet.app.onboarding.domain.popularCoins[1]
    
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Row(
            modifier = Modifier.background(Color(0xFF0F172A)).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CoinSelectionCard(
                coin = btc,
                isSelected = true,
                onToggle = {},
                modifier = Modifier.weight(1f)
            )
            CoinSelectionCard(
                coin = eth,
                isSelected = false,
                onToggle = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}
