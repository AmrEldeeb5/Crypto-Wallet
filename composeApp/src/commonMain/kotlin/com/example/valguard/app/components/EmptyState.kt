/**
 * EmptyState.kt
 *
 * Displays a friendly empty state message when no content is available.
 * Used throughout the app when lists are empty or no data matches filters.
 *
 * Features:
 * - Emoji icon for visual appeal
 * - Customizable title and description
 * - Optional action button for user guidance
 * - Accessibility support
 *
 * @see ErrorState for error message display
 */
package com.example.valguard.app.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.LocalCryptoColors

/**
 * Empty state component for when no content is available.
 *
 * Displays a centered message with optional action button to guide
 * users on what to do next.
 *
 * @param title Main heading text (e.g., "No coins found")
 * @param description Supporting text explaining the empty state
 * @param actionLabel Optional button label (null hides button)
 * @param onAction Optional callback for button click (null hides button)
 * @param modifier Optional modifier for the component
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val dimensions = AppTheme.dimensions
    
    // Scale pulse animation (6s interval)
    val infiniteTransition = rememberInfiniteTransition(label = "EmptyStatePulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensions.screenPadding)
            .semantics {
                contentDescription = "$title. $description"
            }
    ) {
        // Empty state icon with pulse animation
        Box(
            modifier = Modifier
                .size(dimensions.appIconSize)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📈",
                style = MaterialTheme.typography.displayLarge
            )
        }
        
        Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(dimensions.smallSpacing))
        
        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        
        // Action button (only if both label and action are provided)
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
            
            Button(
                onClick = onAction,
                modifier = Modifier.height(dimensions.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonPrimary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
