package com.example.cryptowallet.app.portfolio.data


import com.example.cryptowallet.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptowallet.app.core.domain.DataError
import com.example.cryptowallet.app.core.domain.Result
import com.example.cryptowallet.app.core.domain.coin.Coin
import com.example.cryptowallet.app.portfolio.data.local.PortfolioDao
import com.example.cryptowallet.app.portfolio.data.local.UserBalanceDao
import com.example.cryptowallet.app.portfolio.data.local.UserBalanceEntity
import com.example.cryptowallet.app.portfolio.data.local.PortfolioCoinEntity
import com.example.cryptowallet.app.portfolio.domain.PortfolioCoinModel
import com.example.cryptowallet.app.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow


class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinsRemoteDataSource: CoinsRemoteDataSource,
) : PortfolioRepository{

    override suspend fun initializeBalance() {
        val cashBalance = userBalanceDao.getCashBalance()
        if (cashBalance == null) {
            userBalanceDao.insertBalance(
                UserBalanceEntity(cashBalance = 10000.0)
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinsEntities ->
            if (portfolioCoinsEntities.isEmpty()) {
                flow {
                    emit(Result.Success(emptyList<PortfolioCoinModel>()))
                }
            } else {
                flow {
                    coinsRemoteDataSource.getListOfCoins()
                        .onError { error ->
                            emit(Result.Error(error))
                        }
                        .onSuccess { coinsDto ->
                            val portfolioCoins = portfolioCoinsEntities.mapNotNull { portfolioCoinsEntity ->
                                val coin = coinsDto.data.coins.find { it.uuid == portfolioCoinsEntity.coinId }
                                coin?.let {
                                    portfolioCoinsEntity.toPortfolioCoinModel(it.price)
                                }
                            }
                            emit(Result.Success(portfolioCoins))
                        }
                }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId)
            .onError { error ->
                return Result.Error(error)
            }
            .onSuccess { coinDto ->
                val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
                return if (portfolioCoinEntity != null) {
                    Result.Success(portfolioCoinEntity.toPortfolioCoinModel(coinDto.data.coin.price))
                } else {
                    Result.Success(null)
                }
            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        try {
            portfolioDao.insert(portfolioCoin.toPortfolioCoinEntity())
            return Result.Success(Unit)
        } catch (e: SQLiteException) {
            return Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deletePortfolioItem(coinId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinsEntities ->
            if (portfolioCoinsEntities.isEmpty()) {
                flow {
                    emit(Result.Success(0.0))
                }
            } else {
                flow {
                    val apiResult = coinsRemoteDataSource.getListOfCoins()
                    apiResult.onError { error ->
                        emit(Result.Error(error))
                    }.onSuccess { coinsDto ->
                        val totalValue = portfolioCoinsEntities.sumOf { ownedCoin ->
                            val coinPrice = coinsDto.data.coins.find { it.uuid == ownedCoin.coinId }?.price ?: 0.0
                            ownedCoin.amountOwned * coinPrice
                        }
                        emit(Result.Success(totalValue))
                    }
                }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
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
                is Result.Success -> {
                    Result.Success(cashBalance + portfolioResult.data)
                }
                is Result.Error -> {
                    Result.Error(portfolioResult.error)
                }
            }
        }
    }

    override suspend fun updateCashBalance(newBalance: Double) {
        userBalanceDao.updateCashBalance(newBalance)
    }
}
}

