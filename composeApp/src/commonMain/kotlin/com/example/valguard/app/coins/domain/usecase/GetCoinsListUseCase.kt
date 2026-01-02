package com.example.valguard.app.coins.domain.usecase

import com.example.valguard.app.coins.domain.api.CoinsRemoteDataSource
import com.example.valguard.app.coins.domain.model.CoinModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.domain.map
import com.example.valguard.app.mapper.toCoinModel

class GetCoinsListUseCase(
    private val client: CoinsRemoteDataSource,
) {

    suspend fun execute(): Result<List<CoinModel>, DataError.Remote> {
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map { it.toCoinModel() }
        }
    }
}