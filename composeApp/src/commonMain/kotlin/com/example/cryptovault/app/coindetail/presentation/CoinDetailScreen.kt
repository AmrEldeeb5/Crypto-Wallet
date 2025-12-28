package com.example.cryptovault.app.coindetail.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cryptovault.app.coindetail.domain.ChartTimeframe
import com.example.cryptovault.app.coindetail.domain.CoinDetailData
import com.example.cryptovault.app.coindetail.domain.CoinHoldings
import com.example.cryptovault.app.components.AlertModal
import com.example.cryptovault.app.components.EmptyState
import com.example.cryptovault.app.components.ErrorState
import com.example.cryptovault.app.components.SimplePriceChart
import com.example.cryptovault.app.components.SkeletonBox
import com.example.cryptovault.app.components.SkeletonText
import com.example.cryptovault.app.core.util.UiState
import com.example.cryptovault.app.core.util.getPriceChangeColor
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs
import kotlin.math.pow


// Custom gradient colors
private val SlateGradientStart = Color(0xFF1E293B) // slate-800
private val SlateGradientEnd = Color(0xFF0F172A)   // slate-900
private val EmeraldStart = Color(0xFF34D399)       // emerald-400
private val EmeraldEnd = Color(0xFF10B981)         // emerald-500
private val RoseStart = Color(0xFFFB7185)          // rose-400
private val RoseEnd = Color(0xFFF43F5E)            // rose-500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    onDismiss: () -> Unit,
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit
) {
    val viewModel = koinViewModel<CoinDetailViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalCryptoColors.current
    LocalCryptoSpacing.current
    
    LaunchedEffect(coinId) {
        viewModel.onEvent(CoinDetailEvent.LoadCoin(coinId))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .statusBarsPadding()
    ) {
        // Top bar
        CoinDetailTopBar(
            isInWatchlist = state.isInWatchlist,
            onBackClick = onDismiss,
            onWatchlistClick = { viewModel.onEvent(CoinDetailEvent.ToggleWatchlist) }
        )
        
        when (val coinData = state.coinData) {
            is UiState.Loading -> {
                CoinDetailLoadingContent()
            }
            is UiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.onEvent(CoinDetailEvent.Refresh) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    CoinDetailContent(
                        coinData = coinData.data,
                        holdings = state.holdings,
                        chartData = state.currentChartData,
                        selectedTimeframe = state.selectedTimeframe,
                        isOffline = state.isOffline,
                        onTimeframeSelected = { viewModel.onEvent(CoinDetailEvent.SelectTimeframe(it)) },
                        onSetAlertClick = { viewModel.onEvent(CoinDetailEvent.ShowAlertModal) },
                        onBuyClick = { onBuyClick(coinId) },
                        onSellClick = { onSellClick(coinId) }
                    )
                }
            }
            is UiState.Error -> {
                ErrorState(
                    message = coinData.message,
                    onRetry = { viewModel.onEvent(CoinDetailEvent.Retry) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is UiState.Empty -> {
                EmptyState(
                    title = "Coin Not Found",
                    description = "The requested coin could not be found.",
                    actionLabel = null,
                    onAction = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    
    // Alert modal
    if (state.showAlertModal) {
        AlertModal(
            alerts = emptyList(),
            onDismiss = { viewModel.onEvent(CoinDetailEvent.HideAlertModal) },
            onCreateAlert = { /* TODO: Implement */ },
            onToggleAlert = { },
            onDeleteAlert = { }
        )
    }
}


@Composable
private fun CoinDetailTopBar(
    isInWatchlist: Boolean,
    onBackClick: () -> Unit,
    onWatchlistClick: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.textPrimary
            )
        }
        
        IconButton(onClick = onWatchlistClick) {
            Icon(
                imageVector = if (isInWatchlist) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                tint = if (isInWatchlist) colors.accentBlue400 else colors.textSecondary
            )
        }
    }
}

@Composable
private fun CoinDetailContent(
    coinData: CoinDetailData,
    holdings: CoinHoldings?,
    chartData: List<Float>,
    selectedTimeframe: ChartTimeframe,
    isOffline: Boolean,
    onTimeframeSelected: (ChartTimeframe) -> Unit,
    onSetAlertClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit
) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = spacing.md)
    ) {
        // Offline indicator
        if (isOffline) {
            OfflineBanner(modifier = Modifier.padding(bottom = spacing.sm))
        }
        
        // Coin header
        CoinDetailHeader(coinData = coinData)
        
        Spacer(modifier = Modifier.height(spacing.lg))
        
        // Price display card with gradient background
        PriceDisplayCard(
            coinData = coinData,
            chartData = chartData,
            selectedTimeframe = selectedTimeframe,
            onTimeframeSelected = onTimeframeSelected
        )
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Quick stats row
        QuickStatsRow(coinData = coinData)
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Detailed stats grid
        StatsGrid(coinData = coinData)
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Price alert button
        PriceAlertButton(onClick = onSetAlertClick)
        
        // Holdings section (only if user owns this coin)
        if (holdings != null && holdings.amountOwned > 0) {
            Spacer(modifier = Modifier.height(spacing.md))
            HoldingsCard(holdings = holdings, coinData = coinData)
        }
        
        Spacer(modifier = Modifier.height(spacing.lg))
        
        // Buy/Sell buttons with gradients
        ActionButtons(
            onBuyClick = onBuyClick,
            onSellClick = onSellClick,
            canSell = holdings != null && holdings.amountOwned > 0
        )
        
        Spacer(modifier = Modifier.height(spacing.xl))
    }
}


@Composable
private fun CoinDetailHeader(coinData: CoinDetailData) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coin icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardBackground),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = coinData.iconUrl,
                contentDescription = "${coinData.name} icon",
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(spacing.md))
        
        Column {
            Text(
                text = coinData.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = coinData.symbol.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun PriceDisplayCard(
    coinData: CoinDetailData,
    chartData: List<Float>,
    selectedTimeframe: ChartTimeframe,
    onTimeframeSelected: (ChartTimeframe) -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    val changeColor = getPriceChangeColor(coinData.change24h, colors)
    val isPositive = coinData.change24h >= 0
    
    // Pulsing animation for positive price change badge
    val infiniteTransition = rememberInfiniteTransition(label = "pricePulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPositive) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    // Animated price value
    var displayedPrice by remember { mutableStateOf(coinData.price) }
    val animatedPrice by animateFloatAsState(
        targetValue = coinData.price.toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "priceAnimation"
    )
    
    LaunchedEffect(coinData.price) {
        displayedPrice = coinData.price
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SlateGradientStart, SlateGradientEnd)
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Current Price",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            
            Spacer(modifier = Modifier.height(spacing.xs))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "\$${formatPrice(animatedPrice.toDouble())}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                
                // Animated change badge
                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(changeColor.copy(alpha = 0.15f))
                        .padding(horizontal = spacing.md, vertical = spacing.xs)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${formatDecimal(coinData.change24h, 2)}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = changeColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            // Timeframe selector
            TimeframeSelector(
                selectedTimeframe = selectedTimeframe,
                onTimeframeSelected = onTimeframeSelected
            )
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            // Price chart with animation
            AnimatedVisibility(
                visible = chartData.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                SimplePriceChart(
                    prices = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    isPositive = isPositive,
                    animate = true
                )
            }
            
            // Show skeleton when no chart data
            if (chartData.isEmpty()) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}


@Composable
private fun TimeframeSelector(
    selectedTimeframe: ChartTimeframe,
    onTimeframeSelected: (ChartTimeframe) -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        ChartTimeframe.entries.forEach { timeframe ->
            val isSelected = timeframe == selectedTimeframe
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) colors.accentBlue400.copy(alpha = 0.2f)
                        else colors.backgroundSecondary
                    )
                    .clickable { onTimeframeSelected(timeframe) }
                    .padding(vertical = spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeframe.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) colors.accentBlue400 else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(coinData: CoinDetailData) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        QuickStatCard(
            label = "Supply",
            value = formatSupply(coinData.circulatingSupply, coinData.symbol),
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            label = "ATH",
            value = "\$${formatPrice(coinData.allTimeHigh)}",
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            label = "Rank",
            value = "#${coinData.marketCapRank}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardBackground)
            .padding(spacing.sm)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun StatsGrid(coinData: CoinDetailData) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            EnhancedStatCard(
                label = "24h Volume",
                value = "\$${formatLargeNumber(coinData.volume24h)}",
                modifier = Modifier.weight(1f)
            )
            EnhancedStatCard(
                label = "Market Cap",
                value = "\$${formatLargeNumber(coinData.marketCap)}",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            EnhancedStatCard(
                label = "24h High",
                value = "\$${formatPrice(coinData.high24h)}",
                modifier = Modifier.weight(1f)
            )
            EnhancedStatCard(
                label = "24h Low",
                value = "\$${formatPrice(coinData.low24h)}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun EnhancedStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = colors.accentBlue400.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.accentBlue400.copy(alpha = 0.3f),
                        colors.accentPurple400.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(colors.cardBackground)
            .padding(spacing.md)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun PriceAlertButton(onClick: () -> Unit) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(
                colors = listOf(colors.accentBlue400, colors.accentPurple400)
            )
        )
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = colors.accentBlue400,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(spacing.sm))
        Text(
            text = "Set Price Alert",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun HoldingsCard(
    holdings: CoinHoldings,
    coinData: CoinDetailData
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    val isProfit = holdings.profitLoss >= 0
    val profitLossColor = if (isProfit) colors.profit else colors.loss
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isProfit) colors.profit.copy(alpha = 0.2f) else colors.loss.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        profitLossColor.copy(alpha = 0.3f),
                        profitLossColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(colors.cardBackground)
            .padding(spacing.lg)
    ) {
        Column {
            Text(
                text = "Your Holdings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${formatAmount(holdings.amountOwned)} ${coinData.symbol.uppercase()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Value",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\$${formatPrice(holdings.currentValue)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Avg. Purchase Price",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\$${formatPrice(holdings.averagePurchasePrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Profit/Loss",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${if (isProfit) "+" else ""}\$${formatPrice(abs(holdings.profitLoss))} (${formatDecimal(holdings.profitLossPercentage, 2)}%)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = profitLossColor
                    )
                }
            }
        }
    }
}


@Composable
private fun ActionButtons(
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    canSell: Boolean
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    val haptic = LocalHapticFeedback.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        // Buy button with emerald gradient and glow
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = EmeraldStart.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(EmeraldStart, EmeraldEnd)
                    )
                )
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBuyClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Buy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // Sell button with rose gradient and glow
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(
                    elevation = if (canSell) 12.dp else 0.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = if (canSell) RoseStart.copy(alpha = 0.4f) else Color.Transparent
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (canSell) {
                        Brush.horizontalGradient(colors = listOf(RoseStart, RoseEnd))
                    } else {
                        Brush.horizontalGradient(colors = listOf(colors.buttonDisabled, colors.buttonDisabled))
                    }
                )
                .clickable(enabled = canSell) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSellClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sell",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (canSell) Color.White else colors.textSecondary
            )
        }
    }
}

@Composable
private fun CoinDetailLoadingContent() {
    val spacing = LocalCryptoSpacing.current
    val colors = LocalCryptoColors.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.md)
    ) {
        // Header skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.width(spacing.md))
            Column {
                SkeletonText(width = 120.dp, height = 28.dp)
                Spacer(modifier = Modifier.height(spacing.xs))
                SkeletonText(width = 60.dp, height = 16.dp)
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.lg))
        
        // Price card skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardBackground)
                .padding(24.dp)
        ) {
            Column {
                SkeletonText(width = 100.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SkeletonText(width = 150.dp, height = 36.dp)
                    SkeletonBox(
                        modifier = Modifier.size(80.dp, 32.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(spacing.md))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(spacing.md))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Quick stats skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            repeat(3) {
                SkeletonBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Stats grid skeleton
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                repeat(2) {
                    SkeletonBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.sm))
        }
    }
}

@Composable
private fun OfflineBanner(modifier: Modifier = Modifier) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.statusError.copy(alpha = 0.15f))
            .padding(spacing.sm)
    ) {
        Text(
            text = "You're offline. Showing cached data.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.statusError
        )
    }
}


// Utility functions

private fun formatPrice(price: Double): String {
    return when {
        price >= 1000 -> formatDecimal(price, 2)
        price >= 1 -> formatDecimal(price, 2)
        price >= 0.01 -> formatDecimal(price, 4)
        else -> formatDecimal(price, 8)
    }
}

private fun formatAmount(amount: Double): String {
    return when {
        amount >= 1000 -> formatDecimal(amount, 2)
        amount >= 1 -> formatDecimal(amount, 4)
        else -> formatDecimal(amount, 8)
    }
}

private fun formatLargeNumber(number: Double): String {
    return when {
        number >= 1_000_000_000_000 -> "${formatDecimal(number / 1_000_000_000_000, 2)}T"
        number >= 1_000_000_000 -> "${formatDecimal(number / 1_000_000_000, 2)}B"
        number >= 1_000_000 -> "${formatDecimal(number / 1_000_000, 2)}M"
        number >= 1_000 -> "${formatDecimal(number / 1_000, 2)}K"
        else -> formatDecimal(number, 2)
    }
}

private fun formatSupply(supply: Double, symbol: String): String {
    val formatted = when {
        supply >= 1_000_000_000 -> "${formatDecimal(supply / 1_000_000_000, 1)}B"
        supply >= 1_000_000 -> "${formatDecimal(supply / 1_000_000, 1)}M"
        supply >= 1_000 -> "${formatDecimal(supply / 1_000, 1)}K"
        else -> formatDecimal(supply, 0)
    }
    return "$formatted ${symbol.uppercase()}"
}

private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(value * factor) / factor
    val parts = rounded.toString().split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) {
        parts[1].take(decimals).padEnd(decimals, '0')
    } else {
        "0".repeat(decimals)
    }
    return if (decimals > 0) "$intPart.$decPart" else intPart
}
