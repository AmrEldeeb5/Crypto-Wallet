/**
 * NotificationTypeCard.kt
 *
 * Card component for displaying notification type options during onboarding.
 * Shows notification category with icon, description, and pulsing status indicator.
 *
 * Features:
 * - Gradient icon background
 * - Pulsing status dot animation
 * - Slate-themed card styling
 *
 * @see NotificationsStep for usage context
 * @see NotificationType for the data model
 */
package com.example.cryptovault.app.onboarding.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptovault.app.onboarding.domain.NotificationType
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Card displaying a notification type option.
 *
 * Shows the notification category with icon, title, description,
 * and a pulsing status dot indicating active status.
 *
 * @param notificationType The notification type to display
 * @param modifier Optional modifier for the component
 */
@Composable
fun NotificationTypeCard(
    notificationType: NotificationType,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    
    // Pulsing animation for status dot
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // React: slate-800 = #1E293B, slate-700 = #334155
    val slateBackground = Color(0xFF1E293B).copy(alpha = 0.2f)
    val slateBorder = Color(0xFF334155).copy(alpha = 0.5f)
    val cardShape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(slateBackground)
            .border(1.dp, slateBorder, cardShape)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle with gradient
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(notificationType.gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notificationType.iconType.emoji,
                    style = typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notificationType.title,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notificationType.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            
            // Pulsing status dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(pulseAlpha)
                    .clip(CircleShape)
                    .background(colors.statusConnected)
            )
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun NotificationTypeCardPreview() {
    val notification = com.example.cryptowallet.app.onboarding.domain.notificationTypes[0]
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(
            modifier = Modifier.background(Color(0xFF0F172A)).padding(16.dp)
        ) {
            NotificationTypeCard(notificationType = notification)
        }
    }
}
