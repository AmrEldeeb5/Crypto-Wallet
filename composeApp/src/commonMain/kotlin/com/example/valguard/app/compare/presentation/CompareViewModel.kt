package com.example.valguard.app.compare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.compare.data.ComparisonRepository
import com.example.valguard.app.compare.domain.SavedComparison
import com.example.valguard.app.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompareViewModel(
    private val comparisonRepository: ComparisonRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CompareState())
    val state: StateFlow<CompareState> = _state.asStateFlow()
    
    init {
        loadSavedComparisons()
    }
    
    fun onEvent(event: CompareEvent) {
        when (event) {
            is CompareEvent.LoadSavedComparisons -> loadSavedComparisons()
            is CompareEvent.ShowCoinSelector -> showCoinSelector(event.target)
            is CompareEvent.HideCoinSelector -> hideCoinSelector()
            is CompareEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is CompareEvent.SelectCoin -> selectCoin(event.coinId, event.name, event.symbol, event.iconUrl)
            is CompareEvent.SwapCoins -> swapCoins()
            is CompareEvent.SaveComparison -> saveComparison()
            is CompareEvent.LoadSavedComparison -> loadSavedComparison(event.comparison)
            is CompareEvent.DeleteSavedComparison -> deleteSavedComparison(event.comparisonId)
            is CompareEvent.ClearComparison -> clearComparison()
            is CompareEvent.DismissSnackbar -> _state.update { it.copy(showSnackbar = false, snackbarMessage = null) }
            is CompareEvent.Retry -> loadSavedComparisons()
        }
    }
    
    private fun loadSavedComparisons() {
        _state.update { it.copy(savedComparisons = UiState.Loading.Initial) }
        
        viewModelScope.launch {
            comparisonRepository.getAllComparisons()
                .catch { e ->
                    _state.update { it.copy(savedComparisons = UiState.Error(e.message ?: "Failed to load comparisons")) }
                }
                .collect { comparisons ->
                    _state.update {
                        it.copy(
                            savedComparisons = if (comparisons.isEmpty()) UiState.Empty() else UiState.Success(comparisons)
                        )
                    }
                }
        }
    }
    
    private fun showCoinSelector(target: CoinSelectorTarget) {
        _state.update { it.copy(showCoinSelector = target, coinSearchQuery = "") }
    }
    
    private fun hideCoinSelector() {
        _state.update { it.copy(showCoinSelector = null, coinSearchQuery = "") }
    }
    
    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(coinSearchQuery = query) }
    }
    
    private fun selectCoin(coinId: String, name: String, symbol: String, iconUrl: String) {
        val target = _state.value.showCoinSelector ?: return
        
        // Create a coin slot with mock data (in real app, would fetch from API)
        val coinSlot = CoinSlot(
            id = coinId,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl,
            price = getMockPrice(coinId),
            change24h = getMockChange(coinId),
            marketCap = getMockMarketCap(coinId),
            volume24h = getMockVolume(coinId),
            marketCapRank = getMockRank(coinId),
            circulatingSupply = getMockSupply(coinId)
        )
        
        _state.update { currentState ->
            val newState = when (target) {
                CoinSelectorTarget.COIN_1 -> currentState.copy(coin1 = coinSlot)
                CoinSelectorTarget.COIN_2 -> currentState.copy(coin2 = coinSlot)
            }
            
            // Calculate comparison if both coins are selected
            val comparisonData = if (newState.coin1 != null && newState.coin2 != null) {
                ComparisonCalculator.calculateComparison(newState.coin1, newState.coin2)
            } else null
            
            newState.copy(
                comparisonData = comparisonData,
                showCoinSelector = null,
                coinSearchQuery = ""
            )
        }
    }
    
    private fun swapCoins() {
        _state.update { currentState ->
            val newState = currentState.copy(
                coin1 = currentState.coin2,
                coin2 = currentState.coin1
            )
            
            val comparisonData = if (newState.coin1 != null && newState.coin2 != null) {
                ComparisonCalculator.calculateComparison(newState.coin1, newState.coin2)
            } else null
            
            newState.copy(comparisonData = comparisonData)
        }
    }
    
    private fun saveComparison() {
        val coin1 = _state.value.coin1 ?: return
        val coin2 = _state.value.coin2 ?: return
        
        viewModelScope.launch {
            comparisonRepository.saveComparison(
                coin1Id = coin1.id,
                coin1Name = coin1.name,
                coin1Symbol = coin1.symbol,
                coin1IconUrl = coin1.iconUrl,
                coin2Id = coin2.id,
                coin2Name = coin2.name,
                coin2Symbol = coin2.symbol,
                coin2IconUrl = coin2.iconUrl
            )
            _state.update { 
                it.copy(
                    snackbarMessage = "${coin1.symbol.uppercase()} vs ${coin2.symbol.uppercase()} saved",
                    showSnackbar = true
                )
            }
        }
    }
    
    private fun loadSavedComparison(comparison: SavedComparison) {
        val coin1 = CoinSlot(
            id = comparison.coin1Id,
            name = comparison.coin1Name,
            symbol = comparison.coin1Symbol,
            iconUrl = comparison.coin1IconUrl,
            price = getMockPrice(comparison.coin1Id),
            change24h = getMockChange(comparison.coin1Id),
            marketCap = getMockMarketCap(comparison.coin1Id),
            volume24h = getMockVolume(comparison.coin1Id),
            marketCapRank = getMockRank(comparison.coin1Id),
            circulatingSupply = getMockSupply(comparison.coin1Id)
        )
        
        val coin2 = CoinSlot(
            id = comparison.coin2Id,
            name = comparison.coin2Name,
            symbol = comparison.coin2Symbol,
            iconUrl = comparison.coin2IconUrl,
            price = getMockPrice(comparison.coin2Id),
            change24h = getMockChange(comparison.coin2Id),
            marketCap = getMockMarketCap(comparison.coin2Id),
            volume24h = getMockVolume(comparison.coin2Id),
            marketCapRank = getMockRank(comparison.coin2Id),
            circulatingSupply = getMockSupply(comparison.coin2Id)
        )
        
        val comparisonData = ComparisonCalculator.calculateComparison(coin1, coin2)
        
        _state.update {
            it.copy(
                coin1 = coin1,
                coin2 = coin2,
                comparisonData = comparisonData
            )
        }
    }
    
    private fun deleteSavedComparison(comparisonId: Long) {
        viewModelScope.launch {
            comparisonRepository.deleteComparison(comparisonId)
        }
    }
    
    private fun clearComparison() {
        _state.update {
            it.copy(
                coin1 = null,
                coin2 = null,
                comparisonData = null
            )
        }
    }
    
    // Mock data functions (in real app, would fetch from API)
    private fun getMockPrice(coinId: String): Double = when (coinId) {
        "bitcoin" -> 67234.56
        "ethereum" -> 3456.78
        "solana" -> 145.23
        "cardano" -> 0.45
        "dogecoin" -> 0.12
        else -> 100.0
    }
    
    private fun getMockChange(coinId: String): Double = when (coinId) {
        "bitcoin" -> 2.34
        "ethereum" -> -1.23
        "solana" -> 5.67
        "cardano" -> -0.89
        "dogecoin" -> 12.34
        else -> 0.0
    }
    
    private fun getMockMarketCap(coinId: String): Double = when (coinId) {
        "bitcoin" -> 1320000000000.0
        "ethereum" -> 415000000000.0
        "solana" -> 63000000000.0
        "cardano" -> 16000000000.0
        "dogecoin" -> 17000000000.0
        else -> 1000000000.0
    }
    
    private fun getMockVolume(coinId: String): Double = when (coinId) {
        "bitcoin" -> 28000000000.0
        "ethereum" -> 15000000000.0
        "solana" -> 2500000000.0
        "cardano" -> 450000000.0
        "dogecoin" -> 800000000.0
        else -> 100000000.0
    }
    
    private fun getMockRank(coinId: String): Int = when (coinId) {
        "bitcoin" -> 1
        "ethereum" -> 2
        "solana" -> 5
        "cardano" -> 9
        "dogecoin" -> 8
        else -> 100
    }
    
    private fun getMockSupply(coinId: String): Double = when (coinId) {
        "bitcoin" -> 19600000.0
        "ethereum" -> 120000000.0
        "solana" -> 435000000.0
        "cardano" -> 35000000000.0
        "dogecoin" -> 143000000000.0
        else -> 1000000000.0
    }
}
