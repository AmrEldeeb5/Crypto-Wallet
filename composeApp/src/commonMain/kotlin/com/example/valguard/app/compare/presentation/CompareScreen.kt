package com.example.valguard.app.compare.presentation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.valguard.app.compare.domain.SavedComparison
import com.example.valguard.app.components.ErrorState
import com.example.valguard.app.components.SkeletonCard
import com.example.valguard.app.core.util.UiState
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow

// Locked row height for consistent spacing
private val ComparisonRowHeight = 56.dp

@Composable
fun CompareScreen() {
    val viewModel = koinViewModel<CompareViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle Snackbar from state
    LaunchedEffect(state.showSnackbar) {
        val message = state.snackbarMessage
        if (state.showSnackbar && message != null) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(CompareEvent.DismissSnackbar)
        }
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = colors.accentBlue500,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = colors.backgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = spacing.md,
                end = spacing.md,
                top = spacing.md,
                bottom = 100.dp // Clear floating bottom nav
            ),
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
                    onSelectCoin2 = { viewModel.onEvent(CompareEvent.ShowCoinSelector(CoinSelectorTarget.COIN_2)) }
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
                        horizontalArrangement = Arrangement.spacedBy(spacing.md) // Increased spacing
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(CompareEvent.ClearComparison) },
                            modifier = Modifier.weight(1f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                colors.border.copy(alpha = 0.3f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.textSecondary
                            )
                        ) {
                            Text("Clear", color = colors.textSecondary.copy(alpha = 0.7f))
                        }

                        Button(
                            onClick = { viewModel.onEvent(CompareEvent.SaveComparison) },
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    8.dp,
                                    RoundedCornerShape(24.dp),
                                    spotColor = colors.accentBlue400
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(ButtonDefaults.MinHeight)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(colors.accentBlue400, colors.accentPurple400)
                                        ),
                                        shape = ButtonDefaults.shape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = androidx.compose.ui.graphics.Color.White
                                    )
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(
                                        "Save Comparison",
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
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
    val spacing = LocalCryptoSpacing.current
    
    Column {
        Text(
            text = "Compare Coins",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = "Real-time market comparison",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(spacing.sm))
        
        // Identity Divider
        Box(
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(colors.accentBlue400, colors.accentPurple400)
                    )
                )
        )
    }
}

@Composable
private fun CoinSelectorRow(
    coin1: CoinSlot?,
    coin2: CoinSlot?,
    onSelectCoin1: () -> Unit,
    onSelectCoin2: () -> Unit
) {
    val colors = LocalCryptoColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinSelectorCard(
            coin = coin1,
            borderColor = colors.accentBlue400,
            onClick = onSelectCoin1,
            modifier = Modifier.weight(1f)
        )
        
        CoinSelectorCard(
            coin = coin2,
            borderColor = colors.accentPurple400,
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
    
    val shape = RoundedCornerShape(24.dp)
    
    // Selection micro-animation
    val isSelected = coin != null
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "coinScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.2f else 0.05f,
        animationSpec = tween(500),
        label = "glowAlpha"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = shape,
                        spotColor = borderColor.copy(alpha = 0.3f)
                    )
                } else Modifier
            )
            .clip(shape)
            .background(colors.cardBackground.copy(alpha = 0.5f))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) borderColor.copy(alpha = 0.6f) else borderColor.copy(alpha = 0.15f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (coin != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon Box with accent glow
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(borderColor.copy(alpha = 0.2f), borderColor.copy(alpha = 0.05f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = coin.iconUrl,
                        contentDescription = "${coin.name} icon",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Column {
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = coin.symbol.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        // Mini price context
                        Text(
                            text = "$${formatPrice(coin.price)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.backgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Select coin",
                        tint = colors.textSecondary
                    )
                }
                Text(
                    text = "Select",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
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
    
    val shape = RoundedCornerShape(24.dp)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.cardBackground.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = colors.border.copy(alpha = 0.1f),
                shape = shape
            )
            .padding(spacing.xl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            val metrics: List<Pair<String, @Composable () -> Unit>> = listOf(
                "Current Price" to { 
                    MetricValues(formatPrice(coin1.price), formatPrice(coin2.price), comparisonData.priceWinner)
                },
                "24h Change" to { 
                    MetricValues(
                        "${formatPercentage(coin1.change24h)}%", 
                        "${formatPercentage(coin2.change24h)}%", 
                        comparisonData.change24hWinner,
                        isChange = true,
                        pos1 = coin1.change24h > 0,
                        pos2 = coin2.change24h > 0
                    )
                },
                "Market Cap" to {
                    MetricValues(
                        value1 = formatLargeNumber(coin1.marketCap),
                        value2 = formatLargeNumber(coin2.marketCap),
                        winner = comparisonData.marketCapWinner,
                        raw1 = coin1.marketCap,
                        raw2 = coin2.marketCap,
                        showBar = true
                    )
                },
                "24h Volume" to {
                    MetricValues(
                        value1 = formatLargeNumber(coin1.volume24h),
                        value2 = formatLargeNumber(coin2.volume24h),
                        winner = comparisonData.volumeWinner,
                        raw1 = coin1.volume24h,
                        raw2 = coin2.volume24h,
                        showBar = true
                    )
                },
                "Market Rank" to {
                    MetricValues(
                        value1 = "#${coin1.marketCapRank}",
                        value2 = "#${coin2.marketCapRank}",
                        winner = comparisonData.rankWinner,
                        isRank = true
                    )
                }
            )
            
            metrics.forEachIndexed { index, (label, content) ->
                val isCurrentPrice = index == 0
                
                // External spacing BEFORE Current Price for emphasis
                if (isCurrentPrice) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        color = colors.border.copy(alpha = 0.15f),
                        thickness = 1.5.dp
                    )
                }
                
                ComparisonRow(label = label, content = content)

                // External spacing and divider AFTER Current Price
                if (isCurrentPrice) {
                    HorizontalDivider(
                        color = colors.border.copy(alpha = 0.15f),
                        thickness = 1.5.dp
                    )
                    Spacer(Modifier.height(8.dp))
                } else if (index < metrics.size - 1) {
                    // Standard divider for other rows
                    HorizontalDivider(
                        color = colors.border.copy(alpha = 0.05f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = spacing.md)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.lg))
            
            // Quick Verdict Row
            QuickVerdictRow(coin1, coin2, comparisonData)
        }
    }
}

@Composable
private fun QuickVerdictRow(
    coin1: CoinSlot,
    coin2: CoinSlot,
    comparisonData: ComparisonData
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current

    val verdictText: AnnotatedString = remember(coin1, coin2, comparisonData) {
        generateVerdict(coin1, coin2, comparisonData)
    }

    val whyItMatters: String = remember(comparisonData) {
        if (comparisonData.marketCapWinner == MetricWinner.COIN_1) {
            "${coin1.symbol.uppercase()}'s dominance suggests lower volatility and stronger institutional confidence today."
        } else {
            "${coin2.symbol.uppercase()}'s dominance suggests lower volatility and stronger institutional confidence today."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.accentBlue400.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .border(1.dp, colors.accentBlue400.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(spacing.md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            // Header with AI Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(colors.accentBlue400.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AI Insight",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.accentBlue400,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Verdict Text
            Text(
                text = verdictText,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
            )

            HorizontalDivider(
                color = colors.accentBlue400.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Why it matters
            Column {
                Text(
                    text = "Why this matters",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = whyItMatters,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary.copy(alpha = 0.9f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

private fun generateVerdict(
    coin1: CoinSlot,
    coin2: CoinSlot,
    data: ComparisonData
): AnnotatedString {
    val liquidityLeader =
        if (data.marketCapWinner == MetricWinner.COIN_1) coin1.symbol else coin2.symbol
    val growthLeader =
        if (data.change24hWinner == MetricWinner.COIN_1) coin1.symbol else coin2.symbol

    return buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(liquidityLeader.uppercase())
        }
        append(" dominates the ")
        withStyle(SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF4CAF50))) { // Greenish
            append("market")
        }
        append(", while ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(growthLeader.uppercase())
        }
        append(" shows stronger ")
        withStyle(SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF2196F3))) { // Blueish
            append("price momentum")
        }
        append(" today.")
    }
}

@Composable
private fun MetricValues(
    value1: String,
    value2: String,
    winner: MetricWinner,
    isChange: Boolean = false,
    isRank: Boolean = false,
    pos1: Boolean = true,
    pos2: Boolean = true,
    raw1: Double = 0.0,
    raw2: Double = 0.0,
    showBar: Boolean = false
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current

    // Calculate bar fractions relative to max
    val maxVal = maxOf(raw1, raw2).coerceAtLeast(1.0)
    val frac1 = (raw1 / maxVal).toFloat().coerceIn(0f, 1f)
    val frac2 = (raw2 / maxVal).toFloat().coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.xl),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetricValueCell(
            value = value1,
            isWinner = winner == MetricWinner.COIN_1,
            color = if (isChange) (if (pos1) colors.profit else colors.loss) else colors.textPrimary,
            modifier = Modifier.weight(1f),
            barFraction = if (showBar) frac1 else 0f,
            isRank = isRank,
            rankUp = !pos1 // For rank, we don't have pos1/pos2 flags passed correctly for arrows, handled below
        )
        MetricValueCell(
            value = value2,
            isWinner = winner == MetricWinner.COIN_2,
            color = if (isChange) (if (pos2) colors.profit else colors.loss) else colors.textPrimary,
            modifier = Modifier.weight(1f),
            barFraction = if (showBar) frac2 else 0f,
            isRank = isRank,
            rankUp = !pos2
        )
    }
}

@Composable
private fun MetricValueCell(
    value: String,
    isWinner: Boolean,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    barFraction: Float = 0f,
    isRank: Boolean = false,
    rankUp: Boolean = false // Not used effectively for rank yet without diff logic, keeping simple
) {
    val colors = LocalCryptoColors.current

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isWinner) 1f else 0.7f,
        animationSpec = tween(500),
        label = "winnerAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                // Apply pill treatment to all rank cells and winners
                if (isWinner || isRank) {
                    Modifier
                        .background(colors.accentBlue400.copy(alpha = if (isWinner) 0.12f else 0.08f))
                        .border(
                            1.dp,
                            colors.accentBlue400.copy(alpha = if (isWinner) 0.2f else 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                } else Modifier
            )
            // Normalized padding: horizontal 10dp, vertical 6dp max (4dp for rank to reduce bloat)
            .padding(
                horizontal = 10.dp,
                vertical = if (isRank) 4.dp else 6.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background Bar
        if (barFraction > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barFraction)
                        .height(4.dp)
                        .align(Alignment.BottomStart)
                        .background(color.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Crown removed per feedback - rank pill treatment is sufficient emphasis
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isWinner) FontWeight.Black else FontWeight.Medium, // Bolder for winner
                color = color.copy(alpha = animatedAlpha),
                textAlign = TextAlign.Center
            )
            
            if (isWinner && !isRank) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle, // "Leading" badge
                    contentDescription = "Winner",
                    tint = colors.accentBlue400,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    content: @Composable () -> Unit
) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ComparisonRowHeight),  // Locked row height for consistency
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.weight(1.2f)
        )
        
        Box(modifier = Modifier.weight(2f)) {
            content()
        }
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
        Column {
            Text(
                text = "Saved Comparisons",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = "Revisit past decisions",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary.copy(alpha = 0.7f)
            )
        }
        
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
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground.copy(alpha = 0.5f))
            .clickable(onClick = onLoad)
            .padding(spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stacked Icons Pair
                Box(modifier = Modifier.size(48.dp)) {
                    AsyncImage(
                        model = comparison.coin1IconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .align(Alignment.TopStart)
                    )
                    AsyncImage(
                        model = comparison.coin2IconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .align(Alignment.BottomEnd)
                            .border(2.dp, colors.cardBackground, RoundedCornerShape(8.dp))
                    )
                }
                
                Column {
                    Text(
                        text = "${comparison.coin1Symbol.uppercase()} / ${comparison.coin2Symbol.uppercase()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Saved ${formatSavedTime(comparison.savedAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary.copy(alpha = 0.6f)
                    )
                }
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(32.dp)
                    .background(colors.loss.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colors.loss,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatSavedTime(@Suppress("UNUSED_PARAMETER") timestamp: Long): String {
    // Very simple mock time-ago (in real app, use kotlinx-datetime)
    return "just now" 
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
