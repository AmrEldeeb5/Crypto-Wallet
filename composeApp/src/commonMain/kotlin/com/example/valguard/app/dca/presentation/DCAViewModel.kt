package com.example.valguard.app.dca.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.coins.data.mapper.toCoinModel
import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.core.util.UiState
import com.example.valguard.app.dca.data.DCARepository
import com.example.valguard.app.dca.domain.DCAFrequency
import com.example.valguard.app.dca.domain.DCASchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DCAViewModel(
    private val dcaRepository: DCARepository,
    private val coinGeckoRepository: CoinGeckoRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DCAState())
    val state: StateFlow<DCAState> = _state.asStateFlow()
    
    init {
        loadSchedules()
        observeCoins()
    }
    
    private fun observeCoins() {
        viewModelScope.launch {
            coinGeckoRepository.observeCoins().collect { entities ->
                val coins = entities.map { entity ->
                    DCASelectableCoin(
                        id = entity.id,
                        name = entity.name,
                        symbol = entity.symbol,
                        iconUrl = entity.image ?: "",
                        price = entity.currentPrice
                    )
                }
                _state.update { it.copy(availableCoins = coins) }
            }
        }
    }
    
    fun onEvent(event: DCAEvent) {
        when (event) {
            is DCAEvent.LoadSchedules -> loadSchedules()
            is DCAEvent.ShowCoinSelector -> showCoinSelector()
            is DCAEvent.HideCoinSelector -> hideCoinSelector()
            is DCAEvent.ShowActionSheet -> showActionSheet(event.schedule)
            is DCAEvent.HideActionSheet -> hideActionSheet()
            is DCAEvent.EditSchedule -> editSchedule(event.schedule)
            is DCAEvent.DeleteSchedule -> deleteSchedule(event.scheduleId)
            is DCAEvent.ToggleScheduleActive -> toggleScheduleActive(event.scheduleId, event.isActive)
            is DCAEvent.SelectCoin -> selectCoin(event.coinId, event.name, event.symbol, event.iconUrl)
            is DCAEvent.UpdateAmount -> updateAmount(event.amount)
            is DCAEvent.UpdateFrequency -> updateFrequency(event.frequency)
            is DCAEvent.UpdateDayOfWeek -> updateDayOfWeek(event.day)
            is DCAEvent.UpdateDayOfMonth -> updateDayOfMonth(event.day)
            is DCAEvent.SaveSchedule -> saveSchedule()
            is DCAEvent.CancelEdit -> hideCreateForm()
            is DCAEvent.Retry -> loadSchedules()
            is DCAEvent.DismissUpgradePrompt -> dismissUpgradePrompt()
            else -> {} // Skip legacy events
        }
    }
    
    private fun loadSchedules() {
        _state.update { it.copy(schedules = UiState.Loading.Initial) }
        
        viewModelScope.launch {
            dcaRepository.getAllSchedules()
                .catch { e ->
                    _state.update { it.copy(schedules = UiState.Error(e.message ?: "Failed to load schedules")) }
                }
                .collect { schedules ->
                    val activeCount = schedules.count { it.isActive }
                    val totalInvested = schedules.sumOf { it.totalInvested }
                    
                    _state.update {
                        it.copy(
                            schedules = if (schedules.isEmpty()) UiState.Empty() else UiState.Success(schedules),
                            activeScheduleCount = activeCount,
                            totalInvested = totalInvested
                        )
                    }
                }
        }
    }
    
    private fun showCoinSelector() {
        _state.update { it.copy(showCoinSelector = true) }
    }
    
    private fun hideCoinSelector() {
        _state.update { it.copy(showCoinSelector = false) }
    }
    
    private fun showActionSheet(schedule: DCASchedule) {
        _state.update {
            it.copy(
                showActionSheet = true,
                selectedScheduleForAction = schedule
            )
        }
    }
    
    private fun hideActionSheet() {
        _state.update {
            it.copy(
                showActionSheet = false,
                selectedScheduleForAction = null
            )
        }
    }
    
    private fun dismissUpgradePrompt() {
        _state.update { it.copy(showUpgradePrompt = false) }
    }
    
    private fun editSchedule(schedule: DCASchedule) {
        _state.update {
            it.copy(
                showActionSheet = false,
                editingSchedule = schedule,
                createFormState = DCACreateFormState(
                    selectedCoinId = schedule.coinId,
                    selectedCoinName = schedule.coinName,
                    selectedCoinSymbol = schedule.coinSymbol,
                    selectedCoinIconUrl = schedule.coinIconUrl,
                    amount = schedule.amount.toString(),
                    frequency = schedule.frequency,
                    dayOfWeek = schedule.dayOfWeek ?: 1,
                    dayOfMonth = schedule.dayOfMonth ?: 1,
                    isValid = true
                )
            )
        }
    }
    
    private fun deleteSchedule(scheduleId: Long) {
        viewModelScope.launch {
            dcaRepository.deleteSchedule(scheduleId)
        }
    }
    
    private fun toggleScheduleActive(scheduleId: Long, isActive: Boolean) {
        viewModelScope.launch {
            dcaRepository.toggleScheduleActive(scheduleId, isActive)
        }
    }
    
    private fun selectCoin(coinId: String, name: String, symbol: String, iconUrl: String) {
        _state.update {
            it.copy(
                createFormState = it.createFormState.copy(
                    selectedCoinId = coinId,
                    selectedCoinName = name,
                    selectedCoinSymbol = symbol,
                    selectedCoinIconUrl = iconUrl
                ).validate()
            )
        }
    }
    
    private fun updateAmount(amount: String) {
        _state.update {
            it.copy(
                createFormState = it.createFormState.copy(amount = amount).validate()
            )
        }
    }
    
    private fun updateFrequency(frequency: DCAFrequency) {
        _state.update {
            it.copy(
                createFormState = it.createFormState.copy(frequency = frequency)
            )
        }
    }
    
    private fun updateDayOfWeek(day: Int) {
        _state.update {
            it.copy(
                createFormState = it.createFormState.copy(dayOfWeek = day)
            )
        }
    }
    
    private fun updateDayOfMonth(day: Int) {
        _state.update {
            it.copy(
                createFormState = it.createFormState.copy(dayOfMonth = day)
            )
        }
    }
    
    private fun saveSchedule() {
        val formState = _state.value.createFormState
        if (!formState.isValid) return
        
        val amount = formState.amount.toDoubleOrNull() ?: return
        val editingSchedule = _state.value.editingSchedule
        
        // Check schedule limit (3 active schedules for free tier)
        if (editingSchedule == null) { // Only check limit for new schedules
            val activeCount = _state.value.activeScheduleCount
            if (activeCount >= 3) {
                // Show upgrade prompt
                _state.update { it.copy(showUpgradePrompt = true) }
                return
            }
        }
        
        viewModelScope.launch {
            if (editingSchedule != null) {
                val updatedSchedule = editingSchedule.copy(
                    coinId = formState.selectedCoinId,
                    coinName = formState.selectedCoinName,
                    coinSymbol = formState.selectedCoinSymbol,
                    coinIconUrl = formState.selectedCoinIconUrl,
                    amount = amount,
                    frequency = formState.frequency,
                    dayOfWeek = if (formState.frequency == DCAFrequency.WEEKLY || formState.frequency == DCAFrequency.BIWEEKLY) formState.dayOfWeek else null,
                    dayOfMonth = if (formState.frequency == DCAFrequency.MONTHLY) formState.dayOfMonth else null
                )
                dcaRepository.updateSchedule(updatedSchedule)
            } else {
                dcaRepository.createSchedule(
                    coinId = formState.selectedCoinId,
                    coinName = formState.selectedCoinName,
                    coinSymbol = formState.selectedCoinSymbol,
                    coinIconUrl = formState.selectedCoinIconUrl,
                    amount = amount,
                    frequency = formState.frequency,
                    dayOfWeek = if (formState.frequency == DCAFrequency.WEEKLY || formState.frequency == DCAFrequency.BIWEEKLY) formState.dayOfWeek else null,
                    dayOfMonth = if (formState.frequency == DCAFrequency.MONTHLY) formState.dayOfMonth else null
                )
            }
            
            hideCreateForm()
        }
    }
    
    private fun hideCreateForm() {
        _state.update {
            it.copy(
                editingSchedule = null,
                createFormState = DCACreateFormState()
            )
        }
    }
}

/**
 * Selectable coin for DCA screen
 */
data class DCASelectableCoin(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val price: Double?
)
