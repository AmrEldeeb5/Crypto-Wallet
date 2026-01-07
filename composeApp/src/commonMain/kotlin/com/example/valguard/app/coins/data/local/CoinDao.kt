package com.example.valguard.app.coins.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    fun observeAllCoins(): Flow<List<CoinEntity>>
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    fun observeCoin(coinId: String): Flow<CoinEntity?>
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    suspend fun getCoin(coinId: String): CoinEntity?
    
    @Query("SELECT * FROM coins WHERE id IN (:coinIds)")
    suspend fun getCoinsByIds(coinIds: List<String>): List<CoinEntity>
    
    @Query("SELECT lastUpdated FROM coins ORDER BY lastUpdated ASC LIMIT 1")
    suspend fun getOldestTimestamp(): Long?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinEntity)
    
    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()
}

@Dao
interface CoinDetailDao {
    
    @Query("SELECT * FROM coin_details WHERE id = :coinId")
    fun observeCoinDetail(coinId: String): Flow<CoinDetailEntity?>
    
    @Query("SELECT * FROM coin_details WHERE id = :coinId")
    suspend fun getCoinDetail(coinId: String): CoinDetailEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinDetail(detail: CoinDetailEntity)
}
