package com.example.cryptowallet.app.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cryptowallet.app.portfolio.data.local.PortfolioCoinEntity
import com.example.cryptowallet.app.portfolio.data.local.PortfolioDao


@Database(entities = [PortfolioCoinEntity::class], version = 1)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}