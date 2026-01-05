package com.example.valguard.app.portfolio.data


import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.EmptyResult
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.portfolio.data.local.PortfolioCoinEntity
import com.example.valguard.app.portfolio.data.local.PortfolioDao
import com.example.valguard.app.portfolio.data.local.UserBalanceDao
import com.example.valguard.app.portfolio.data.local.UserBalanceEntity
import com.example.valguard.app.portfolio.data.mapper.toPortfolioCoinEntity
import com.example.valguard.app.portfolio.data.mapper.toPortfolioCoinModel
import com.example.valguard.app.portfolio.domain.PortfolioCoinModel
import com.example.valguard.app.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow


class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinGeckoRepository: CoinGeckoRepository,
) : PortfolioRepository {

    override suspend fun initializeBalance() {
        val cashBalance = userBalanceDao.getCashBalance()
        if (cashBalance == null) {
            userBalanceDao.insertBalance(
                UserBalanceEntity(id = "user_balance", cashBalance = 10000.0)
            )
        }
    }

    override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return combine(
            flow { emit(portfolioDao.getAllOwnedCoins()) },
            coinGeckoRepository.observeCoins()
        ) { portfolioCoinsEntities, cachedCoins ->
            try {
                if (portfolioCoinsEntities.isEmpty()) {
                    Result.Success(emptyList())
                } else {
                    // Refresh coins from CoinGecko if stale
                    if (coinGeckoRepository.isCacheStale()) {
                        coinGeckoRepository.refreshCoins()
                    }
                    
                    val portfolioCoins = portfolioCoinsEntities.mapNotNull { entity: PortfolioCoinEntity ->
                        val coin = cachedCoins.find { it.id == entity.coinId }
                        coin?.let {
                            val price = it.currentPrice ?: 0.0
                            entity.toPortfolioCoinModel(price, it.priceChangePercentage24h)
                        }
                    }
                    Result.Success(portfolioCoins)
                }
            } catch (e: Exception) {
                Result.Failure(DataError.Remote.UNKNOWN_ERROR)
            }
        }.catch { e ->
            emit(Result.Failure(DataError.Remote.UNKNOWN_ERROR))
        }
    }

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        // Refresh coin detail from CoinGecko
        coinGeckoRepository.refreshCoinDetail(coinId)
        
        // Get coin from cache
        val coin = coinGeckoRepository.getCoin(coinId)
        val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
        
        return if (coin != null && portfolioCoinEntity != null) {
            val price = coin.currentPrice ?: 0.0
            Result.Success(portfolioCoinEntity.toPortfolioCoinModel(price, coin.priceChangePercentage24h))
        } else if (portfolioCoinEntity == null) {
            Result.Success(null)
        } else {
            Result.Failure(DataError.Remote.UNKNOWN_ERROR)
        }
    }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        return try {
            portfolioDao.insertPortfolioCoin(portfolioCoin.toPortfolioCoinEntity())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Failure(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deleteCoinById(coinId)
    }

    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return combine(
            flow { emit(portfolioDao.getAllOwnedCoins()) },
            coinGeckoRepository.observeCoins()
        ) { portfolioCoinsEntities, cachedCoins ->
            try {
                if (portfolioCoinsEntities.isEmpty()) {
                    Result.Success(0.0)
                } else {
                    // Refresh coins from CoinGecko if stale
                    if (coinGeckoRepository.isCacheStale()) {
                        coinGeckoRepository.refreshCoins()
                    }
                    
                    val totalValue = portfolioCoinsEntities.sumOf { entity: PortfolioCoinEntity ->
                        val coinPrice = cachedCoins.find { it.id == entity.coinId }?.currentPrice ?: 0.0
                        entity.amountOwned * coinPrice
                    }
                    Result.Success(totalValue)
                }
            } catch (e: Exception) {
                Result.Failure(DataError.Remote.UNKNOWN_ERROR)
            }
        }.catch { e ->
            emit(Result.Failure(DataError.Remote.UNKNOWN_ERROR))
        }
    }

    override fun cashBalanceFlow(): Flow<Double> {
        return flow {
            emit(userBalanceDao.getCashBalance() ?: 10000.0)
        }
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
        return combine(
            cashBalanceFlow(),
            calculateTotalPortfolioValue()
        ) { cashBalance, portfolioResult ->
            when (portfolioResult) {
                is Result.Success -> Result.Success(cashBalance + portfolioResult.data)
                is Result.Failure -> Result.Failure(portfolioResult.error)
            }
        }
    }

    override suspend fun updateCashBalance(newBalance: Double) {
        userBalanceDao.updateCashBalance(newBalance)
    }
}
