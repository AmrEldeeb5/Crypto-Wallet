package com.example.valguard.app.coins.data.remote.impl

import com.example.valguard.app.coins.data.remote.dto.CoinGeckoChartDto
import com.example.valguard.app.coins.data.remote.dto.CoinGeckoDetailDto
import com.example.valguard.app.coins.data.remote.dto.CoinGeckoMarketDto
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://api.coingecko.com/api/v3"

/**
 * CoinGecko API implementation
 * Single source of truth for all cryptocurrency market data
 */
class CoinGeckoRemoteDataSource(
    private val httpClient: HttpClient
) {
    /**
     * Fetch list of coins with market data
     * Endpoint: /coins/markets
     */
    suspend fun getCoinsMarkets(
        vsCurrency: String = "usd",
        perPage: Int = 100,
        page: Int = 1,
        sparkline: Boolean = true
    ): Result<List<CoinGeckoMarketDto>, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coins/markets") {
                parameter("vs_currency", vsCurrency)
                parameter("order", "market_cap_desc")
                parameter("per_page", perPage)
                parameter("page", page)
                parameter("sparkline", sparkline)
            }
        }
    }

    /**
     * Fetch detailed coin information
     * Endpoint: /coins/{id}
     */
    suspend fun getCoinDetail(
        coinId: String,
        localization: Boolean = false,
        tickers: Boolean = false,
        marketData: Boolean = true,
        communityData: Boolean = false,
        developerData: Boolean = false
    ): Result<CoinGeckoDetailDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coins/$coinId") {
                parameter("localization", localization)
                parameter("tickers", tickers)
                parameter("market_data", marketData)
                parameter("community_data", communityData)
                parameter("developer_data", developerData)
            }
        }
    }

    /**
     * Fetch price history for charts
     * Endpoint: /coins/{id}/market_chart
     */
    suspend fun getCoinMarketChart(
        coinId: String,
        vsCurrency: String = "usd",
        days: String = "7"
    ): Result<CoinGeckoChartDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coins/$coinId/market_chart") {
                parameter("vs_currency", vsCurrency)
                parameter("days", days)
            }
        }
    }
}
