package com.example.valguard.app.coins.presentation

import com.example.valguard.app.realtime.domain.PriceDirection

data class UiCoinListItem(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val formattedPrice: String,
    val formattedChange: String,
    val isPositive: Boolean,
    val priceDirection: PriceDirection = PriceDirection.UNCHANGED,
    val holdingsAmount: String? = null,
    val holdingsValue: String? = null,
    val sparklineData: List<Double> = emptyList(),
    val marketCap: String? = null
)
