package com.example.valguard.app.coins.domain.usecase

import com.example.valguard.app.coins.domain.api.CoinsRemoteDataSource
import com.example.valguard.app.coins.domain.model.CoinModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.domain.map
import com.example.valguard.app.mapper.toCoinModel

class GetCoinDetailsUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<CoinModel, DataError.Remote> {
        return client.getCoinById(coinId).map { dto ->
            dto.data.coin.toCoinModel()  // Single object, not .first()
        }
    }
}