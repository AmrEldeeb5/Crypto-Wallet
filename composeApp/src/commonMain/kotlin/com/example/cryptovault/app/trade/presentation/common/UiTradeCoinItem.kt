package com.example.cryptovault.app.trade.presentation.common

import com.example.cryptovault.app.realtime.domain.PriceDirection

data class UiTradeCoinItem(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val price: Double,
    val priceDirection: PriceDirection = PriceDirection.UNCHANGED
)