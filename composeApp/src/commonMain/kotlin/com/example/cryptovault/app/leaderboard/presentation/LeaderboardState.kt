package com.example.cryptovault.app.leaderboard.presentation

import com.example.cryptovault.app.core.util.UiState

data class LeaderboardState(
    val leaderboard: UiState<List<LeaderboardEntry>> = UiState.Loading,
    val userEntry: LeaderboardEntry? = null,
    val selectedTimeframe: LeaderboardTimeframe = LeaderboardTimeframe.WEEK,
    val hasPortfolio: Boolean = true
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val returnPercentage: Double,
    val isCurrentUser: Boolean = false
)

enum class LeaderboardTimeframe(val displayName: String) {
    DAY("24H"),
    WEEK("7D"),
    MONTH("30D"),
    ALL_TIME("All Time")
}

sealed class LeaderboardEvent {
    data object LoadLeaderboard : LeaderboardEvent()
    data class SelectTimeframe(val timeframe: LeaderboardTimeframe) : LeaderboardEvent()
    data object Retry : LeaderboardEvent()
}

object LeaderboardCalculator {
    
    fun calculateReturn(currentValue: Double, investedAmount: Double): Double {
        if (investedAmount == 0.0) return 0.0
        return ((currentValue - investedAmount) / investedAmount) * 100
    }
    
    fun getRankBadge(rank: Int): String? {
        return when (rank) {
            1 -> "🏆"
            2 -> "🥈"
            3 -> "🥉"
            else -> null
        }
    }
    
    fun isTopThree(rank: Int): Boolean {
        return rank in 1..3
    }
}
