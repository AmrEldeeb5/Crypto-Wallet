package com.example.cryptovault.app.coins.domain.usecase

import com.example.cryptovault.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptovault.app.coins.domain.model.PriceModel
import com.example.cryptovault.app.core.domain.DataError
import com.example.cryptovault.app.core.domain.Result
import com.example.cryptovault.app.core.domain.map
import com.example.cryptovault.app.mapper.toPriceModel

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getCoinPriceHistory(coinId).map { dto ->
            dto.data.history.map { it.toPriceModel() }
        }
    }
}