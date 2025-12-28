package com.example.cryptovault.app.leaderboard.presentation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptovault.app.components.EmptyState
import com.example.cryptovault.app.components.ErrorState
import com.example.cryptovault.app.components.ScreenHeader
import com.example.cryptovault.app.components.SkeletonCard
import com.example.cryptovault.app.core.util.UiState
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit
) {
    val viewModel = koinViewModel<LeaderboardViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalCryptoColors.current
    LocalCryptoSpacing.current
    
    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Leaderboard",
                subtitle = "Top performers",
                onBackClick = onBack
            )
        },
        containerColor = colors.backgroundPrimary
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TimeframeSelector(
                selected = state.selectedTimeframe,
                onSelect = { viewModel.onEvent(LeaderboardEvent.SelectTimeframe(it)) }
            )
            LeaderboardContent(state = state, onRetry = { viewModel.onEvent(LeaderboardEvent.Retry) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeframeSelector(
    selected: LeaderboardTimeframe,
    onSelect: (LeaderboardTimeframe) -> Unit
) {
    val spacing = LocalCryptoSpacing.current
    
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md)
    ) {
        LeaderboardTimeframe.entries.forEachIndexed { index, timeframe ->
            SegmentedButton(
                selected = timeframe == selected,
                onClick = { onSelect(timeframe) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = LeaderboardTimeframe.entries.size)
            ) {
                Text(timeframe.displayName, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun LeaderboardContent(state: LeaderboardState, onRetry: () -> Unit) {
    val spacing = LocalCryptoSpacing.current
    
    when (val leaderboard = state.leaderboard) {
        is UiState.Loading -> LeaderboardLoadingContent()
        is UiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(leaderboard.data, key = { it.userId }) { entry ->
                    LeaderboardEntryCard(entry = entry)
                }
            }
        }
        is UiState.Error -> ErrorState(message = leaderboard.message, onRetry = onRetry, modifier = Modifier.fillMaxSize())
        is UiState.Empty -> {
            if (!state.hasPortfolio) {
                EmptyState(
                    title = "No Portfolio Yet",
                    description = "Start trading to join the leaderboard",
                    actionLabel = null,
                    onAction = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                EmptyState(
                    title = "No Data",
                    description = "Leaderboard data unavailable",
                    actionLabel = null,
                    onAction = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
private fun LeaderboardEntryCard(entry: LeaderboardEntry) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    val isTopThree = LeaderboardCalculator.isTopThree(entry.rank)
    val badge = LeaderboardCalculator.getRankBadge(entry.rank)
    
    val borderModifier = if (entry.isCurrentUser) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(colors.accentBlue400, colors.accentPurple400)),
            shape = RoundedCornerShape(16.dp)
        )
    } else Modifier
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
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
                // Rank
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isTopThree) colors.profit.copy(alpha = 0.2f) else colors.backgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    if (badge != null) {
                        Text(text = badge, style = MaterialTheme.typography.titleMedium)
                    } else {
                        Text(
                            text = "#${entry.rank}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }
                
                // User info
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                            color = colors.textPrimary
                        )
                        if (entry.isCurrentUser) {
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "(You)",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.accentBlue400
                            )
                        }
                    }
                }
            }
            
            // Return percentage
            Text(
                text = "${if (entry.returnPercentage >= 0) "+" else ""}${formatDecimal(entry.returnPercentage, 2)}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (entry.returnPercentage >= 0) colors.profit else colors.loss
            )
        }
    }
}

@Composable
private fun LeaderboardLoadingContent() {
    val spacing = LocalCryptoSpacing.current
    
    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        repeat(5) { SkeletonCard() }
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