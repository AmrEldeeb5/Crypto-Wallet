/**
 * TradeConfirmationDialog.kt
 *
 * Confirmation dialog shown before executing a buy or sell trade.
 * Displays trade details and requires user confirmation.
 *
 * Features:
 * - Trade summary (coin, amount, price, total)
 * - Color-coded confirm button (green for buy, red for sell)
 * - Cancel option
 * - Accessibility support
 *
 * @see TradeConfirmation for the trade data model
 */
package com.example.valguard.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.LocalCryptoTypography

/**
 * Data class containing trade confirmation details.
 *
 * @property coinName Full name of the cryptocurrency
 * @property coinSymbol Symbol of the cryptocurrency (e.g., "BTC")
 * @property amount Amount of coin being traded
 * @property price Price per coin
 * @property totalValue Total value of the trade
 * @property isBuy True for buy orders, false for sell orders
 */
data class TradeConfirmation(
    val coinName: String,
    val coinSymbol: String,
    val amount: String,
    val price: String,
    val totalValue: String,
    val isBuy: Boolean
)

/**
 * Dialog for confirming a trade before execution.
 *
 * Shows a summary of the trade with confirm and cancel buttons.
 * The confirm button is colored based on trade type (buy/sell).
 *
 * @param confirmation Trade details to display
 * @param onConfirm Callback when user confirms the trade
 * @param onCancel Callback when user cancels
 */
@Composable
fun TradeConfirmationDialog(
    confirmation: TradeConfirmation,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    
    val actionText = if (confirmation.isBuy) "Buy" else "Sell"
    val actionColor = if (confirmation.isBuy) colors.profit else colors.loss

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Confirm $actionText",
                style = typography.titleLarge,
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Review your $actionText order: ${confirmation.amount} of ${confirmation.coinName} at ${confirmation.price} for a total of ${confirmation.totalValue}"
                    }
            ) {
                TradeSummaryRow(
                    label = "Coin",
                    value = "${confirmation.coinName} (${confirmation.coinSymbol})"
                )
                Spacer(modifier = Modifier.height(spacing.xs))
                TradeSummaryRow(
                    label = "Amount",
                    value = confirmation.amount
                )
                Spacer(modifier = Modifier.height(spacing.xs))
                TradeSummaryRow(
                    label = "Price",
                    value = confirmation.price
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                TradeSummaryRow(
                    label = "Total",
                    value = confirmation.totalValue,
                    isHighlighted = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = actionColor
                )
            ) {
                Text(
                    text = "Confirm $actionText",
                    style = typography.labelLarge,
                    color = colors.cardBackground
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text(
                    text = "Cancel",
                    style = typography.labelLarge,
                    color = colors.textSecondary
                )
            }
        }
    )
}

/**
 * Row displaying a label-value pair in the trade summary.
 *
 * @param label Description of the value (e.g., "Amount", "Price")
 * @param value The value to display
 * @param isHighlighted Whether to use emphasized styling (for totals)
 */
@Composable
private fun TradeSummaryRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlighted) typography.titleMedium else typography.bodyMedium,
            color = colors.textSecondary
        )
        Text(
            text = value,
            style = if (isHighlighted) typography.titleMedium else typography.bodyMedium,
            color = if (isHighlighted) colors.textPrimary else colors.textPrimary
        )
    }
}
