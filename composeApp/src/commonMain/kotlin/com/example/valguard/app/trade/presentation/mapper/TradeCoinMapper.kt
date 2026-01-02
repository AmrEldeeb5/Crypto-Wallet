package com.example.valguard.app.trade.presentation.mapper

import com.example.valguard.app.core.domain.coin.Coin
import com.example.valguard.app.trade.presentation.common.UiTradeCoinItem


fun UiTradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)