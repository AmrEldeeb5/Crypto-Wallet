package com.example.cryptowallet.app.compare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cryptowallet.app.compare.domain.SavedComparison
import com.example.cryptowallet.app.components.ErrorState
import com.example.cryptowallet.app.components.ScreenHeader
import com.example.cryptowallet.app.components.SkeletonCard
import com.example.cryptowallet.app.core.util.UiState
import com.example.cryptowallet.theme.LocalCryptoColors
import com.example.cryptowallet.theme.LocalCryptoSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow

@Composable
fun CompareScreen(
    onBack: () -> Unit
) {
    val viewModel = koinViewModel<CompareViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Compare Coins",
                subtitle = "Side-by-side analysis",
                onBackClick = onBack
            )
        },
        containerColor = colors.backgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            item {
                CompareHeader()
            }
            
            item {
                CoinSelectorRow(
                    coin1 = state.coin1,
                    coin2 = state.coin2,
                    onSelectCoin1 = { viewModel.onEvent(CompareEvent.ShowCoinSelector(CoinSelectorTarget.COIN_1)) },
                    onSelectCoin2 = { viewModel.onEvent(CompareEvent.ShowCoinSelector(CoinSelectorTarget.COIN_2)) },
                    onSwap = { viewModel.onEvent(CompareEvent.SwapCoins) }
                )
            }
            
            if (state.coin1 != null && state.coin2 != null && state.comparisonData != null) {
                item {
                    ComparisonTable(
                        coin1 = state.coin1!!,
                        coin2 = state.coin2!!,
                        comparisonData = state.comparisonData!!
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(CompareEvent.ClearComparison) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear")
                        }
                        Button(
                            onClick = { viewModel.onEvent(CompareEvent.SaveComparison) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentBlue400)
                        ) {
                            Text("Save Comparison")
                        }
                    }
                }
            }
            
            item {
                SavedComparisonsSection(
                    savedComparisons = state.savedComparisons,
                    onLoadComparison = { viewModel.onEvent(CompareEvent.LoadSavedComparison(it)) },
                    onDeleteComparison = { viewModel.onEvent(CompareEvent.DeleteSavedComparison(it)) },
                    onRetry = { viewModel.onEvent(CompareEvent.Retry) }
                )
            }
        }
    }
    
    if (state.showCoinSelector != null) {
        CoinSelectorModal(
            searchQuery = state.coinSearchQuery,
            onSearchQueryChange = { viewModel.onEvent(CompareEvent.UpdateSearchQuery(it)) },
            onSelectCoin = { id, name, symbol, iconUrl ->
                viewModel.onEvent(CompareEvent.SelectCoin(id, name, symbol, iconUrl))
            },
            onDismiss = { viewModel.onEvent(CompareEvent.HideCoinSelector) }
        )
    }
}


@Composable
private fun CompareHeader() {
    val colors = LocalCryptoColors.current
    
    Column {
        Text(
            text = "Compare Coins",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = "Compare metrics between two cryptocurrencies",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun CoinSelectorRow(
    coin1: CoinSlot?,
    coin2: CoinSlot?,
    onSelectCoin1: () -> Unit,
    onSelectCoin2: () -> Unit,
    onSwap: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinSelectorCard(
            coin = coin1,
            borderColor = colors.accentBlue400,
            onClick = onSelectCoin1,
            modifier = Modifier.weight(1f)
        )
        
        IconButton(
            onClick = onSwap,
            enabled = coin1 != null && coin2 != null
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Swap coins",
                tint = if (coin1 != null && coin2 != null) colors.textPrimary else colors.textSecondary
            )
        }
        
        CoinSelectorCard(
            coin = coin2,
            borderColor = colors.profit,
            onClick = onSelectCoin2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CoinSelectorCard(
    coin: CoinSlot?,
    borderColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onClick)
            .padding(spacing.md),
        contentAlignment = Alignment.Center
    ) {
        if (coin != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                AsyncImage(
                    model = coin.iconUrl,
                    contentDescription = "${coin.name} icon",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Select coin",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Select Coin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ComparisonTable(
    coin1: CoinSlot,
    coin2: CoinSlot,
    comparisonData: ComparisonData
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .padding(spacing.md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ComparisonRow(
                label = "Price",
                value1 = formatPrice(coin1.price),
                value2 = formatPrice(coin2.price),
                winner = comparisonData.priceWinner,
                difference = "${formatPercentage(comparisonData.priceDifference)}%"
            )
            HorizontalDivider(color = colors.backgroundSecondary)
            
            ComparisonRow(
                label = "24h Change",
                value1 = "${formatPercentage(coin1.change24h)}%",
                value2 = "${formatPercentage(coin2.change24h)}%",
                winner = comparisonData.change24hWinner,
                difference = "${formatPercentage(comparisonData.change24hDifference)}%"
            )
            HorizontalDivider(color = colors.backgroundSecondary)
            
            ComparisonRow(
                label = "Market Cap",
                value1 = formatLargeNumber(coin1.marketCap),
                value2 = formatLargeNumber(coin2.marketCap),
                winner = comparisonData.marketCapWinner,
                difference = "${formatPercentage(comparisonData.marketCapDifference)}%"
            )
            HorizontalDivider(color = colors.backgroundSecondary)
            
            ComparisonRow(
                label = "24h Volume",
                value1 = formatLargeNumber(coin1.volume24h),
                value2 = formatLargeNumber(coin2.volume24h),
                winner = comparisonData.volumeWinner,
                difference = "${formatPercentage(comparisonData.volumeDifference)}%"
            )
            HorizontalDivider(color = colors.backgroundSecondary)
            
            ComparisonRow(
                label = "Rank",
                value1 = "#${coin1.marketCapRank}",
                value2 = "#${coin2.marketCapRank}",
                winner = comparisonData.rankWinner,
                difference = null
            )
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    value1: String,
    value2: String,
    winner: MetricWinner,
    difference: String?
) {
    val colors = LocalCryptoColors.current
    LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value1,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (winner == MetricWinner.COIN_1) FontWeight.Bold else FontWeight.Normal,
            color = if (winner == MetricWinner.COIN_1) colors.profit else colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
            if (difference != null) {
                Text(
                    text = difference,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Text(
            text = value2,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (winner == MetricWinner.COIN_2) FontWeight.Bold else FontWeight.Normal,
            color = if (winner == MetricWinner.COIN_2) colors.profit else colors.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun SavedComparisonsSection(
    savedComparisons: UiState<List<SavedComparison>>,
    onLoadComparison: (SavedComparison) -> Unit,
    onDeleteComparison: (Long) -> Unit,
    onRetry: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(
            text = "Saved Comparisons",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        
        when (savedComparisons) {
            is UiState.Loading -> {
                repeat(2) {
                    SkeletonCard()
                    Spacer(modifier = Modifier.height(spacing.xs))
                }
            }
            is UiState.Success -> {
                savedComparisons.data.forEach { comparison ->
                    SavedComparisonCard(
                        comparison = comparison,
                        onLoad = { onLoadComparison(comparison) },
                        onDelete = { onDeleteComparison(comparison.id) }
                    )
                }
            }
            is UiState.Error -> {
                ErrorState(
                    message = savedComparisons.message,
                    onRetry = onRetry
                )
            }
            is UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardBackground)
                        .padding(spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved comparisons yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedComparisonCard(
    comparison: SavedComparison,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onLoad)
            .padding(spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = comparison.coin1IconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "vs",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                AsyncImage(
                    model = comparison.coin2IconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "${comparison.coin1Symbol} vs ${comparison.coin2Symbol}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colors.loss
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoinSelectorModal(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectCoin: (id: String, name: String, symbol: String, iconUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    // Mock coin list (in real app, would come from repository)
    val coins = listOf(
        CoinOption("bitcoin", "Bitcoin", "BTC", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"),
        CoinOption("ethereum", "Ethereum", "ETH", "https://assets.coingecko.com/coins/images/279/large/ethereum.png"),
        CoinOption("solana", "Solana", "SOL", "https://assets.coingecko.com/coins/images/4128/large/solana.png"),
        CoinOption("cardano", "Cardano", "ADA", "https://assets.coingecko.com/coins/images/975/large/cardano.png"),
        CoinOption("dogecoin", "Dogecoin", "DOGE", "https://assets.coingecko.com/coins/images/5/large/dogecoin.png"),
        CoinOption("ripple", "XRP", "XRP", "https://assets.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png"),
        CoinOption("polkadot", "Polkadot", "DOT", "https://assets.coingecko.com/coins/images/12171/large/polkadot.png"),
        CoinOption("avalanche-2", "Avalanche", "AVAX", "https://assets.coingecko.com/coins/images/12559/large/Avalanche_Circle_RedWhite_Trans.png")
    )
    
    val filteredCoins = if (searchQuery.isEmpty()) {
        coins
    } else {
        coins.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.symbol.contains(searchQuery, ignoreCase = true)
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Coin",
                    style = MaterialTheme.typography.titleLarge,
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
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search coins...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                items(filteredCoins) { coin ->
                    CoinOptionItem(
                        coin = coin,
                        onClick = { onSelectCoin(coin.id, coin.name, coin.symbol, coin.iconUrl) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

private data class CoinOption(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String
)

@Composable
private fun CoinOptionItem(
    coin: CoinOption,
    onClick: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(spacing.sm)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.iconUrl,
                contentDescription = "${coin.name} icon",
                modifier = Modifier.size(40.dp)
            )
            Column {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price >= 1) {
        "\$${formatDecimal(price, 2)}"
    } else {
        "\$${formatDecimal(price, 6)}"
    }
}

private fun formatPercentage(value: Double): String {
    val sign = if (value > 0) "+" else ""
    return "$sign${formatDecimal(value, 2)}"
}

private fun formatLargeNumber(value: Double): String {
    return when {
        value >= 1_000_000_000_000 -> "\$${formatDecimal(value / 1_000_000_000_000, 2)}T"
        value >= 1_000_000_000 -> "\$${formatDecimal(value / 1_000_000_000, 2)}B"
        value >= 1_000_000 -> "\$${formatDecimal(value / 1_000_000, 2)}M"
        value >= 1_000 -> "\$${formatDecimal(value / 1_000, 2)}K"
        else -> "\$${formatDecimal(value, 2)}"
    }
}

private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(value * factor) / factor
    val parts = rounded.toString().split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].take(decimals).padEnd(decimals, '0') else "0".repeat(decimals)
    return "$intPart.$decPart"
}
