package com.example.cryptovault.app.leaderboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptovault.app.core.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(LeaderboardState())
    val state: StateFlow<LeaderboardState> = _state.asStateFlow()
    
    init {
        loadLeaderboard()
    }
    
    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.LoadLeaderboard -> loadLeaderboard()
            is LeaderboardEvent.SelectTimeframe -> selectTimeframe(event.timeframe)
            is LeaderboardEvent.Retry -> loadLeaderboard()
        }
    }
    
    private fun loadLeaderboard() {
        _state.update { it.copy(leaderboard = UiState.Loading) }
        
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(500)
                
                val entries = generateMockLeaderboard(_state.value.selectedTimeframe)
                val userEntry = entries.find { it.isCurrentUser }
                
                _state.update {
                    it.copy(
                        leaderboard = if (entries.isEmpty()) UiState.Empty else UiState.Success(entries),
                        userEntry = userEntry,
                        hasPortfolio = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(leaderboard = UiState.Error(e.message ?: "Failed to load leaderboard"))
                }
            }
        }
    }
    
    private fun selectTimeframe(timeframe: LeaderboardTimeframe) {
        _state.update { it.copy(selectedTimeframe = timeframe) }
        loadLeaderboard()
    }
    
    private fun generateMockLeaderboard(timeframe: LeaderboardTimeframe): List<LeaderboardEntry> {
        // Generate mock data based on timeframe
        val multiplier = when (timeframe) {
            LeaderboardTimeframe.DAY -> 0.1
            LeaderboardTimeframe.WEEK -> 0.5
            LeaderboardTimeframe.MONTH -> 1.0
            LeaderboardTimeframe.ALL_TIME -> 2.0
        }
        
        val mockUsers = listOf(
            Triple("CryptoKing", "user1", 156.78 * multiplier),
            Triple("DiamondHands", "user2", 134.56 * multiplier),
            Triple("MoonShot", "user3", 98.34 * multiplier),
            Triple("HODLer", "user4", 87.65 * multiplier),
            Triple("SatoshiFan", "user5", 76.54 * multiplier),
            Triple("You", "currentUser", 45.23 * multiplier),
            Triple("BlockchainBob", "user6", 43.21 * multiplier),
            Triple("CoinCollector", "user7", 32.10 * multiplier),
            Triple("TokenTrader", "user8", 21.09 * multiplier),
            Triple("AltcoinAce", "user9", 10.98 * multiplier)
        )
        
        return mockUsers
            .sortedByDescending { it.third }
            .mapIndexed { index, (name, id, returnPct) ->
                LeaderboardEntry(
                    rank = index + 1,
                    userId = id,
                    displayName = name,
                    returnPercentage = returnPct,
                    isCurrentUser = id == "currentUser"
                )
            }
    }
}
