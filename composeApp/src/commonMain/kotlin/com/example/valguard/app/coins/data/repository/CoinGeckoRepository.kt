package com.example.valguard.app.coins.data.repository

import com.example.valguard.app.coins.data.local.CoinDao
import com.example.valguard.app.coins.data.local.CoinDetailDao
import com.example.valguard.app.coins.data.local.CoinEntity
import com.example.valguard.app.coins.data.mapper.toDetailEntity
import com.example.valguard.app.coins.data.mapper.toEntity
import com.example.valguard.app.coins.data.mapper.toPriceModels
import com.example.valguard.app.coins.data.remote.impl.CoinGeckoRemoteDataSource
import com.example.valguard.app.coins.domain.model.PriceModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

/**
 * Cache-first repository for CoinGecko data
 * 
 * Strategy:
 * 1. UI observes database (Flow)
 * 2. On observe, check staleness
 * 3. If stale (> 5 min), fetch from API and update cache
 * 4. Database emits new data, UI recomposes
 */
class CoinGeckoRepository(
    private val remoteDataSource: CoinGeckoRemoteDataSource,
    private val coinDao: CoinDao,
    private val coinDetailDao: CoinDetailDao
) {
    companion object {
        private const val CACHE_STALENESS_MS = 5 * 60 * 1000L // 5 minutes
    }

    /**
     * Observe all coins from database
     * Triggers refresh if cache is stale
     */
    fun observeCoins(): Flow<List<CoinEntity>> = coinDao.observeAllCoins()

    /**
     * Observe single coin from database
     */
    fun observeCoin(coinId: String): Flow<CoinEntity?> = coinDao.observeCoin(coinId)

    /**
     * Get coin by ID (suspend, not Flow)
     */
    suspend fun getCoin(coinId: String): CoinEntity? = coinDao.getCoin(coinId)

    /**
     * Get multiple coins by IDs
     */
    suspend fun getCoinsByIds(coinIds: List<String>): List<CoinEntity> = coinDao.getCoinsByIds(coinIds)

    /**
     * Check if cache is stale
     */
    suspend fun isCacheStale(): Boolean {
        val oldestTimestamp = coinDao.getOldestTimestamp() ?: return true
        return Clock.System.now().toEpochMilliseconds() - oldestTimestamp > CACHE_STALENESS_MS
    }

    /**
     * Refresh coins from API
     * Returns Result to indicate success/failure
     */
    suspend fun refreshCoins(): Result<Unit, DataError.Remote> {
        return when (val result = remoteDataSource.getCoinsMarkets()) {
            is Result.Success -> {
                val timestamp = Clock.System.now().toEpochMilliseconds()
                val entities = result.data.map { it.toEntity(timestamp) }
                coinDao.insertCoins(entities)
                Result.Success(Unit)
            }
            is Result.Failure -> result
        }
    }

    /**
     * Refresh single coin detail from API
     */
    suspend fun refreshCoinDetail(coinId: String): Result<Unit, DataError.Remote> {
        return when (val result = remoteDataSource.getCoinDetail(coinId)) {
            is Result.Success -> {
                val timestamp = Clock.System.now().toEpochMilliseconds()

                // Update coin entity with detailed market data
                val marketData = result.data.marketData
                val existingCoin = coinDao.getCoin(coinId)
                
                if (existingCoin != null && marketData != null) {
                    val updatedCoin = existingCoin.copy(
                        marketCapRank = result.data.marketCapRank ?: existingCoin.marketCapRank,
                        currentPrice = marketData.currentPrice?.get("usd") ?: existingCoin.currentPrice,
                        marketCap = marketData.marketCap?.get("usd") ?: existingCoin.marketCap,
                        totalVolume = marketData.totalVolume?.get("usd") ?: existingCoin.totalVolume,
                        high24h = marketData.high24h?.get("usd") ?: existingCoin.high24h,
                        low24h = marketData.low24h?.get("usd") ?: existingCoin.low24h,
                        priceChangePercentage24h = marketData.priceChangePercentage24h ?: existingCoin.priceChangePercentage24h,
                        circulatingSupply = marketData.circulatingSupply ?: existingCoin.circulatingSupply,
                        totalSupply = marketData.totalSupply ?: existingCoin.totalSupply,
                        maxSupply = marketData.maxSupply ?: existingCoin.maxSupply,
                        ath = marketData.ath?.get("usd") ?: existingCoin.ath,
                        atl = marketData.atl?.get("usd") ?: existingCoin.atl,
                        lastUpdated = timestamp
                    )
                    coinDao.insertCoin(updatedCoin)
                }
                
                // Save description
                coinDetailDao.insertCoinDetail(result.data.toDetailEntity(timestamp))
                
                Result.Success(Unit)
            }
            is Result.Failure -> result
        }
    }

    /**
     * Get price history for charts
     * No caching for chart data - always fresh
     */
    suspend fun getPriceHistory(coinId: String, days: String): Result<List<PriceModel>, DataError.Remote> {
        return when (val result = remoteDataSource.getCoinMarketChart(coinId, days = days)) {
            is Result.Success -> {
                val priceModels = result.data.prices.toPriceModels()
                Result.Success(priceModels)
            }
            is Result.Failure -> result
        }
    }

    /**
     * Get coin description from cache
     */
    suspend fun getCoinDescription(coinId: String): String? {
        return coinDetailDao.getCoinDetail(coinId)?.description
    }
}
