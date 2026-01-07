package com.example.valguard.app.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.valguard.app.coins.data.local.CoinDao
import com.example.valguard.app.coins.data.local.CoinDetailDao
import com.example.valguard.app.coins.data.local.CoinDetailEntity
import com.example.valguard.app.coins.data.local.CoinEntity
import com.example.valguard.app.coins.data.local.Converters
import com.example.valguard.app.compare.data.local.SavedComparisonDao
import com.example.valguard.app.compare.data.local.SavedComparisonEntity
import com.example.valguard.app.dca.data.local.DCAExecutionDao
import com.example.valguard.app.dca.data.local.DCAExecutionEntity
import com.example.valguard.app.dca.data.local.DCAScheduleDao
import com.example.valguard.app.dca.data.local.DCAScheduleEntity
import com.example.valguard.app.portfolio.data.local.PortfolioCoinEntity
import com.example.valguard.app.portfolio.data.local.PortfolioDao
import com.example.valguard.app.portfolio.data.local.UserBalanceDao
import com.example.valguard.app.portfolio.data.local.UserBalanceEntity
import com.example.valguard.app.watchlist.data.local.WatchlistDao
import com.example.valguard.app.watchlist.data.local.WatchlistEntity


import com.example.valguard.app.core.database.preferences.PreferenceDao
import com.example.valguard.app.core.database.preferences.PreferenceEntity

@Database(
    entities = [
        PortfolioCoinEntity::class,
        UserBalanceEntity::class,
        WatchlistEntity::class,
        DCAScheduleEntity::class,
        DCAExecutionEntity::class,
        SavedComparisonEntity::class,
        CoinEntity::class,
        CoinDetailEntity::class,
        PreferenceEntity::class
    ],
    version = 7
)
@TypeConverters(Converters::class)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun UserBalanceDao(): UserBalanceDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun dcaScheduleDao(): DCAScheduleDao
    abstract fun dcaExecutionDao(): DCAExecutionDao
    abstract fun savedComparisonDao(): SavedComparisonDao
    abstract fun coinDao(): CoinDao
    abstract fun coinDetailDao(): CoinDetailDao
    abstract fun preferenceDao(): PreferenceDao
}