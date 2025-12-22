package com.example.cryptowallet.app.core.database.portfolio

import androidx.room.RoomDatabaseConstructor

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PortfolioDatabaseCreator : RoomDatabaseConstructor<PortfolioDatabase> {
    actual override fun initialize(): PortfolioDatabase {
        return PortfolioDatabase_Impl()
    }
}
