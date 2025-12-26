package com.example.cryptowallet.app.watchlist.domain

import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getWatchlist(): Flow<List<String>>
    suspend fun addToWatchlist(coinId: String)
    suspend fun removeFromWatchlist(coinId: String)
    suspend fun isInWatchlist(coinId: String): Boolean
    suspend fun toggleWatchlist(coinId: String)
}
