package com.example.valguard.app.coins.domain.usecase

import com.example.valguard.app.coins.data.mapper.toCoinModel
import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.coins.domain.model.CoinModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCoinDetailsUseCase(
    private val repository: CoinGeckoRepository,
) {
    /**
     * Observe single coin from cache
     */
    fun observe(coinId: String): Flow<CoinModel?> {
        return repository.observeCoin(coinId).map { entity ->
            entity?.toCoinModel()
        }
    }

    /**
     * Get coin details (suspend)
     */
    suspend fun execute(coinId: String): Result<CoinModel, DataError.Remote> {
        // First try cache
        val cached = repository.getCoin(coinId)
        if (cached != null) {
            return Result.Success(cached.toCoinModel())
        }
        
        // If not in cache, refresh and try again
        return when (val refreshResult = repository.refreshCoins()) {
            is Result.Success -> {
                val coin = repository.getCoin(coinId)
                if (coin != null) {
                    Result.Success(coin.toCoinModel())
                } else {
                    Result.Failure(DataError.Remote.UNKNOWN_ERROR)
                }
            }
            is Result.Failure -> refreshResult
        }
    }

    /**
     * Refresh coin detail (description, etc.)
     */
    suspend fun refreshDetail(coinId: String): Result<Unit, DataError.Remote> {
        return repository.refreshCoinDetail(coinId)
    }

    /**
     * Get coin description
     */
    suspend fun getDescription(coinId: String): String? {
        return repository.getCoinDescription(coinId)
    }
}