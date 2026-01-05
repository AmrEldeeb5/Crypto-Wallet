package com.example.valguard.app.coins.data.mapper

import com.example.valguard.app.coins.data.local.CoinDetailEntity
import com.example.valguard.app.coins.data.local.CoinEntity
import com.example.valguard.app.coins.data.remote.dto.CoinGeckoDetailDto
import com.example.valguard.app.coins.data.remote.dto.CoinGeckoMarketDto
import com.example.valguard.app.coins.domain.model.CoinModel
import com.example.valguard.app.coins.domain.model.PriceModel
import com.example.valguard.app.core.domain.coin.Coin
import kotlinx.datetime.Clock

/**
 * Mappers for CoinGecko data flow
 * DTO → Entity → Domain
 * 
 * CRITICAL: No default values invented. Null stays null.
 */

// DTO → Entity mappers

fun CoinGeckoMarketDto.toEntity(timestamp: Long = Clock.System.now().toEpochMilliseconds()): CoinEntity {
    return CoinEntity(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        totalVolume = totalVolume,
        high24h = high24h,
        low24h = low24h,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        atl = atl,
        sparkline7d = sparklineIn7d?.price,
        lastUpdated = timestamp
    )
}

fun CoinGeckoDetailDto.toDetailEntity(timestamp: Long = Clock.System.now().toEpochMilliseconds()): CoinDetailEntity {
    return CoinDetailEntity(
        id = id,
        description = description?.en,
        lastUpdated = timestamp
    )
}

// Entity → Domain mappers

fun CoinEntity.toCoinModel(): CoinModel {
    return CoinModel(
        coin = Coin(
            id = id,
            name = name,
            symbol = symbol,
            iconUrl = image ?: ""
        ),
        price = currentPrice ?: 0.0,
        change = priceChangePercentage24h ?: 0.0,
        sparkline = sparkline7d ?: emptyList(),
        marketCap = marketCap ?: 0.0
    )
}

fun CoinEntity.toCoin(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        iconUrl = image ?: ""
    )
}

// Chart data mapper
fun List<List<Double>>.toPriceModels(): List<PriceModel> {
    return mapNotNull { point ->
        if (point.size >= 2) {
            PriceModel(
                timestamp = point[0].toLong(),
                price = point[1]
            )
        } else null
    }
}
