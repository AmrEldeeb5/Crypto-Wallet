/**
 * FeatureCard.kt
 *
 * Card components for displaying app features during onboarding.
 * Provides both full-size and compact variants with staggered
 * entrance animations.
 *
 * Features:
 * - Gradient icon backgrounds
 * - Staggered fade-in animations
 * - Slate-themed card styling
 * - Respects reduce motion accessibility
 *
 * @see FeaturesStep for usage context
 * @see OnboardingFeature for the data model
 */
package com.example.valguard.app.onboarding.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.valguard.app.onboarding.domain.OnboardingFeature
import com.example.valguard.app.onboarding.domain.welcomeFeatures
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.CoinRoutineTheme
import com.example.valguard.theme.LocalCryptoAccessibility
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoTypography
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

// React color constants: slate-800 = #1E293B, slate-700 = #334155
private val SlateBackground = Color(0xFF1E293B)
private val SlateBorder = Color(0xFF334155)

/**
 * Full-size feature card with vertical layout.
 *
 * Displays feature icon, title, and description in a card format.
 * Supports staggered entrance animation based on index.
 *
 * @param feature The feature to display
 * @param index Position in list for staggered animation (default 0)
 * @param animateIn Whether to animate entrance (default true)
 * @param modifier Optional modifier for the component
 */
@Composable
fun FeatureCard(
    feature: OnboardingFeature,
    index: Int = 0,
    animateIn: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    // Skip animation if reduce motion is enabled
    val shouldAnimate = animateIn && !reduceMotion
    var isVisible by remember { mutableStateOf(!shouldAnimate) }
    
    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            delay(index * 100L) // Staggered delay
            isVisible = true
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = if (reduceMotion) 0 else 300)
    )
    
    val cardShape = RoundedCornerShape(dimensions.cardCornerRadius)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(cardShape)
            // React: bg-slate-800/30 - more transparent/darker
            .background(SlateBackground.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = SlateBorder.copy(alpha = 0.5f),
                shape = cardShape
            )
            .padding(dimensions.cardPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon with rounded square background (rounded-xl = 12dp)
            Box(
                modifier = Modifier
                    .size(dimensions.coinIconSize)
                    .clip(RoundedCornerShape(dimensions.cardCornerRadius * 0.75f))
                    .background(
                        Brush.linearGradient(feature.gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(feature.iconType.resource),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(dimensions.coinIconSize * 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(dimensions.itemSpacing))
            
            // Title
            Text(
                text = feature.title,
                style = typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(dimensions.smallSpacing / 2))
            
            // Description
            Text(
                text = feature.description,
                style = typography.bodySmall,
                color = colors.textTertiary.copy(alpha = 0.8f)
            )
        }
    }
}


