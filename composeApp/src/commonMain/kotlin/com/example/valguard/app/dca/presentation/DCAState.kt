package com.example.valguard.app.dca.presentation

import com.example.valguard.app.core.util.UiState
import com.example.valguard.app.dca.domain.DCAFrequency
import com.example.valguard.app.dca.domain.DCASchedule

data class DCAState(
    val schedules: UiState<List<DCASchedule>> = UiState.Loading,
    val totalInvested: Double = 0.0,
    val activeScheduleCount: Int = 0,
    val showCoinSelector: Boolean = false,
    val showActionSheet: Boolean = false,
    val selectedScheduleForAction: DCASchedule? = null,
    val editingSchedule: DCASchedule? = null,
    val createFormState: DCACreateFormState = DCACreateFormState(),
    val creationStatus: CreationStatus = CreationStatus.Idle,
    val showUpgradePrompt: Boolean = false
)

sealed class CreationStatus {
    data object Idle : CreationStatus()
    data object Validating : CreationStatus()
    data object Submitting : CreationStatus()
    data object Success : CreationStatus()
    data class Error(val message: String) : CreationStatus()
}

data class DCACreateFormState(
    val selectedCoinId: String = "",
    val selectedCoinName: String = "",
    val selectedCoinSymbol: String = "",
    val selectedCoinIconUrl: String = "",
    val amount: String = "",
    val frequency: DCAFrequency = DCAFrequency.WEEKLY,
    val dayOfWeek: Int = 1, // Monday
    val dayOfMonth: Int = 1,
    val isValid: Boolean = false,
    val amountError: String? = null
) {
    fun validate(): DCACreateFormState {
        val amountValue = amount.toDoubleOrNull()
        val newAmountError = when {
            amount.isEmpty() -> "Amount is required"
            amountValue == null -> "Invalid amount"
            amountValue <= 0 -> "Amount must be positive"
            else -> null
        }
        val coinSelected = selectedCoinId.isNotEmpty()
        
        return copy(
            amountError = newAmountError,
            isValid = newAmountError == null && coinSelected
        )
    }
}

sealed class DCAEvent {
    data object LoadSchedules : DCAEvent()
    data object ShowCoinSelector : DCAEvent()
    data object HideCoinSelector : DCAEvent()
    data class ShowActionSheet(val schedule: DCASchedule) : DCAEvent()
    data object HideActionSheet : DCAEvent()
    data class EditSchedule(val schedule: DCASchedule) : DCAEvent()
    data class DeleteSchedule(val scheduleId: Long) : DCAEvent()
    data class ToggleScheduleActive(val scheduleId: Long, val isActive: Boolean) : DCAEvent()
    data class SelectCoin(val coinId: String, val name: String, val symbol: String, val iconUrl: String) : DCAEvent()
    data class UpdateAmount(val amount: String) : DCAEvent()
    data class UpdateFrequency(val frequency: DCAFrequency) : DCAEvent()
    data class UpdateDayOfWeek(val day: Int) : DCAEvent()
    data class UpdateDayOfMonth(val day: Int) : DCAEvent()
    data object SaveSchedule : DCAEvent()
    data object CancelEdit : DCAEvent()
    data object Retry : DCAEvent()
    data object DismissUpgradePrompt : DCAEvent()
    
}
