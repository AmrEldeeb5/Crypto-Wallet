/**
 * BalanceHeader.kt
 *
 * Displays the portfolio balance header with total value, cash balance,
 * and performance metrics. Used at the top of portfolio-related screens.
 *
 * Features:
 * - Large display of total portfolio value
 * - Performance percentage with color coding (green/red)
 * - Cash balance display
 * - Optional "Buy Coin" action button
 * - Full accessibility support with content descriptions
 *
 * @see PortfolioValueCard for an alternative portfolio display component
 */
package com.example.cryptovault.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoSpacing
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Header component displaying portfolio balance information.
 *
 * Shows the total portfolio value prominently, along with performance
 * metrics and cash balance. Optionally displays a buy button.
 *
 * @param totalValue Formatted string of total portfolio value (e.g., "$12,345.67")
 * @param cashBalance Formatted string of available cash (e.g., "$1,000.00")
 * @param performancePercent Formatted performance percentage (e.g., "+5.23%")
 * @param performanceLabel Label for the performance period (default: "24h")
 * @param isPositive Whether the performance is positive (affects color)
 * @param showBuyButton Whether to show the "Buy Coin" button
 * @param onBuyClick Callback when the buy button is clicked
 * @param modifier Optional modifier for the component
 */
@Composable
fun BalanceHeader(
    totalValue: String,
    cashBalance: String,
    performancePercent: String,
    performanceLabel: String = "24h",
    isPositive: Boolean,
    showBuyButton: Boolean,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current

    val performanceColor = when {
        isPositive -> colors.profit
        else -> colors.loss
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackgroundElevated)
            .padding(spacing.lg)
            .semantics {
                contentDescription = "Portfolio value: $totalValue. Cash balance: $cashBalance. Performance: $performancePercent $performanceLabel"
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Total value label
            Text(
                text = "Total Value",
                style = typography.bodyMedium,
                color = colors.textSecondary
            )
            
            Spacer(modifier = Modifier.height(spacing.xs))
            
            // Total value amount (large)
            Text(
                text = totalValue,
                style = typography.displayLarge,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            // Performance indicator with label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = performancePercent,
                    style = typography.titleMedium,
                    color = performanceColor
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = "($performanceLabel)",
                    style = typography.bodyMedium,
                    color = colors.textTertiary
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            // Cash balance
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cash Balance:",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = cashBalance,
                    style = typography.bodyLarge,
                    color = colors.textPrimary
                )
            }
            
            // Buy button (conditional)
            if (showBuyButton) {
                Spacer(modifier = Modifier.height(spacing.lg))
                
                Button(
                    onClick = onBuyClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.profit
                    ),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Buy Coin",
                        style = typography.labelLarge,
                        color = colors.cardBackground
                    )
                }
            }
        }
    }
}
