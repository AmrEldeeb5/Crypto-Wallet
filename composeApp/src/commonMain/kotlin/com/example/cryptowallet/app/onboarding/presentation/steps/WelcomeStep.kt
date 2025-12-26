package com.example.cryptowallet.app.onboarding.presentation.steps

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.cryptowallet.app.onboarding.domain.OnboardingFeature
import com.example.cryptowallet.app.onboarding.domain.welcomeFeatures
import com.example.cryptowallet.theme.LocalCryptoAccessibility
import com.example.cryptowallet.theme.LocalCryptoColors
import com.example.cryptowallet.theme.LocalCryptoTypography
import kotlinx.coroutines.delay

@Composable
fun WelcomeStep(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    // Animated values - static when reduce motion is enabled
    val iconScale: Float
    val pingScale: Float
    val pingAlpha: Float
    
    if (reduceMotion) {
        iconScale = 1f
        pingScale = 1f
        pingAlpha = 0f
    } else {
        val infiniteTransition = rememberInfiniteTransition()
        
        // Pulse animation for icon (slow pulse like animate-pulse-slow)
        val animatedIconScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse
            )
        )
        iconScale = animatedIconScale
        
        // Ping animation for outer circle
        val animatedPingScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Restart
            )
        )
        pingScale = animatedPingScale
        
        val animatedPingAlpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Restart
            )
        )
        pingAlpha = animatedPingAlpha
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sparkles icon with rounded square background and ping effect
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Ping circle (border animation)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(pingScale)
                    .alpha(pingAlpha)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .then(
                        Modifier.background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.accentBlue500.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                    )
            )
            
            // Main icon with rounded square background
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(iconScale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(colors.accentBlue500, colors.accentPurple500)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Sparkles icon
                Text(
                    text = "✦",
                    fontSize = 48.sp,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Gradient title "Welcome to CryptoVault" (blue -> purple -> pink gradient)
        Text(
            text = "Welcome to CryptoVault",
            style = typography.displayLarge.copy(
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
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = "Your premium crypto tracking companion",
            style = typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Feature highlight cards
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
private fun WelcomeFeatureCard(
    feature: OnboardingFeature,
    index: Int = 0,
    animateIn: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
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
    val cardShape = RoundedCornerShape(16.dp)
    // React: bg-slate-800/50 border border-slate-700/50
    val slateBackground = Color(0xFF1E293B).copy(alpha = 0.5f)
    val slateBorder = Color(0xFF334155).copy(alpha = 0.5f)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(cardShape)
            .background(slateBackground)
            .border(
                width = 1.dp,
                color = slateBorder,
                shape = cardShape
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient background (rounded-xl)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(feature.gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feature.iconType.emoji,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = feature.title,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = feature.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}


@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun WelcomeStepPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
        ) {
            WelcomeStep()
        }
    }
}
