/**
 * WelcomeStep.kt
 *
 * First step of the onboarding flow introducing users to Valguard.
 * Displays the app logo, welcome message, and key feature highlights.
 *
 * Features:
 * - Animated sparkle icon with pulse effect
 * - Gradient text for app name
 * - Staggered feature card animations
 * - Respects reduce motion accessibility
 *
 * @see OnboardingScreen for the parent container
 * @see welcomeFeatures for the displayed features
 */
package com.example.valguard.app.onboarding.presentation.steps

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.app.onboarding.domain.OnboardingFeature
import com.example.valguard.app.onboarding.domain.welcomeFeatures
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.LocalCryptoAccessibility
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoTypography
import com.example.valguard.theme.bebasNeueFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

/**
 * Welcome step content for onboarding.
 *
 * Displays the app introduction with animated header and
 * feature highlight cards.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun WelcomeStep(
    modifier: Modifier = Modifier
) {
    val dimensions = AppTheme.dimensions
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.screenPadding * 2),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.verticalSpacing * 2)
    ) {
        WelcomeHeader()
        
        // Feature highlight cards with more spacing
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing * 2)
        ) {
            welcomeFeatures.forEachIndexed { index, feature ->
                WelcomeFeatureCard(
                    feature = feature,
                    index = index,
                    animateIn = !reduceMotion
                )
            }
        }
    }
}

/**
 * Header section with animated icon and welcome text.
 *
 * Displays the sparkle icon with pulse animation, gradient app name,
 * and tagline. Animations are disabled when reduce motion is enabled.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun WelcomeHeader(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.verticalSpacing)
    ) {
        Text(
            text = "Welcome to Valguard",
            style = typography.displayMedium.copy(
                fontFamily = bebasNeueFontFamily(),
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.accentBlue400,
                        colors.accentPurple400,
                        colors.accentPink400
                    )
                )
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your premium crypto tracking companion",
            style = typography.bodyLarge,
            color = colors.textTertiary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Feature card for the welcome step.
 *
 * Compact horizontal card showing feature icon, title, and description.
 * Supports staggered entrance animation.
 *
 * @param feature The feature to display
 * @param index Position for staggered animation (default 0)
 * @param animateIn Whether to animate entrance (default true)
 * @param modifier Optional modifier for the component
 */
@Composable
fun WelcomeFeatureCard(
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


    // Staggered animation
    var isVisible by remember { mutableStateOf(!animateIn || reduceMotion) }

    LaunchedEffect(animateIn, reduceMotion) {
        if (animateIn && !reduceMotion) {
            delay(index * 150L)
            isVisible = true
        }
    }

    val alpha = if (isVisible) 1f else 0f

    // Plain layout - no container, no background, no border
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .padding(vertical = dimensions.itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.itemSpacing)
    ) {
        // Icon with softened gradient background
        Box(
            modifier = Modifier
                .size(dimensions.coinIconSize * 0.9f)
                .clip(RoundedCornerShape(dimensions.cardCornerRadius * 0.75f))
                .background(
                    Brush.linearGradient(
                        feature.gradientColors.map { it.copy(alpha = 0.7f) }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(feature.iconType.resource),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(dimensions.coinIconSize * 0.65f)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = feature.title,
                style = typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(dimensions.smallSpacing / 2))
            Text(
                text = feature.description,
                style = typography.bodyMedium,
                color = colors.textTertiary.copy(alpha = 0.8f)
            )
        }
    }
}

