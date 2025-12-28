package com.example.cryptovault.app.portfolio.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert


@Dao
interface PortfolioDao {

    @Upsert
    suspend fun insertPortfolioCoin(portfolioCoinEntity: PortfolioCoinEntity)


    @Query("SELECT * FROM PortfolioCoinEntity ORDER BY timestamp DESC")
    suspend fun getAllOwnedCoins(): List<PortfolioCoinEntity>

    @Query("SELECT * FROM PortfolioCoinEntity WHERE coinId = :coinId")
    suspend fun getCoinById(coinId: String): PortfolioCoinEntity?

    @Query("DELETE FROM PortfolioCoinEntity WHERE coinId = :coinId")
    suspend fun deleteCoinById(coinId: String)

}