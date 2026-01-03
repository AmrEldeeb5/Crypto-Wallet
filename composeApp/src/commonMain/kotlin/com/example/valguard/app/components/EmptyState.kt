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
 * - EmptyReason-based contextual messaging
 *
 * @see ErrorState for error message display
 * @see EmptyReason for contextual empty state reasons
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import com.example.valguard.app.core.util.EmptyReason
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.LocalCryptoColors
import androidx.compose.foundation.layout.fillMaxSize
import org.jetbrains.compose.resources.painterResource
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.solar__chart_square_outline
import valguard.composeapp.generated.resources.solar__alt_arrow_right_outline

/**
 * Returns contextual title and description based on EmptyReason.
 */
private fun getEmptyReasonContent(reason: EmptyReason): Pair<String, String> {
    return when (reason) {
        EmptyReason.NoResults -> "No Results" to "Try adjusting your search or filters."
        EmptyReason.NoHoldings -> "No Holdings Yet" to "Start building your portfolio by adding some coins."
        EmptyReason.NotSupported -> "Not Available" to "This feature is not supported for this item."
        EmptyReason.NoData -> "No Data" to "There's nothing to show here yet."
    }
}

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
 * @param reason Optional EmptyReason for contextual messaging (overrides title/description if provided)
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
    reason: EmptyReason? = null
) {
    // Use reason-based content if provided, otherwise use explicit title/description
    val (displayTitle, displayDescription) = if (reason != null) {
        getEmptyReasonContent(reason)
    } else {
        title to description
    }
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
                contentDescription = "$displayTitle. $displayDescription"
            }
    ) {
        // Empty state icon with pulse animation
        Box(
            modifier = Modifier
                .size(dimensions.appIconSize)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.solar__chart_square_outline),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = colors.textPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
        
        // Title
        Text(
            text = displayTitle,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(dimensions.smallSpacing))
        
        // Description
        Text(
            text = displayDescription,
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
                        painter = painterResource(Res.drawable.solar__alt_arrow_right_outline),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
