package com.example.valguard.app.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.valguard.app.coins.presentation.CoinsListViewModel
import com.example.valguard.app.coins.presentation.UiCoinListItem
import com.example.valguard.app.compare.presentation.CompareScreen
import com.example.valguard.app.components.AlertModal
import com.example.valguard.app.components.BottomNavItem
import com.example.valguard.app.components.CryptoBottomNavigation
import com.example.valguard.app.components.ValguardHeader
import com.example.valguard.app.components.EmptyState
import com.example.valguard.app.components.ExpandableCoinCard
import com.example.valguard.app.components.MoreMenuDropdown
import com.example.valguard.app.components.PortfolioValueCard
import com.example.valguard.app.components.PriceAlert
import com.example.valguard.app.components.SearchBar
import com.example.valguard.app.components.Tab
import com.example.valguard.app.components.TabNavigation
import com.example.valguard.app.components.Timeframe
import com.example.valguard.app.components.UiCoinItem
import com.example.valguard.app.components.activeCount
import com.example.valguard.app.portfolio.presentation.PortfolioViewModel
import com.example.valguard.app.portfolio.presentation.UiPortfolioCoinItem
import com.example.valguard.app.watchlist.domain.WatchlistRepository
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    onCoinClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    // ViewModels
    val coinsViewModel = koinViewModel<CoinsListViewModel>()
    val portfolioViewModel = koinViewModel<PortfolioViewModel>()
    val watchlistRepository: WatchlistRepository = koinInject()
    
    // State
    val coinsState by coinsViewModel.state.collectAsStateWithLifecycle()
    val portfolioState by portfolioViewModel.state.collectAsStateWithLifecycle()
    val watchlistIds by watchlistRepository.getWatchlist().collectAsStateWithLifecycle(initialValue = emptyList())
    
    // Local UI state
    var activeTab by remember { mutableStateOf(Tab.MARKET) }
    var activeBottomNav by remember { mutableStateOf(BottomNavItem.MARKET) }
    var expandedCoinId by remember { mutableStateOf<String?>(null) }
    var selectedTimeframe by remember { mutableStateOf(Timeframe.H24) }
    var showAlertModal by remember { mutableStateOf(false) }
    var alerts by remember { mutableStateOf<List<PriceAlert>>(emptyList()) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    // Sync tab with bottom nav
    LaunchedEffect(activeBottomNav) {
        activeTab = when (activeBottomNav) {
            BottomNavItem.MARKET -> Tab.MARKET
            BottomNavItem.PORTFOLIO -> Tab.PORTFOLIO
            BottomNavItem.DCA -> activeTab
            BottomNavItem.COMPARE -> activeTab
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Only show common header/tabs for Market, Portfolio, and Watchlist
            if (activeBottomNav == BottomNavItem.MARKET || activeBottomNav == BottomNavItem.PORTFOLIO) {
                // Header
                ValguardHeader(
                    marketCount = coinsState.coins.size,
                    portfolioCount = portfolioState.coins.size,
                    alertCount = alerts.activeCount(),
                    onAlertClick = { showAlertModal = true },
                    onMoreClick = { showMoreMenu = true }
                )
                
                // Search bar
                val searchPlaceholder = when (activeTab) {
                    Tab.PORTFOLIO -> "Search your assets"
                    else -> "Search cryptocurrencies"
                }
                SearchBar(
                    query = coinsState.searchQuery,
                    onQueryChange = { coinsViewModel.onSearchQueryChange(it) },
                    placeholder = searchPlaceholder,
                    modifier = Modifier.padding(vertical = spacing.sm)
                )
                
                // Tab navigation
                TabNavigation(
                    activeTab = activeTab,
                    onTabSelected = { tab ->
                        activeTab = tab
                        activeBottomNav = when (tab) {
                            Tab.MARKET -> BottomNavItem.MARKET
                            Tab.PORTFOLIO -> BottomNavItem.PORTFOLIO
                            Tab.WATCHLIST -> activeBottomNav // Keep current nav if it's already Market/Portfolio
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(spacing.sm))
            }
            
            // Content based on active bottom nav
            Box(modifier = Modifier.weight(1f)) {
                when (activeBottomNav) {
                    BottomNavItem.DCA -> {
                        com.example.valguard.app.dca.presentation.DCAScreen(
                            onBack = { activeBottomNav = BottomNavItem.MARKET },
                            onNavigateToBuy = onBuyClick
                        )
                    }
                    BottomNavItem.COMPARE -> {
                        CompareScreen()
                    }
                    else -> {
                        // Market / Portfolio / Watchlist logic
                        when {
                            coinsState.isLoading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = colors.accentBlue500)
                                }
                            }
                            else -> {
                                when (activeTab) {
                                    Tab.MARKET -> MarketContent(
                                        coins = coinsState.filteredCoins,
                                        watchlistIds = watchlistIds,
                                        expandedCoinId = expandedCoinId,
                                        selectedTimeframe = selectedTimeframe,
                                        onCardClick = { coinId -> onCoinClick(coinId) },
                                        onTimeframeSelected = { selectedTimeframe = it },
                                        onSetAlertClick = { showAlertModal = true },
                                        onBuyClick = onBuyClick,
                                        onSellClick = onSellClick,
                                        onWatchlistToggle = { }
                                    )
                                    Tab.PORTFOLIO -> PortfolioContent(
                                        portfolioValue = portfolioState.portfolioValue,
                                        change24h = portfolioState.performancePercent,
                                        isPositive = portfolioState.isPerformancePositive,
                                        coins = portfolioState.coins,
                                        watchlistIds = watchlistIds,
                                        expandedCoinId = expandedCoinId,
                                        selectedTimeframe = selectedTimeframe,
                                        onCardClick = { coinId -> onCoinClick(coinId) },
                                        onTimeframeSelected = { selectedTimeframe = it },
                                        onSetAlertClick = { showAlertModal = true },
                                        onBuyClick = onBuyClick,
                                        onSellClick = onSellClick,
                                        onWatchlistToggle = { },
                                        onDiscoverClick = { activeTab = Tab.MARKET }
                                    )
                                    Tab.WATCHLIST -> WatchlistContent(
                                        coins = coinsState.filteredCoins.filter { it.id in watchlistIds },
                                        watchlistIds = watchlistIds,
                                        expandedCoinId = expandedCoinId,
                                        selectedTimeframe = selectedTimeframe,
                                        onCardClick = { coinId -> onCoinClick(coinId) },
                                        onTimeframeSelected = { selectedTimeframe = it },
                                        onSetAlertClick = { showAlertModal = true },
                                        onBuyClick = onBuyClick,
                                        onSellClick = onSellClick,
                                        onWatchlistToggle = { },
                                        onDiscoverClick = { activeTab = Tab.MARKET }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Bottom navigation with padding container
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(spacing.md)
        ) {
            CryptoBottomNavigation(
                activeItem = activeBottomNav,
                onItemSelected = { item ->
                    activeBottomNav = item
                }
            )
        }
        
        // Alert modal
        if (showAlertModal) {
            AlertModal(
                alerts = alerts,
                onDismiss = { showAlertModal = false },
                onCreateAlert = { /* TODO: Implement create alert */ },
                onToggleAlert = { alertId ->
                    alerts = alerts.map { 
                        if (it.id == alertId) it.copy(isActive = !it.isActive) else it 
                    }
                },
                onDeleteAlert = { alertId ->
                    alerts = alerts.filter { it.id != alertId }
                }
            )
        }
        
        // More menu dropdown
        if (showMoreMenu) {
            MoreMenuDropdown(
                onDismiss = { showMoreMenu = false },
                onReferralClick = {
                    showMoreMenu = false
                    // TODO: Handle referral navigation if still needed as a separate screen
                }
            )
        }
    }
}

@Composable
private fun MarketContent(
    coins: List<UiCoinListItem>,
    watchlistIds: List<String>,
    expandedCoinId: String?,
    selectedTimeframe: Timeframe,
    onCardClick: (String) -> Unit,
    onTimeframeSelected: (Timeframe) -> Unit,
    onSetAlertClick: () -> Unit,
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    onWatchlistToggle: (String) -> Unit
) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    if (coins.isEmpty()) {
        EmptyState(
            title = "No coins found",
            description = "Try adjusting your search query",
            actionLabel = null,
            onAction = null
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
            modifier = Modifier.fillMaxSize()
        ) {
            items(coins, key = { it.id }) { coin ->
                ExpandableCoinCard(
                    coin = coin.toUiCoinItem(watchlistIds),
                    isExpanded = coin.id == expandedCoinId,
                    selectedTimeframe = selectedTimeframe,
                    onCardClick = { onCardClick(coin.id) },
                    onTimeframeSelected = onTimeframeSelected,
                    onSetAlertClick = onSetAlertClick,
                    onBuyClick = { onBuyClick(coin.id) },
                    onSellClick = { onSellClick(coin.id) },
                    onWatchlistToggle = { onWatchlistToggle(coin.id) },
                    showHoldings = false
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow() {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Buy", "Watchlist", "Alert").forEach { label ->
            Box(
                modifier = Modifier
                    .background(colors.cardBackground.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .border(1.dp, colors.cardBorder.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = colors.textTertiary.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PortfolioContent(
    portfolioValue: String,
    change24h: String,
    isPositive: Boolean,
    coins: List<UiPortfolioCoinItem>,
    watchlistIds: List<String>,
    expandedCoinId: String?,
    selectedTimeframe: Timeframe,
    onCardClick: (String) -> Unit,
    onTimeframeSelected: (Timeframe) -> Unit,
    onSetAlertClick: () -> Unit,
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    onWatchlistToggle: (String) -> Unit,
    onDiscoverClick: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    // Parse portfolio value for the card
    val portfolioValueDouble = portfolioValue.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
    val changeDouble = change24h.replace("%", "").replace("+", "").toDoubleOrNull() ?: 0.0
    val actualChange = if (isPositive) changeDouble else -kotlin.math.abs(changeDouble)
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        contentPadding = PaddingValues(
            start = spacing.md,
            end = spacing.md, 
            top = spacing.sm,
            bottom = 100.dp // Clear bottom nav
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        // Portfolio value card
        item {
            PortfolioValueCard(
                totalValue = portfolioValueDouble,
                change24h = actualChange,
                isEmpty = coins.isEmpty()
            )
        }
        
        if (coins.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EmptyState(
                        title = "No holdings yet",
                        description = "Start building your portfolio by buying some coins",
                        actionLabel = "Build Your Portfolio",
                        onAction = onDiscoverClick
                    )
                    
                    Text(
                        text = "Most portfolios start with Bitcoin or Ethereum",
                        fontSize = 13.sp,
                        color = colors.textSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(spacing.lg))
                    
                    Text(
                        text = "Available Actions",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textTertiary,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
                    )
                    
                    QuickActionsRow()
                }
            }
        } else {
            items(coins, key = { it.id }) { coin ->
                ExpandableCoinCard(
                    coin = coin.toUiCoinItem(watchlistIds),
                    isExpanded = coin.id == expandedCoinId,
                    selectedTimeframe = selectedTimeframe,
                    onCardClick = { onCardClick(coin.id) },
                    onTimeframeSelected = onTimeframeSelected,
                    onSetAlertClick = onSetAlertClick,
                    onBuyClick = { onBuyClick(coin.id) },
                    onSellClick = { onSellClick(coin.id) },
                    onWatchlistToggle = { onWatchlistToggle(coin.id) },
                    showHoldings = true
                )
            }
        }
    }
}

@Composable
private fun WatchlistContent(
    coins: List<UiCoinListItem>,
    watchlistIds: List<String>,
    expandedCoinId: String?,
    selectedTimeframe: Timeframe,
    onCardClick: (String) -> Unit,
    onTimeframeSelected: (Timeframe) -> Unit,
    onSetAlertClick: () -> Unit,
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    onWatchlistToggle: (String) -> Unit,
    onDiscoverClick: () -> Unit
) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    if (coins.isEmpty()) {
        EmptyState(
            title = "Watchlist empty",
            description = "Add coins to your watchlist to track them here",
            actionLabel = "Discover Coins",
            onAction = onDiscoverClick
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
            modifier = Modifier.fillMaxSize()
        ) {
            items(coins, key = { it.id }) { coin ->
                ExpandableCoinCard(
                    coin = coin.toUiCoinItem(watchlistIds),
                    isExpanded = coin.id == expandedCoinId,
                    selectedTimeframe = selectedTimeframe,
                    onCardClick = { onCardClick(coin.id) },
                    onTimeframeSelected = onTimeframeSelected,
                    onSetAlertClick = onSetAlertClick,
                    onBuyClick = { onBuyClick(coin.id) },
                    onSellClick = { onSellClick(coin.id) },
                    onWatchlistToggle = { onWatchlistToggle(coin.id) },
                    showHoldings = false
                )
            }
        }
    }
}

// Extension functions to convert view models to UiCoinItem
private fun UiCoinListItem.toUiCoinItem(watchlistIds: List<String>): UiCoinItem {
    return UiCoinItem(
        id = id,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
        formattedPrice = formattedPrice,
        formattedChange = formattedChange,
        isPositive = isPositive,
        priceDirection = priceDirection,
        isInWatchlist = id in watchlistIds
    )
}

private fun UiPortfolioCoinItem.toUiCoinItem(watchlistIds: List<String>): UiCoinItem {
    return UiCoinItem(
        id = id,
        name = name,
        symbol = "",
        iconUrl = iconUrl,
        formattedPrice = amountInFiatText,
        formattedChange = performancePercentText,
        isPositive = isPositive,
        priceDirection = priceDirection,
        holdingsAmount = amountInUnitText,
        holdingsValue = amountInFiatText,
        isInWatchlist = id in watchlistIds
    )
}
