package com.example.valguard.app.coins.domain.usecase

import com.example.valguard.app.coins.data.mapper.toCoinModel
import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.coins.domain.model.CoinModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCoinsListUseCase(
    private val repository: CoinGeckoRepository,
) {
    /**
     * Observe coins from cache, triggers refresh if stale
     */
    fun observe(): Flow<List<CoinModel>> {
        return repository.observeCoins().map { entities ->
            entities.map { it.toCoinModel() }
        }
    }

    /**
     * Check if cache needs refresh and trigger if needed
     */
    suspend fun refreshIfNeeded(): Result<Unit, DataError.Remote> {
        return if (repository.isCacheStale()) {
            repository.refreshCoins()
        } else {
            Result.Success(Unit)
        }
    }

    /**
     * Force refresh from API
     */
    suspend fun forceRefresh(): Result<Unit, DataError.Remote> {
        return repository.refreshCoins()
    }
}