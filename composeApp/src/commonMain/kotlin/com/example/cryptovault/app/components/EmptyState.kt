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
package com.example.cryptovault.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoSpacing
import com.example.cryptovault.theme.LocalCryptoTypography

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
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.lg)
            .semantics {
                contentDescription = "$title. $description"
            }
    ) {
        // Empty state icon
        Text(
            text = "📭",
            style = typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Title
        Text(
            text = title,
            style = typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(spacing.xs))
        
        // Description
        Text(
            text = description,
            style = typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        
        // Action button (only if both label and action are provided)
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(spacing.lg))
            
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonPrimary
                )
            ) {
                Text(
                    text = actionLabel,
                    style = typography.labelLarge
                )
            }
        }
    }
}
