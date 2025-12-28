package com.example.cryptovault.app.dca.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptovault.app.core.util.UiState
import com.example.cryptovault.app.dca.data.DCARepository
import com.example.cryptovault.app.dca.domain.DCAFrequency
import com.example.cryptovault.app.dca.domain.DCASchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DCAViewModel(
    private val dcaRepository: DCARepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DCAState())
    val state: StateFlow<DCAState> = _state.asStateFlow()
    
    init {
        loadSchedules()
    }
    
    fun onEvent(event: DCAEvent) {
        when (event) {
            is DCAEvent.LoadSchedules -> loadSchedules()
            is DCAEvent.ShowCreateDialog -> showCreateDialog()
            is DCAEvent.HideCreateDialog -> hideCreateDialog()
            is DCAEvent.EditSchedule -> editSchedule(event.schedule)
            is DCAEvent.DeleteSchedule -> deleteSchedule(event.scheduleId)
            is DCAEvent.ToggleScheduleActive -> toggleScheduleActive(event.scheduleId, event.isActive)
            is DCAEvent.SelectCoin -> selectCoin(event.coinId, event.name, event.symbol, event.iconUrl)
            is DCAEvent.UpdateAmount -> updateAmount(event.amount)
            is DCAEvent.UpdateFrequency -> updateFrequency(event.frequency)
            is DCAEvent.UpdateDayOfWeek -> updateDayOfWeek(event.day)
            is DCAEvent.UpdateDayOfMonth -> updateDayOfMonth(event.day)
            is DCAEvent.SaveSchedule -> saveSchedule()
            is DCAEvent.Retry -> loadSchedules()
        }
    }
    
    private fun loadSchedules() {
        _state.update { it.copy(schedules = UiState.Loading) }
        
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
                            schedules = if (schedules.isEmpty()) UiState.Empty else UiState.Success(schedules),
                            activeScheduleCount = activeCount,
                            totalInvested = totalInvested
                        )
                    }
                }
        }
    }
    
    private fun showCreateDialog() {
        _state.update {
            it.copy(
                showCreateDialog = true,
                editingSchedule = null,
                createFormState = DCACreateFormState()
            )
        }
    }
    
    private fun hideCreateDialog() {
        _state.update {
            it.copy(
                showCreateDialog = false,
                editingSchedule = null,
                createFormState = DCACreateFormState()
            )
        }
    }
    
    private fun editSchedule(schedule: DCASchedule) {
        _state.update {
            it.copy(
                showCreateDialog = true,
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
            
            hideCreateDialog()
        }
    }
}
