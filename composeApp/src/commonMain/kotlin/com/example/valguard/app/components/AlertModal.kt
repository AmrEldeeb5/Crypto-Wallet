/**
 * AlertModal.kt
 *
 * Provides a modal dialog for managing cryptocurrency price alerts.
 * Users can view, create, toggle, and delete price alerts for various coins.
 *
 * Features:
 * - Display list of existing price alerts
 * - Toggle alerts on/off
 * - Delete individual alerts
 * - Create new alerts via callback
 * - Empty state when no alerts exist
 *
 * @see PriceAlert for the alert data model
 * @see AlertCondition for alert trigger conditions
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.valguard.theme.LocalCryptoColors
import kotlin.math.pow

/**
 * Defines the condition that triggers a price alert.
 *
 * @property label Human-readable label for the condition
 */
enum class AlertCondition(val label: String) {
    /** Alert triggers when price goes above target */
    ABOVE("Above"),
    /** Alert triggers when price goes below target */
    BELOW("Below")
}

/**
 * Data class representing a price alert configuration.
 *
 * @property id Unique identifier for the alert
 * @property coinId The cryptocurrency ID this alert is for
 * @property coinSymbol The symbol of the cryptocurrency (e.g., "BTC")
 * @property condition Whether to alert when price is above or below target
 * @property targetPrice The price threshold that triggers the alert
 * @property isActive Whether the alert is currently enabled
 */
data class PriceAlert(
    val id: String,
    val coinId: String,
    val coinSymbol: String,
    val condition: AlertCondition,
    val targetPrice: Double,
    val isActive: Boolean
)

/**
 * Modal dialog for managing price alerts.
 *
 * Displays a list of existing alerts with toggle and delete options,
 * plus a button to create new alerts. Shows an empty state message
 * when no alerts exist.
 *
 * @param alerts List of existing price alerts to display
 * @param onDismiss Callback when the modal is dismissed
 * @param onCreateAlert Callback when user wants to create a new alert
 * @param onToggleAlert Callback when user toggles an alert's active state
 * @param onDeleteAlert Callback when user deletes an alert
 * @param modifier Optional modifier for the dialog content
 */
@Composable
fun AlertModal(
    alerts: List<PriceAlert>,
    onDismiss: () -> Unit,
    onCreateAlert: () -> Unit,
    onToggleAlert: (String) -> Unit,
    onDeleteAlert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundPrimary.copy(alpha = 0.9f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.cardBackground)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .clickable(enabled = false) {} // Prevent click through
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price Alerts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Alert list
                if (alerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No alerts set",
                                fontSize = 16.sp,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create an alert to get notified when prices change",
                                fontSize = 14.sp,
                                color = colors.textTertiary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(alerts, key = { it.id }) { alert ->
                            AlertItem(
                                alert = alert,
                                onToggle = { onToggleAlert(alert.id) },
                                onDelete = { onDeleteAlert(alert.id) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Create alert button
                Button(
                    onClick = onCreateAlert,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accentBlue500
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create New Alert",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Individual alert item displayed in the alert list.
 *
 * Shows the coin symbol, alert condition, target price, active status,
 * and a toggle switch to enable/disable the alert.
 *
 * @param alert The price alert to display
 * @param onToggle Callback when the toggle switch is changed
 * @param onDelete Callback when the delete action is triggered
 */
@Composable
private fun AlertItem(
    alert: PriceAlert,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(12.dp)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.cardBackgroundElevated)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coin symbol badge
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colors.accentBlue500.copy(alpha = 0.3f),
                            colors.accentPurple500.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = alert.coinSymbol.take(2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Alert details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.coinSymbol,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = "${alert.condition.label} \$${formatDecimal(alert.targetPrice, 2)}",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
        
        // Active indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (alert.isActive) colors.profit else colors.textTertiary
                )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Toggle switch
        Switch(
            checked = alert.isActive,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.textPrimary,
                checkedTrackColor = colors.accentBlue500,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.cardBorder
            )
        )
    }
}

/**
 * Extension function to count active alerts in a list.
 *
 * @return The number of alerts with [PriceAlert.isActive] set to true
 */
fun List<PriceAlert>.activeCount(): Int = count { it.isActive }

/**
 * Formats a decimal number to a specified number of decimal places.
 *
 * @param value The number to format
 * @param decimals The number of decimal places
 * @return Formatted string representation of the number
 */
private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(value * factor) / factor
    val parts = rounded.toString().split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].take(decimals).padEnd(decimals, '0') else "0".repeat(decimals)
    return "$intPart.$decPart"
}
