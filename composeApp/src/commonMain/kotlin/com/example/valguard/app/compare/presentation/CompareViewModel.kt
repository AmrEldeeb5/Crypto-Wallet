package com.example.valguard.app.compare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
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
    private val comparisonRepository: ComparisonRepository,
    private val coinGeckoRepository: CoinGeckoRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CompareState())
    val state: StateFlow<CompareState> = _state.asStateFlow()
    
    init {
        loadSavedComparisons()
        loadAvailableCoins()
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
    
    private fun loadAvailableCoins() {
        viewModelScope.launch {
            coinGeckoRepository.observeCoins().collect { coinEntities ->
                val coinOptions = coinEntities.map { entity ->
                    CoinOption(
                        id = entity.id,
                        name = entity.name,
                        symbol = entity.symbol,
                        iconUrl = entity.image ?: ""
                    )
                }
                _state.update { it.copy(availableCoins = coinOptions) }
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
        
        viewModelScope.launch {
            // Fetch real data from CoinGecko cache
            val coinEntity = coinGeckoRepository.getCoin(coinId)
            
            val coinSlot = CoinSlot(
                id = coinId,
                name = name,
                symbol = symbol,
                iconUrl = iconUrl,
                // Real data from API - null if not available
                price = coinEntity?.currentPrice,
                change24h = coinEntity?.priceChangePercentage24h,
                marketCap = coinEntity?.marketCap,
                volume24h = coinEntity?.totalVolume,
                marketCapRank = coinEntity?.marketCapRank,
                circulatingSupply = coinEntity?.circulatingSupply
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
        
        // Check for duplicates
        val currentSaved = (_state.value.savedComparisons as? UiState.Success)?.data ?: emptyList()
        val isDuplicate = currentSaved.any { saved ->
            (saved.coin1Id == coin1.id && saved.coin2Id == coin2.id) ||
            (saved.coin1Id == coin2.id && saved.coin2Id == coin1.id)
        }

        if (isDuplicate) {
            _state.update { 
                it.copy(
                    snackbarMessage = "Comparison already saved",
                    showSnackbar = true
                )
            }
            return
        }
        
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
                    snackbarMessage = "Comparison saved",
                    showSnackbar = true
                )
            }
        }
    }
    
    private fun loadSavedComparison(comparison: SavedComparison) {
        viewModelScope.launch {
            // Fetch real data for both coins
            val coin1Entity = coinGeckoRepository.getCoin(comparison.coin1Id)
            val coin2Entity = coinGeckoRepository.getCoin(comparison.coin2Id)
            
            val coin1 = CoinSlot(
                id = comparison.coin1Id,
                name = comparison.coin1Name,
                symbol = comparison.coin1Symbol,
                iconUrl = comparison.coin1IconUrl,
                price = coin1Entity?.currentPrice,
                change24h = coin1Entity?.priceChangePercentage24h,
                marketCap = coin1Entity?.marketCap,
                volume24h = coin1Entity?.totalVolume,
                marketCapRank = coin1Entity?.marketCapRank,
                circulatingSupply = coin1Entity?.circulatingSupply
            )
            
            val coin2 = CoinSlot(
                id = comparison.coin2Id,
                name = comparison.coin2Name,
                symbol = comparison.coin2Symbol,
                iconUrl = comparison.coin2IconUrl,
                price = coin2Entity?.currentPrice,
                change24h = coin2Entity?.priceChangePercentage24h,
                marketCap = coin2Entity?.marketCap,
                volume24h = coin2Entity?.totalVolume,
                marketCapRank = coin2Entity?.marketCapRank,
                circulatingSupply = coin2Entity?.circulatingSupply
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
}
