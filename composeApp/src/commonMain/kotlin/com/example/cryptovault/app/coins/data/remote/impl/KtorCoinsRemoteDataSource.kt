package com.example.cryptovault.app.coins.data.remote.impl

import com.example.cryptovault.app.coins.data.remote.dto.CoinDetailsResponseDto
import com.example.cryptovault.app.coins.data.remote.dto.CoinPriceHistoryResponseDto
import com.example.cryptovault.app.coins.data.remote.dto.CoinResponseDto
import com.example.cryptovault.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptovault.app.core.domain.DataError
import com.example.cryptovault.app.core.domain.Result
import com.example.cryptovault.app.core.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "https://api.coinranking.com/v2"

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient
): CoinsRemoteDataSource{

    override suspend fun getListOfCoins(): Result<CoinResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coins")
        }
    }

    override suspend fun getCoinPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId/history")
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId")
        }
    }
}