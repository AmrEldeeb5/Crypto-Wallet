package com.example.valguard.app.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.valguard.app.components.BalanceHeader
import com.example.valguard.app.components.CoinCard
import com.example.valguard.app.components.EmptyState
import com.example.valguard.app.components.UiCoinItem
import com.example.valguard.app.realtime.domain.PriceDirection
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.LocalCryptoTypography
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PortfolioScreen(
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    val portfolioViewModel = koinViewModel<PortfolioViewModel>()
    val state by portfolioViewModel.state.collectAsStateWithLifecycle()
    val colors = LocalCryptoColors.current
    
    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                color = colors.profit,
                modifier = Modifier.size(32.dp)
            )
        }
    } else {
        PortfolioContent(
            state = state,
            onCoinItemClicked = onCoinItemClicked,
            onDiscoverCoinsClicked = onDiscoverCoinsClicked
        )
    }
}

@Composable
fun PortfolioContent(
    state: PortfolioState,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    val colors = LocalCryptoColors.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackgroundElevated)
    ) {
        BalanceHeader(
            totalValue = state.portfolioValue,
            cashBalance = state.cashBalance,
            performancePercent = state.performancePercent,
            performanceLabel = "24h",
            isPositive = state.isPerformancePositive,
            showBuyButton = state.showBuyButton,
            onBuyClick = onDiscoverCoinsClicked,
            modifier = Modifier.fillMaxHeight(0.35f)
        )
        PortfolioCoinsList(
            coins = state.coins,
            onCoinItemClicked = onCoinItemClicked,
            onDiscoverCoinsClicked = onDiscoverCoinsClicked
        )
    }
}

@Composable
private fun PortfolioCoinsList(
    coins: List<UiPortfolioCoinItem>,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(colors.cardBackground)
    ) {
        if (coins.isEmpty()) {
            EmptyState(
                title = "No coins yet",
                description = "Start building your portfolio by discovering and buying coins.",
                actionLabel = "Discover Coins",
                onAction = onDiscoverCoinsClicked
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Your Holdings",
                    style = typography.titleLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(dimensions.screenPadding)
                )
                
                Spacer(modifier = Modifier.height(dimensions.smallSpacing))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
                    modifier = Modifier.padding(horizontal = dimensions.screenPadding)
                ) {
                    items(coins, key = { it.id }) { coin ->
                        CoinCard(
                            coin = coin.toUiCoinItem(),
                            onClick = { onCoinItemClicked(coin.id) },
                            showHoldings = true
                        )
                    }
                }
            }
        }
    }
}

private fun UiPortfolioCoinItem.toUiCoinItem(): UiCoinItem {
    return UiCoinItem(
        id = id,
        name = name,
        symbol = "", // Symbol not available in UiPortfolioCoinItem
        iconUrl = iconUrl,
        formattedPrice = amountInFiatText,
        formattedChange = performancePercentText,
        isPositive = isPositive,
        priceDirection = priceDirection,
        holdingsAmount = amountInUnitText,
        holdingsValue = amountInFiatText
    )
}