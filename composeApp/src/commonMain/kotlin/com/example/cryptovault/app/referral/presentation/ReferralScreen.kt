package com.example.cryptovault.app.referral.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cryptovault.app.components.ScreenHeader
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoSpacing
import kotlin.math.pow

@Composable
fun ReferralScreen(
    onBack: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    // Mock state for MVP with mutable state
    var state by remember {
        mutableStateOf(
            ReferralState(
                referralCode = "CRYPTO123ABC",
                totalEarned = 125.50,
                pendingRewards = 25.00,
                totalReferrals = 8,
                activeReferrals = 5
            )
        )
    }
    
    // Event handler
    val onEvent: (ReferralEvent) -> Unit = { event ->
        when (event) {
            is ReferralEvent.CopyCode -> {
                // TODO: Implement clipboard copy
                state = state.copy(codeCopied = true)
            }
            is ReferralEvent.ShareCode -> {
                // TODO: Implement share functionality
            }
            is ReferralEvent.DismissCopiedMessage -> {
                state = state.copy(codeCopied = false)
            }
            is ReferralEvent.LoadReferralData -> {
                // TODO: Load data from repository
            }
        }
    }
    
    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Referral Program",
                subtitle = "Invite friends & earn rewards",
                onBackClick = onBack
            )
        },
        containerColor = colors.backgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            EarningsCard(
                totalEarned = state.totalEarned,
                pendingRewards = state.pendingRewards
            )
            
            StatsGrid(
                totalReferrals = state.totalReferrals,
                activeReferrals = state.activeReferrals
            )
            
            ReferralCodeCard(
                code = state.referralCode,
                codeCopied = state.codeCopied,
                onCopy = { onEvent(ReferralEvent.CopyCode) },
                onShare = { onEvent(ReferralEvent.ShareCode) }
            )
            
            ComingSoonBanner()
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    
    // Dismiss copied message after delay
    LaunchedEffect(state.codeCopied) {
        if (state.codeCopied) {
            kotlinx.coroutines.delay(2000)
            onEvent(ReferralEvent.DismissCopiedMessage)
        }
    }
}

@Composable
private fun EarningsCard(
    totalEarned: Double,
    pendingRewards: Double
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.profit,
                        colors.profit.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(spacing.lg)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Total Earned",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.backgroundPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = "$${formatAmount(totalEarned)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = colors.backgroundPrimary
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            Text(
                text = "Pending: $${formatAmount(pendingRewards)}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.backgroundPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun StatsGrid(
    totalReferrals: Int,
    activeReferrals: Int
) {
    LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        StatCard(
            label = "Total Referrals",
            value = totalReferrals.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Active",
            value = activeReferrals.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .padding(spacing.md)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun ReferralCodeCard(
    code: String,
    codeCopied: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Your Referral Code",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.backgroundPrimary)
                    .padding(horizontal = spacing.lg, vertical = spacing.md)
            ) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentBlue400,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp)
                )
            }
            
            if (codeCopied) {
                Spacer(modifier = Modifier.height(spacing.xs))
                Text(
                    text = "Copied to clipboard!",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.profit
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text("Copy")
                }
                
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentBlue400)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
private fun ComingSoonBanner() {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.accentBlue400.copy(alpha = 0.15f))
            .padding(spacing.md)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🚀 Coming Soon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.accentBlue400
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = "Referral rewards will be available in a future update. Start inviting friends now to be ready!",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    return formatDecimal(amount, 2)
}

private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(value * factor) / factor
    val parts = rounded.toString().split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].take(decimals).padEnd(decimals, '0') else "0".repeat(decimals)
    return "$intPart.$decPart"
}
