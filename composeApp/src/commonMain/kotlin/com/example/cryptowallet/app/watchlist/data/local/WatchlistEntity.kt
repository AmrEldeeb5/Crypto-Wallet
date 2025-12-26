package com.example.cryptowallet.app.watchlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val coinId: String,
    val addedAt: Long = 0L
)
