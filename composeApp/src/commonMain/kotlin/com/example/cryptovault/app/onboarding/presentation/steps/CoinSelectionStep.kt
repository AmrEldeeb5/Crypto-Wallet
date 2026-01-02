/**
 * CoinSelectionStep.kt
 *
 * Third step of the onboarding flow for selecting favorite cryptocurrencies.
 * Displays a 2x3 grid of popular coins that users can select to add
 * to their watchlist.
 *
 * Features:
 * - 2x3 grid of selectable coin cards
 * - Selection count badge
 * - Validation requiring at least one selection
 *
 * @see OnboardingScreen for the parent container
 * @see popularCoins for the available coins
 * @see CoinSelectionCard for individual coin cards
 */
package com.example.cryptovault.app.onboarding.presentation.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptovault.app.onboarding.domain.popularCoins
import com.example.cryptovault.app.onboarding.presentation.components.CoinSelectionCard
import com.example.cryptovault.theme.AppTheme
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Coin selection step content for onboarding.
 *
 * Displays header, 2x3 grid of coin cards, and selection badge.
 * Users must select at least one coin to proceed.
 *
 * @param selectedCoins Set of currently selected coin symbols
 * @param onToggleCoin Callback when a coin is tapped
 * @param modifier Optional modifier for the component
 */
@Composable
fun CoinSelectionStep(
    selectedCoins: Set<String>,
    onToggleCoin: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = AppTheme.dimensions
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensions.cardPadding * 2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoinSelectionHeader()
        
        Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
        
        // Dynamic grid based on screen size
        val columns = dimensions.gridColumns
        val rows = (popularCoins.size + columns - 1) / columns // Ceiling division
        
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing)
        ) {
            for (rowIndex in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.itemSpacing)
                ) {
                    for (colIndex in 0 until columns) {
                        val coinIndex = rowIndex * columns + colIndex
                        if (coinIndex < popularCoins.size) {
                            val coin = popularCoins[coinIndex]
                            CoinSelectionCard(
                                coin = coin,
                                isSelected = selectedCoins.contains(coin.symbol),
                                onToggle = { onToggleCoin(coin.symbol) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Empty spacer to maintain grid alignment
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
        
        //SelectionBadge(selectedCount = selectedCoins.size)
    }
}

/**
 * Header section with chart icon and instruction text.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun CoinSelectionHeader(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(56.dp))
        
        Text(
            text = "Choose Your Favorites",
            style = typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(dimensions.smallSpacing))
        
        Text(
            text = "Select coins to add to your watchlist",
            style = typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(dimensions.smallSpacing / 2))
        
        Text(
            text = "You can always change this later",
            style = typography.bodySmall,
            color = colors.textTertiary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Badge showing the number of selected coins.
 *
 * Only visible when at least one coin is selected.
 *
 * @param selectedCount Number of coins currently selected
 * @param modifier Optional modifier for the component
 */
@Composable
fun SelectionBadge(
    selectedCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    if (selectedCount > 0) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(dimensions.cardCornerRadius))
                .background(colors.profit.copy(alpha = 0.1f))
                .padding(
                    horizontal = dimensions.cardPadding + 4.dp,
                    vertical = dimensions.itemSpacing
                )
        ) {
            Text(
                text = "✓ $selectedCount ${if (selectedCount == 1) "coin" else "coins"} selected",
                style = typography.bodyMedium,
                color = colors.profit,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Returns human-readable text for the selection count.
 *
 * @param count Number of coins selected
 * @return Descriptive text (e.g., "1 coin selected", "3 coins selected")
 */
fun getSelectionCountText(count: Int): String {
    return when {
        count == 0 -> "Select at least one coin to continue"
        count == 1 -> "1 coin selected"
        else -> "$count coins selected"
    }
}

