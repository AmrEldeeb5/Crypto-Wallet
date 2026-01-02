package com.example.valguard.app.watchlist.data

import com.example.valguard.app.watchlist.data.local.WatchlistDao
import com.example.valguard.app.watchlist.data.local.WatchlistEntity
import com.example.valguard.app.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class WatchlistRepositoryImpl(
    private val watchlistDao: WatchlistDao
) : WatchlistRepository {

    override fun getWatchlist(): Flow<List<String>> {
        return watchlistDao.getWatchlistCoinIds()
    }

    override suspend fun addToWatchlist(coinId: String) {
        val entity = WatchlistEntity(
            coinId = coinId,
            addedAt = Clock.System.now().toEpochMilliseconds()
        )
        watchlistDao.addToWatchlist(entity)
    }

    override suspend fun removeFromWatchlist(coinId: String) {
        watchlistDao.removeFromWatchlist(coinId)
    }

    override suspend fun isInWatchlist(coinId: String): Boolean {
        return watchlistDao.isInWatchlist(coinId)
    }

    override suspend fun toggleWatchlist(coinId: String) {
        if (isInWatchlist(coinId)) {
            removeFromWatchlist(coinId)
        } else {
            addToWatchlist(coinId)
        }
    }
}
