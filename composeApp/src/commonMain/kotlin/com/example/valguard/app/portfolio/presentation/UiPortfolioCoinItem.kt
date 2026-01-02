package com.example.valguard.app.portfolio.presentation

import com.example.valguard.app.realtime.domain.PriceDirection


data class UiPortfolioCoinItem(
    val id: String,
    val name: String,
    val iconUrl: String,
    val amountInUnitText: String,
    val amountInFiatText: String,
    val performancePercentText: String,
    val isPositive: Boolean,
    val priceDirection: PriceDirection = PriceDirection.UNCHANGED
)