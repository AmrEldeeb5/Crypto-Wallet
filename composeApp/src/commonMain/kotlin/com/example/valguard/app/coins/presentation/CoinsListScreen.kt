package com.example.valguard.app.coins.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.valguard.app.coins.presentation.component.PerformanceChart
import com.example.valguard.app.components.CoinCard
import com.example.valguard.app.components.ConnectionStatusIndicator
import com.example.valguard.app.components.ErrorState
import com.example.valguard.app.components.SkeletonCoinList
import com.example.valguard.app.components.UiCoinItem
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.LocalCryptoTypography
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinsListViewModel = koinViewModel<CoinsListViewModel>()
    val state by coinsListViewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onDismissChart = { coinsListViewModel.onDismissChart() },
        onCoinLongPressed = { coinId -> coinsListViewModel.onCoinLongPressed(coinId) },
        onCoinClicked = onCoinClicked
    )
}

@Composable
fun CoinsListContent(
    state: CoinsState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    val colors = LocalCryptoColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground.copy(alpha = 0.4f))
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                LoadingContent()
            }
            state.error != null -> {
                ErrorState(
                    message = stringResource(state.error),
                    onRetry = null // Could add retry functionality
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Connection status indicator
                    ConnectionStatusIndicator(connectionState = state.connectionState)

                    CoinsList(
                        coins = state.coins,
                        onCoinLongPressed = onCoinLongPressed,
                        onCoinClicked = onCoinClicked
                    )
                }
            }
        }

        if (state.chartState != null) {
            CoinChartDialog(
                chartState = state.chartState,
                onDismiss = onDismissChart
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    val spacing = LocalCryptoSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.md)
    ) {
        // Use enhanced SkeletonCoinList with accessibility and performance cap
        SkeletonCoinList(
            itemCount = 6,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CoinsList(
    coins: List<UiCoinListItem>,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground.copy(alpha = 0.4f))
            .padding(horizontal = spacing.md),
    ) {
        item {
            // Header with space for future search/filter
            Text(
                text = "Discover Coins",
                style = typography.titleLarge,
                color = colors.textPrimary,
                modifier = Modifier.padding(vertical = spacing.md)
            )
        }
        items(coins, key = { it.id }) { coin ->
            CoinCard(
                coin = coin.toUiCoinItem(),
                onClick = { onCoinClicked(coin.id) },
                onLongClick = { onCoinLongPressed(coin.id) },
                showHoldings = false,
                borderColor = colors.accentPurple400
            )
        }
    }
}

private fun UiCoinListItem.toUiCoinItem(): UiCoinItem {
    return UiCoinItem(
        id = id,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
        formattedPrice = formattedPrice,
        formattedChange = formattedChange,
        changePercent = changePercent,
        isPositive = isPositive,
        priceDirection = priceDirection
    )
}

@Composable
fun CoinChartDialog(
    chartState: ChartState,
    onDismiss: () -> Unit,
) {
    val colors = LocalCryptoColors.current

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "24h Price chart for ${chartState.coinName}",
            )
        },
        text = {
            when (chartState) {
                is ChartState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                is ChartState.Success -> {
                    PerformanceChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        nodes = chartState.sparkLine,
                        profitColor = colors.profit,
                        lossColor = colors.loss,
                        symbol = chartState.coinSymbol,
                        changePercent = chartState.changePercent
                    )
                }
                is ChartState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = chartState.message,
                            color = colors.statusError
                        )
                    }
                }
                is ChartState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No chart data available",
                                color = colors.textSecondary
                            )
                            Text(
                                text = "This may be a stablecoin or data is temporarily unavailable",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = "Close",
                )
            }
        }
    )
}