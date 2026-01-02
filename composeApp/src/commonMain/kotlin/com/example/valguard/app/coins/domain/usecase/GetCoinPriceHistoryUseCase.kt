package com.example.valguard.app.coins.domain.usecase

import com.example.valguard.app.coins.domain.api.CoinsRemoteDataSource
import com.example.valguard.app.coins.domain.model.PriceModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.domain.map
import com.example.valguard.app.mapper.toPriceModel

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getCoinPriceHistory(coinId).map { dto ->
            dto.data.history.map { it.toPriceModel() }
        }
    }
}