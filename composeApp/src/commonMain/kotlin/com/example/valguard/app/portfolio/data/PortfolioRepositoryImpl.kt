package com.example.valguard.app.portfolio.data


import com.example.valguard.app.coins.domain.api.CoinsRemoteDataSource
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.EmptyResult
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.domain.onError
import com.example.valguard.app.core.domain.onSuccess
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
    private val coinsRemoteDataSource: CoinsRemoteDataSource,
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
        return flow {
            val portfolioCoinsEntities = portfolioDao.getAllOwnedCoins()
            if (portfolioCoinsEntities.isEmpty()) {
                emit(Result.Success(emptyList()))
            } else {
                coinsRemoteDataSource.getListOfCoins()
                    .onError { error ->
                        emit(Result.Failure(error))
                    }
                    .onSuccess { coinsDto ->
                        val portfolioCoins = portfolioCoinsEntities.mapNotNull { entity: PortfolioCoinEntity ->
                            val coin = coinsDto.data.coins.find { it.uuid == entity.coinId }
                            coin?.let {
                                val price = it.price?.toDoubleOrNull() ?: 0.0
                                entity.toPortfolioCoinModel(price)
                            }
                        }
                        emit(Result.Success(portfolioCoins))
                    }
            }
        }.catch {
            emit(Result.Failure(DataError.Remote.UNKNOWN_ERROR))
        }
    }

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId)
            .onError { error ->
                return Result.Failure(error)
            }
            .onSuccess { coinDto ->
                val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
                val coinDetails = coinDto.data.coin
                val price = coinDetails.price.toDoubleOrNull() ?: 0.0
                return if (portfolioCoinEntity != null) {
                    Result.Success(portfolioCoinEntity.toPortfolioCoinModel(price))
                } else {
                    Result.Success(null)
                }
            }
        return Result.Failure(DataError.Remote.UNKNOWN_ERROR)
    }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        return try {
            portfolioDao.insertPortfolioCoin(portfolioCoin.toPortfolioCoinEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deleteCoinById(coinId)
    }

    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return flow {
            val portfolioCoinsEntities = portfolioDao.getAllOwnedCoins()
            if (portfolioCoinsEntities.isEmpty()) {
                emit(Result.Success(0.0))
            } else {
                coinsRemoteDataSource.getListOfCoins()
                    .onError { error ->
                        emit(Result.Failure(error))
                    }
                    .onSuccess { coinsDto ->
                        val totalValue = portfolioCoinsEntities.sumOf { entity: PortfolioCoinEntity ->
                            val coinPrice = coinsDto.data.coins.find { it.uuid == entity.coinId }?.price?.toDoubleOrNull() ?: 0.0
                            entity.amountOwned * coinPrice
                        }
                        emit(Result.Success(totalValue))
                    }
            }
        }.catch {
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
