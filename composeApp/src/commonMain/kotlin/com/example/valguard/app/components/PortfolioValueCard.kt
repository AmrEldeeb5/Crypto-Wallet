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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import kotlin.math.abs
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
    isEmpty: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(16.dp)
    val isPositive = change24h >= 0
    val changeColor = if (isPositive) colors.profit else colors.loss
    
    // Derive values based on isEmpty
    val displayValue = if (isEmpty) 0.0 else totalValue
    val formattedValue = formatCurrency(displayValue)
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
            
            PriceChangeIndicator(
                changePercent = change24h,
                formattedChange = "$formattedChange (24h)",
                isPositive = isPositive,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                iconSize = 20.dp,
                color = changeColor
            )
            
            if (isEmpty) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Start investing to build your portfolio",
                    fontSize = 13.sp,
                    color = colors.accentBlue400,
                    fontWeight = FontWeight.Medium
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
    val absValue = abs(value)
    val intPart = absValue.toLong()
    val decPart = ((absValue - intPart) * 100).toLong()

    // Add thousand separators
    val intStr = intPart.toString()
    val withCommas = buildString {
        intStr.forEachIndexed { index, c ->
            if (index > 0 && (intStr.length - index) % 3 == 0) append(',')
            append(c)
        }
    }

    val sign = if (value < 0) "-" else ""
    return "$sign\$$withCommas.${decPart.toString().padStart(2, '0')}"
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
