package com.example.valguard.app.coindetail.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import valguard.composeapp.generated.resources.solar__alt_arrow_left_outline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.valguard.app.coindetail.domain.ChartTimeframe
import com.example.valguard.app.coindetail.domain.CoinDetailData
import com.example.valguard.app.coindetail.domain.CoinHoldings
import com.example.valguard.app.components.AlertModal
import com.example.valguard.app.components.BarChart
import com.example.valguard.app.components.BarChartData
import com.example.valguard.app.components.ChartPoint
import com.example.valguard.app.components.CoinIconBox
import com.example.valguard.app.components.EmptyState
import com.example.valguard.app.components.ErrorState
import com.example.valguard.app.components.SkeletonBox
import com.example.valguard.app.components.XAxisLabelGenerator
import com.example.valguard.app.core.util.UiState
import com.example.valguard.app.core.util.getPriceChangeColor
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.Slate900
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.painterResource
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.material_symbols__notifications_outline
import valguard.composeapp.generated.resources.solar__bookmark_bold
import valguard.composeapp.generated.resources.solar__bookmark_linear
import kotlin.math.abs
import kotlin.math.pow


// Custom gradient colors
private val SlateGradientStart = Color(0xFF1E293B) // slate-800
private val SlateGradientEnd = Slate900   // slate-900
private val EmeraldStart = Color(0xFF34D399)       // emerald-400
private val EmeraldEnd = Color(0xFF10B981)         // emerald-500

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
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.showSnackbar) {
        val message = state.snackbarMessage
        if (state.showSnackbar && message != null) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(CoinDetailEvent.DismissSnackbar)
        }
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = colors.accentBlue500,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.backgroundPrimary)
                    .padding(paddingValues)
            ) {
                // Top bar (Compact Navigation Row)
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
                        CoinDetailContent(
                            coinData = coinData.data,
                            holdings = state.holdings,
                            chartState = state.currentChartState,
                            selectedTimeframe = state.selectedTimeframe,
                            isOffline = state.isOffline,
                            onTimeframeSelected = { viewModel.onEvent(CoinDetailEvent.SelectTimeframe(it)) },
                            onSetAlertClick = { viewModel.onEvent(CoinDetailEvent.ShowAlertModal) },
                            onBuyClick = { onBuyClick(coinId) },
                            onSellClick = { onSellClick(coinId) }
                        )
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
        }
    )
    
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
            .height(56.dp) // Increased height
            .padding(horizontal = spacing.md), // Increased padding
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.solar__alt_arrow_left_outline),
                contentDescription = "Back",
                tint = colors.textPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Bookmark Button with Gradient Border/Fill
        val borderGradient = Brush.linearGradient(
            colors = listOf(colors.accentBlue400, colors.accentPurple400)
        )
        
        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (isInWatchlist) {
                        Modifier.background(borderGradient)
                    } else {
                        Modifier.background(colors.cardBackground.copy(alpha = 0.3f))
                    }
                )
                .border(
                    width = 1.dp,
                    brush = borderGradient,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onWatchlistClick() 
                }, 
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (isInWatchlist) painterResource(Res.drawable.solar__bookmark_bold) else painterResource(Res.drawable.solar__bookmark_linear),
                contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                tint = if (isInWatchlist) Color.White else colors.textSecondary.copy(alpha = 0.85f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CoinDetailContent(
    coinData: CoinDetailData,
    holdings: CoinHoldings?,
    chartState: UiState<List<ChartPoint>>,
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
        // Offline indicator (Stay integrated but visible)
        if (isOffline) {
            OfflineBanner(modifier = Modifier.padding(top = spacing.xs, bottom = spacing.md))
        }
        
        // Price display card with integrated identity and gradient background
        PriceDisplayCard(
            coinData = coinData,
            chartState = chartState,
            selectedTimeframe = selectedTimeframe,
            onTimeframeSelected = onTimeframeSelected
        )
        
        Spacer(modifier = Modifier.height(spacing.sm))
        
        // Meta stats (quiet row)
        MetaStatsRow(coinData = coinData)
        
        Spacer(modifier = Modifier.height(spacing.sm))
        
        // Detailed stats grid
        StatsGrid(coinData = coinData)
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Price alert button (Context Tool position)
        PriceAlertButton(onClick = onSetAlertClick)
        
        // Holdings section (only if user owns this coin)
        if (holdings != null && holdings.amountOwned > 0) {
            Spacer(modifier = Modifier.height(14.dp))
            HoldingsCard(holdings = holdings, coinData = coinData)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Buy/Sell buttons with gradients
        ActionButtons(
            onBuyClick = onBuyClick,
            onSellClick = onSellClick,
            canSell = holdings != null && holdings.amountOwned > 0
        )
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}



@Composable
private fun PriceDisplayCard(
    coinData: CoinDetailData,
    chartState: UiState<List<ChartPoint>>,
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
    val animatedPrice by animateFloatAsState(
        targetValue = coinData.price.toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "priceAnimation"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)) // Slightly more rounded for premium feel
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SlateGradientStart, SlateGradientEnd)
                )
            )
            .padding(
                start = spacing.md,
                top = spacing.md,
                end = spacing.md,
                bottom = spacing.sm
            )
    ) {
        Column {
            // Integrated Header: Icon + Name/Symbol
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinIconBox(
                    iconUrl = coinData.iconUrl ?: "",
                    contentDescription = null,
                    size = 32.dp,
                    iconSize = 20.dp,
                    cornerRadius = 8.dp,
                    borderColor = colors.accentPurple400
                )
                
                Spacer(modifier = Modifier.width(spacing.sm))
                
                Column {
                    Text(
                        text = coinData.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = coinData.symbol.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            Text(
                text = "Current Price",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                AutoSizingPriceText(
                    price = animatedPrice.toDouble(),
                    color = colors.textPrimary
                )
                
                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(10.dp))
                        .background(changeColor.copy(alpha = 0.11f))
                        .padding(horizontal = spacing.md, vertical = 6.dp)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${formatDecimal(coinData.change24h, 2)}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = changeColor.copy(alpha = 0.9f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            // Timeframe selector
            TimeframeSelector(
                selectedTimeframe = selectedTimeframe,
                onTimeframeSelected = onTimeframeSelected
            )
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            // Chart Section with State Handling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                contentAlignment = Alignment.Center
            ) {
                when (chartState) {
                    is UiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.semantics { 
                                contentDescription = "Loading price chart" 
                            }
                        ) {
                            SkeletonBox(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Loading history...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                    is UiState.Success -> {
                        // Generate labels with memoization for stability
                        val labels = remember(chartState.data, selectedTimeframe) {
                            XAxisLabelGenerator.generate(
                                points = chartState.data,
                                timeframe = selectedTimeframe,
                                labelCount = 4
                            )
                        }
                        
                        BarChart(
                            data = BarChartData(
                                values = chartState.data.map { it.price },
                                labels = labels
                            ),
                            modifier = Modifier.fillMaxSize(),
                            animate = true,
                            isLoading = false
                        )
                    }
                    is UiState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Chart unavailable",
                                color = colors.textSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = { onTimeframeSelected(selectedTimeframe) }) {
                                Text("Retry", color = colors.accentBlue400)
                            }
                        }
                    }
                    is UiState.Empty -> {
                        Text(
                            text = "No historical data for this period",
                            color = colors.textSecondary.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
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
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ChartTimeframe.entries.forEach { timeframe ->
            val isSelected = timeframe == selectedTimeframe
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) {
                            Brush.horizontalGradient(
                                colors = listOf(colors.accentBlue500, colors.accentPurple500)
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(SlateGradientStart, SlateGradientStart)
                            )
                        }
                    )
                    .clickable { onTimeframeSelected(timeframe) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeframe.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun MetaStatsRow(coinData: CoinDetailData) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Supply · ${formatSupply(coinData.circulatingSupply, coinData.symbol)}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.7f)
        )
        Text(
            text = "|",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.3f)
        )
        Text(
            text = "ATH · \$${formatPrice(coinData.allTimeHigh)}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.7f)
        )
        Text(
            text = "|",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.3f)
        )
        Text(
            text = "Rank · #${coinData.marketCapRank}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StatsGrid(coinData: CoinDetailData) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            EnhancedStatCard(
                label = "Market Cap",
                value = "\$${formatLargeNumber(coinData.marketCap)}",
                isPrimary = true,
                modifier = Modifier.weight(1f)
            )
            EnhancedStatCard(
                label = "24h Volume",
                value = "\$${formatLargeNumber(coinData.volume24h)}",
                isPrimary = false,
                modifier = Modifier.weight(1f)
            )
        }
        // 24h Range (combined High/Low)
        RangeCard(
            high = coinData.high24h,
            low = coinData.low24h,
            currentPrice = coinData.price
        )
    }
}


@Composable
private fun EnhancedStatCard(
    label: String,
    value: String,
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .height(82.dp) // Fixed compact height
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground.copy(alpha = 0.4f))
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
            .padding(spacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = colors.textTertiary.copy(alpha = 0.8f)
            )
            val (value, suffix) = formatLargeNumberWithSuffix(value)
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge, // Same size for both
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 1.dp, bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun RangeCard(
    high: Double?,
    low: Double?,
    currentPrice: Double
) {
    // Don't show range card if data is missing
    if (high == null || low == null) return
    
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Fixed compact height
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground.copy(alpha = 0.4f))
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
            .padding(spacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "24h Range",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = colors.textTertiary.copy(alpha = 0.8f)
            )
            
            // Range indicator bar with current price dot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.textSecondary.copy(alpha = 0.15f))
            ) {
                // Calculate position percentage
                val range = (high - low).coerceAtLeast(0.000001)
                val position = ((currentPrice - low) / range).coerceIn(0.0, 1.0).toFloat()
                
                // The dot
                val dotColor = when {
                    position > 0.85f -> colors.profit.copy(alpha = 0.9f)
                    position < 0.15f -> colors.loss.copy(alpha = 0.9f)
                    else -> Color.White
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(position)
                        .padding(end = 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(6.dp)
                            .background(dotColor, RoundedCornerShape(3.dp))
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Low",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\$${formatPrice(low)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.loss.copy(alpha = 0.85f)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "High",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\$${formatPrice(high)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.profit.copy(alpha = 0.85f)
                    )
                }
            }
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
            .height(40.dp),
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(
                colors = listOf(
                    colors.textSecondary.copy(alpha = 0.3f),
                    colors.textSecondary.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Icon(
            painter = painterResource(Res.drawable.material_symbols__notifications_outline),
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(spacing.xs))
        Text(
            text = "Set Price Alert",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = colors.textSecondary
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
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = if (isProfit) colors.profit.copy(alpha = 0.2f) else colors.loss.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        profitLossColor.copy(alpha = 0.3f),
                        profitLossColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .background(colors.cardBackground)
            .padding(spacing.md)
    ) {
        Column {
            Text(
                text = "Your Holdings",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${formatAmount(holdings.amountOwned)} ${coinData.symbol.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\$${formatPrice(holdings.currentValue)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.xs))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Avg. Purchase Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "\$${formatPrice(holdings.averagePurchasePrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Profit/Loss",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${if (isProfit) "+" else ""}\$${formatPrice(abs(holdings.profitLoss))} (${formatDecimal(holdings.profitLossPercentage, 2)}%)",
                        style = MaterialTheme.typography.bodySmall,
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
                .height(48.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = EmeraldStart.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(14.dp))
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "↗",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Buy",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        // Sell button (outlined/secondary)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = 2.dp,
                    color = if (canSell) colors.loss.copy(alpha = 0.5f) else colors.textSecondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp)
                )
                .background(colors.backgroundPrimary)
                .clickable(enabled = canSell) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSellClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "↘",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = (if (canSell) colors.loss else colors.textSecondary).copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Sell",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (canSell) colors.loss else colors.textSecondary
                )
            }
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
            .semantics { contentDescription = "Loading coin details" }
            .pointerInput(Unit) {
                // Block interactions during loading
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
    ) {
        // Integrated Card Skeleton (Matches new layout)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.cardBackground)
                .padding(spacing.lg)
        ) {
            Column {
                // Identity row shimmer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp))
                    Spacer(modifier = Modifier.width(spacing.md))
                    Column {
                        SkeletonBox(modifier = Modifier.size(120.dp, 20.dp), shape = RoundedCornerShape(4.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        SkeletonBox(modifier = Modifier.size(60.dp, 12.dp), shape = RoundedCornerShape(3.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(spacing.lg))
                
                // Price shimmer
                SkeletonBox(modifier = Modifier.size(100.dp, 12.dp), shape = RoundedCornerShape(3.dp))
                Spacer(modifier = Modifier.height(spacing.sm))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    SkeletonBox(modifier = Modifier.size(160.dp, 40.dp), shape = RoundedCornerShape(8.dp))
                    SkeletonBox(modifier = Modifier.size(70.dp, 30.dp), shape = RoundedCornerShape(10.dp))
                }
                
                Spacer(modifier = Modifier.height(spacing.md))
                
                // Timeframe shimmer
                SkeletonBox(modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(8.dp))
                
                Spacer(modifier = Modifier.height(spacing.md))
                
                // Chart shimmer
                SkeletonBox(modifier = Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Detailed stats skeleton
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            repeat(3) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    SkeletonBox(modifier = Modifier.weight(1f).height(100.dp), shape = RoundedCornerShape(16.dp))
                    SkeletonBox(modifier = Modifier.weight(1f).height(100.dp), shape = RoundedCornerShape(16.dp))
                }
            }
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


/**
 * Auto-sizing price text that adjusts font size to fit on a single line.
 * Starts at a base size and scales down as needed for longer price strings.
 */
@Composable
private fun AutoSizingPriceText(
    price: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val priceText = "\$${formatPrice(price)}"
    
    // Calculate font size based on string length
    // Shorter prices (e.g., $97,000) get larger text, longer ones scale down
    val fontSize = when {
        priceText.length <= 8 -> 24.sp   // e.g., $97,000
        priceText.length <= 10 -> 22.sp  // e.g., $100,000.00
        priceText.length <= 12 -> 20.sp  // e.g., $1,234,567.89
        priceText.length <= 14 -> 18.sp  // e.g., very long prices
        else -> 16.sp                     // extremely long prices
    }
    
    Text(
        text = priceText,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        ),
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Visible
    )
}


// Utility functions

private fun formatPrice(price: Double?): String {
    if (price == null) return "N/A"
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

private fun formatLargeNumber(number: Double?): String {
    if (number == null) return "N/A"
    return when {
        number >= 1_000_000_000_000 -> "${formatDecimal(number / 1_000_000_000_000, 2)}T"
        number >= 1_000_000_000 -> "${formatDecimal(number / 1_000_000_000, 2)}B"
        number >= 1_000_000 -> "${formatDecimal(number / 1_000_000, 2)}M"
        number >= 1_000 -> "${formatDecimal(number / 1_000, 2)}K"
        else -> formatDecimal(number, 2)
    }
}


private fun formatLargeNumberWithSuffix(text: String): Pair<String, String> {
    return when {
        text.contains("T") -> text.substringBefore("T") to "T"
        text.contains("B") -> text.substringBefore("B") to "B"
        text.contains("M") -> text.substringBefore("M") to "M"
        text.contains("K") -> text.substringBefore("K") to "K"
        else -> text to ""
    }
}

private fun formatSupply(supply: Double?, symbol: String): String {
    if (supply == null) return "N/A"
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
