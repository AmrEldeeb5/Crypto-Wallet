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
package com.example.valguard.app.onboarding.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import com.example.valguard.app.onboarding.domain.OnboardingCoin
import com.example.valguard.app.onboarding.domain.popularCoins
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.CoinRoutineTheme
import com.example.valguard.theme.LocalCryptoAccessibility
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoTypography
import org.jetbrains.compose.resources.painterResource

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
    val dimensions = AppTheme.dimensions
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
            .clip(RoundedCornerShape(dimensions.cardCornerRadius))
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
                        .border(2.dp, slateBorder, RoundedCornerShape(dimensions.cardCornerRadius))
                }
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggle()
            }
            .padding(dimensions.cardPadding)
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
                // Coin icon with brand-appropriate background color
                Box(
                    modifier = Modifier
                        .size(dimensions.coinIconSize)
                        .clip(RoundedCornerShape(dimensions.cardCornerRadius * 0.75f))
                        .background(coin.iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(coin.iconRes),
                        contentDescription = "${coin.name} icon",
                        modifier = Modifier.size(dimensions.coinIconSize * 0.85f)
                    )
                }
                
                // Checkmark when selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(dimensions.coinIconSize * 0.5f)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = coin.gradientColors.first(),
                            modifier = Modifier.size(dimensions.coinIconSize * 0.33f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            
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


