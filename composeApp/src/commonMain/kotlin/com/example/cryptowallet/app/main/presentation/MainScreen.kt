package com.example.cryptowallet.app.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptowallet.app.coins.presentation.CoinsListViewModel
import com.example.cryptowallet.app.coins.presentation.UiCoinListItem
import com.example.cryptowallet.app.components.*
import com.example.cryptowallet.app.portfolio.presentation.PortfolioViewModel
import com.example.cryptowallet.app.portfolio.presentation.UiPortfolioCoinItem
import com.example.cryptowallet.app.realtime.domain.PriceDirection
import com.example.cryptowallet.app.watchlist.domain.WatchlistRepository
import com.example.cryptowallet.theme.LocalCryptoColors
import com.example.cryptowallet.theme.LocalCryptoSpacing
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main screen with CryptoVault design.
 * Integrates header, search, tabs, coin list, and bottom navigation.
 */
@Composable
fun MainScreen(
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    onCoinClick: (String) -> Unit = {},
    onDCAClick: () -> Unit = {},
    onCompareClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onReferralClick: () -> Unit = {},
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
            BottomNavItem.ALERTS -> activeTab // Keep current tab when alerts is clicked
            BottomNavItem.DCA -> activeTab
            BottomNavItem.COMPARE -> activeTab
            BottomNavItem.LEADERBOARD -> activeTab
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
                .padding(bottom = 72.dp) // Space for bottom nav
        ) {
            // Header
            CryptoVaultHeader(
                alertCount = alerts.activeCount(),
                onAlertClick = { showAlertModal = true },
                onMoreClick = { showMoreMenu = true }
            )
            
            // Search bar
            SearchBar(
                query = coinsState.searchQuery,
                onQueryChange = { coinsViewModel.onSearchQueryChange(it) },
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
                        Tab.WATCHLIST -> BottomNavItem.MARKET // Watchlist uses market nav
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            // Content based on active tab
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
                            onWatchlistToggle = { coinId ->
                                // Toggle watchlist in coroutine scope
                            }
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
        
        // Bottom navigation with new items
        CryptoBottomNavigation(
            activeItem = activeBottomNav,
            onItemSelected = { item ->
                when (item) {
                    BottomNavItem.ALERTS -> showAlertModal = true
                    BottomNavItem.DCA -> onDCAClick()
                    BottomNavItem.COMPARE -> onCompareClick()
                    BottomNavItem.LEADERBOARD -> onLeaderboardClick()
                    else -> activeBottomNav = item
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
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
                    onReferralClick()
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
    val colors = LocalCryptoColors.current
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
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        modifier = Modifier.fillMaxSize()
    ) {
        // Portfolio value card
        item {
            PortfolioValueCard(
                totalValue = portfolioValueDouble,
                change24h = actualChange
            )
        }
        
        if (coins.isEmpty()) {
            item {
                EmptyState(
                    title = "No holdings yet",
                    description = "Start building your portfolio by buying some coins",
                    actionLabel = "Discover Coins",
                    onAction = onDiscoverClick
                )
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
    val colors = LocalCryptoColors.current
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
