package com.example.cryptovault.app.trade.presentation.common

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data object Empty : ValidationResult()
    data object Zero : ValidationResult()
    data class InsufficientFunds(val available: Double) : ValidationResult()
}
