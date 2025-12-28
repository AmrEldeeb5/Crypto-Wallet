package com.example.cryptovault.app.trade.presentation.mapper

import com.example.cryptovault.app.core.domain.coin.Coin
import com.example.cryptovault.app.trade.presentation.common.UiTradeCoinItem


fun UiTradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)