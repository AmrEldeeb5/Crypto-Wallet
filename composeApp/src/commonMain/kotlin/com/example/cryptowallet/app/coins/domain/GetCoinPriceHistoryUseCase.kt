package com.example.cryptowallet.app.coins.domain

import com.example.cryptowallet.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptowallet.app.coins.domain.model.PriceModel
import com.example.cryptowallet.app.core.domain.DataError
import com.example.cryptowallet.app.core.domain.Result
import com.example.cryptowallet.app.core.domain.map
import com.example.cryptowallet.app.mapper.toPriceModel

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getCoinPriceHistory(coinId).map { dto ->
            dto.data.history.map { it.toPriceModel() }
        }
    }
}