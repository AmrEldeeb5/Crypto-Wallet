/**
 * PortfolioValueCard.kt
 *
 * Displays the total portfolio value with 24-hour change.
 * Used as a prominent header card in portfolio views.
 *
 * Features:
 * - Large portfolio value display
 * - 24-hour change percentage with color coding
 * - Gradient background for visual appeal
 * - Up/down arrow indicator
 *
 * @see BalanceHeader for an alternative portfolio display
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.LocalCryptoColors
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

/**
 * Card displaying total portfolio value and 24h change.
 *
 * Shows the portfolio value prominently with a gradient background
 * and color-coded change indicator.
 *
 * @param totalValue Total portfolio value in USD
 * @param change24h 24-hour percentage change (positive or negative)
 * @param modifier Optional modifier for the card
 */
@Composable
fun PortfolioValueCard(
    totalValue: Double,
    change24h: Double,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(16.dp)
    val isPositive = change24h >= 0
    val changeColor = if (isPositive) colors.profit else colors.loss
    
    // Format values
    val formattedValue = formatCurrency(totalValue)
    val formattedChange = formatPercentage(change24h)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colors.accentBlue500.copy(alpha = 0.2f),
                        colors.accentPurple500.copy(alpha = 0.2f),
                        colors.accentPink500.copy(alpha = 0.2f)
                    )
                )
            )
            .border(1.dp, colors.cardBorder, shape)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Portfolio Value",
                fontSize = 14.sp,
                color = colors.textSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = formattedValue,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isPositive) "Trending up" else "Trending down",
                    tint = changeColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formattedChange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = changeColor
                )
                Text(
                    text = " (24h)",
                    fontSize = 14.sp,
                    color = colors.textTertiary
                )
            }
        }
    }
}

/**
 * Formats a number as US currency.
 *
 * @param value The numeric value to format
 * @return Formatted currency string (e.g., "$1,234.56")
 */
private fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(value)
}

/**
 * Formats a number as a percentage with sign.
 *
 * @param value The percentage value
 * @return Formatted percentage string (e.g., "+5.23%")
 */
private fun formatPercentage(value: Double): String {
    val sign = if (value >= 0) "+" else ""
    return "$sign${formatDecimal(value, 2)}%"
}

/**
 * Formats a decimal number to specified precision.
 *
 * @param value The number to format
 * @param decimals Number of decimal places
 * @return Formatted decimal string
 */
private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(value * factor) / factor
    val parts = rounded.toString().split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].take(decimals).padEnd(decimals, '0') else "0".repeat(decimals)
    return "$intPart.$decPart"
}
