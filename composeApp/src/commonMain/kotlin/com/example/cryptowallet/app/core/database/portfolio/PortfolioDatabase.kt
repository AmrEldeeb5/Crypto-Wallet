package com.example.cryptowallet.app.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cryptowallet.app.portfolio.data.local.PortfolioCoinEntity
import com.example.cryptowallet.app.portfolio.data.local.PortfolioDao
import com.example.cryptowallet.app.portfolio.data.local.UserBalanceDao
import com.example.cryptowallet.app.portfolio.data.local.UserBalanceEntity


@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 2)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun UserBalanceDao(): UserBalanceDao
}