package com.example.cryptovault.app.realtime.domain

data class PriceUpdate(
    val coinId: String,
    val price: Double,
    val previousPrice: Double,
    val timestamp: Long
) {

    val priceDirection: PriceDirection
        get() = when {
            price > previousPrice -> PriceDirection.UP
            price < previousPrice -> PriceDirection.DOWN
            else -> PriceDirection.UNCHANGED
        }


    val changePercent: Double
        get() = if (previousPrice != 0.0) {
            ((price - previousPrice) / previousPrice) * 100
        } else {
            0.0
        }
}


enum class PriceDirection {
    UP,
    DOWN,
    UNCHANGED
}
