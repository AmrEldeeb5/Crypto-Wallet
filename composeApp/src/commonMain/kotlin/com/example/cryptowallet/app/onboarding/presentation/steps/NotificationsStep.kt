/**
 * NotificationsStep.kt
 *
 * Final step of the onboarding flow for configuring notification preferences.
 * Displays notification type cards and a toggle to enable/disable notifications.
 *
 * Features:
 * - Bouncing bell icon animation
 * - Notification type info cards with pulsing status dots
 * - Toggle button for enabling notifications
 * - Respects reduce motion accessibility
 *
 * @see OnboardingScreen for the parent container
 * @see notificationTypes for the displayed notification options
 */
package com.example.cryptowallet.app.onboarding.presentation.steps

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptowallet.theme.LocalCryptoAccessibility
import com.example.cryptowallet.theme.LocalCryptoColors
import com.example.cryptowallet.theme.LocalCryptoTypography

/**
 * Notifications step content for onboarding.
 *
 * Displays header, notification type cards with pulsing status dots,
 * and a toggle for enabling notifications.
 *
 * @param notificationsEnabled Current notification preference state
 * @param onToggleNotifications Callback when toggle is tapped
 * @param modifier Optional modifier for the component
 */
@Composable
fun NotificationsStep(
    notificationsEnabled: Boolean,
    onToggleNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    val infiniteTransition = rememberInfiniteTransition()
    
    // Pulsing animation for status dots
    val pulseAlpha = if (reduceMotion) {
        1f
    } else {
        val animatedPulse by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )
        animatedPulse
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NotificationsHeader()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Notification type cards with pulsing status dots
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Price Movement Alerts
            NotificationInfoCard(
                icon = "📈",
                iconGradient = listOf(colors.profit, Color(0xFF14B8A6)),
                title = "Price Movement Alerts",
                description = "Get notified when prices hit your targets",
                statusText = "Real-time notifications",
                statusDotColor = colors.profit,
                pulseAlpha = pulseAlpha
            )
            
            // Portfolio Updates
            NotificationInfoCard(
                icon = "💰",
                iconGradient = listOf(colors.accentBlue500, colors.accentPurple500),
                title = "Portfolio Updates",
                description = "Daily summaries of your portfolio performance",
                statusText = "Daily at 9:00 AM",
                statusDotColor = colors.accentBlue500,
                pulseAlpha = pulseAlpha
            )
            
            // Market Insights
            NotificationInfoCard(
                icon = "✨",
                iconGradient = listOf(colors.accentPink500, colors.loss),
                title = "Market Insights",
                description = "Important news and market movements",
                statusText = "As they happen",
                statusDotColor = colors.accentPink500,
                pulseAlpha = pulseAlpha
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        EnableNotificationsToggle(
            enabled = notificationsEnabled,
            onToggle = onToggleNotifications
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Disclaimer text
        Text(
            text = "You can customize notification preferences anytime in settings",
            style = LocalCryptoTypography.current.bodySmall,
            color = colors.textTertiary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Header section with bouncing bell icon and title text.
 *
 * The bell icon has a bounce animation that is disabled when
 * reduce motion accessibility is enabled.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun NotificationsHeader(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    val infiniteTransition = rememberInfiniteTransition()
    
    // Bouncing animation for bell icon
    val bellBounce = if (reduceMotion) {
        0f
    } else {
        val animatedBounce by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -10f,
            animationSpec = infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse
            )
        )
        animatedBounce
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bouncing bell icon section
        Box(
            modifier = Modifier
                .padding(top = (-bellBounce).dp)
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors.profit, Color(0xFF14B8A6))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔔", fontSize = 40.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Never Miss a Move",
            style = typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Get instant alerts on price changes",
            style = typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Toggle button for enabling/disabling notifications.
 *
 * Shows gradient background when enabled, with checkmark indicator.
 * Includes haptic feedback on interaction.
 *
 * @param enabled Current enabled state
 * @param onToggle Callback when toggle is tapped
 * @param modifier Optional modifier for the component
 */
@Composable
fun EnableNotificationsToggle(
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (enabled) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(colors.profit, Color(0xFF14B8A6))
                        )
                    )
                } else {
                    Modifier
                        .background(colors.cardBackground.copy(alpha = 0.3f))
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                }
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggle()
            }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (enabled) Color.White.copy(alpha = 0.2f) else colors.profit.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔔", fontSize = 24.sp)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Enable Notifications",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) Color.White else colors.textPrimary
                    )
                    Text(
                        text = "Stay informed in real-time",
                        style = typography.bodySmall,
                        color = if (enabled) Color.White.copy(alpha = 0.8f) else colors.textSecondary
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .then(
                        if (enabled) Modifier.background(Color.White) else Modifier.border(2.dp, colors.cardBorder, CircleShape)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (enabled) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.profit,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Info card displaying a notification type option.
 *
 * Shows icon, title, description, and pulsing status indicator.
 *
 * @param icon Emoji icon to display
 * @param iconGradient Gradient colors for icon background
 * @param title Notification type title
 * @param description Explanation of the notification
 * @param statusText Timing/frequency text
 * @param statusDotColor Color for the pulsing status dot
 * @param pulseAlpha Current alpha value for pulse animation
 * @param modifier Optional modifier for the component
 */
@Composable
private fun NotificationInfoCard(
    icon: String,
    iconGradient: List<Color>,
    title: String,
    description: String,
    statusText: String,
    statusDotColor: Color,
    pulseAlpha: Float,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    // React: slate-800 = #1E293B, slate-700 = #334155
    val slateBackground = Color(0xFF1E293B).copy(alpha = 0.3f)
    val slateBorder = Color(0xFF334155).copy(alpha = 0.5f)
    val cardShape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(slateBackground)
            .border(1.dp, slateBorder, cardShape)
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // Icon with gradient background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(iconGradient)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status row with pulsing dot
            Row(
                modifier = Modifier.padding(start = 64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsing status dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(pulseAlpha)
                        .clip(CircleShape)
                        .background(statusDotColor)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = statusText,
                    style = typography.bodySmall,
                    color = colors.textTertiary
                )
            }
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun NotificationsHeaderPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(modifier = Modifier.background(Color(0xFF0F172A)).padding(24.dp)) {
            NotificationsHeader()
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun EnableNotificationsTogglePreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Column(
            modifier = Modifier.background(Color(0xFF0F172A)).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnableNotificationsToggle(enabled = true, onToggle = {})
            EnableNotificationsToggle(enabled = false, onToggle = {})
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun NotificationsStepPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(
            modifier = Modifier.background(Color(0xFF0F172A))
        ) {
            NotificationsStep(
                notificationsEnabled = true,
                onToggleNotifications = {}
            )
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun NotificationInfoCardPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(
            modifier = Modifier.background(Color(0xFF0F172A)).padding(16.dp)
        ) {
            NotificationInfoCard(
                icon = "📈",
                iconGradient = listOf(Color(0xFF34D399), Color(0xFF14B8A6)),
                title = "Price Movement Alerts",
                description = "Get notified when prices hit your targets",
                statusText = "Real-time notifications",
                statusDotColor = Color(0xFF34D399),
                pulseAlpha = 1f
            )
        }
    }
}
