package com.example.cryptowallet.app.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cryptowallet.app.core.database.portfolio.PortfolioDatabase
import android.content.Context


fun getPortfolioDatabaseBuilder(context: Context): RoomDatabase.Builder<PortfolioDatabase> {
    val dbFile = context.getDatabasePath("portfolio.db")
    return Room.databaseBuilder<PortfolioDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}