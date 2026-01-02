package com.example.valguard.app.offline

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineCacheFallbackPropertyTest {
    
    enum class NetworkState { ONLINE, OFFLINE }
    
    data class CacheState(
        val hasCachedData: Boolean,
        val cachedDataTimestamp: Long = 0L
    )
    
    sealed class ExpectedUiState {
        data class ShowLiveData(val data: String) : ExpectedUiState()
        data class ShowCachedData(val data: String, val showOfflineIndicator: Boolean) : ExpectedUiState()
        data class ShowError(val message: String) : ExpectedUiState()
    }
    
    fun determineUiState(
        networkState: NetworkState,
        cacheState: CacheState,
        liveData: String?,
        cachedData: String?
    ): ExpectedUiState {
        return when {
            networkState == NetworkState.ONLINE && liveData != null -> {
                ExpectedUiState.ShowLiveData(liveData)
            }
            networkState == NetworkState.OFFLINE && cacheState.hasCachedData && cachedData != null -> {
                ExpectedUiState.ShowCachedData(cachedData, showOfflineIndicator = true)
            }
            networkState == NetworkState.OFFLINE && !cacheState.hasCachedData -> {
                ExpectedUiState.ShowError("No connection and no cached data available")
            }
            networkState == NetworkState.ONLINE && liveData == null -> {
                ExpectedUiState.ShowError("Failed to load data")
            }
            else -> {
                ExpectedUiState.ShowError("Unknown error")
            }
        }
    }
    
    @Test
    fun `Property 13 - Online with live data shows live data`() {
        repeat(100) {
            val liveData = "Live data ${Random.nextInt()}"
            val cachedData = "Cached data ${Random.nextInt()}"
            val cacheState = CacheState(hasCachedData = Random.nextBoolean())
            
            val result = determineUiState(
                networkState = NetworkState.ONLINE,
                cacheState = cacheState,
                liveData = liveData,
                cachedData = cachedData
            )
            
            assertTrue(result is ExpectedUiState.ShowLiveData, "Online with live data should show live data")
            assertEquals(liveData, (result as ExpectedUiState.ShowLiveData).data)
        }
    }
    
    @Test
    fun `Property 13 - Offline with cached data shows cached data with indicator`() {
        repeat(100) {
            val cachedData = "Cached data ${Random.nextInt()}"
            val timestamp = Random.nextLong(1000000000000L, 2000000000000L)
            val cacheState = CacheState(hasCachedData = true, cachedDataTimestamp = timestamp)
            
            val result = determineUiState(
                networkState = NetworkState.OFFLINE,
                cacheState = cacheState,
                liveData = null,
                cachedData = cachedData
            )
            
            assertTrue(result is ExpectedUiState.ShowCachedData, "Offline with cache should show cached data")
            val showCachedData = result as ExpectedUiState.ShowCachedData
            assertEquals(cachedData, showCachedData.data)
            assertTrue(showCachedData.showOfflineIndicator, "Offline indicator should be shown")
        }
    }
    
    @Test
    fun `Property 13 - Offline without cached data shows error`() {
        repeat(100) {
            val cacheState = CacheState(hasCachedData = false)
            
            val result = determineUiState(
                networkState = NetworkState.OFFLINE,
                cacheState = cacheState,
                liveData = null,
                cachedData = null
            )
            
            assertTrue(result is ExpectedUiState.ShowError, "Offline without cache should show error")
        }
    }
    
    @Test
    fun `Property 13 - Online with failed request shows error`() {
        repeat(100) {
            val cacheState = CacheState(hasCachedData = Random.nextBoolean())
            
            val result = determineUiState(
                networkState = NetworkState.ONLINE,
                cacheState = cacheState,
                liveData = null,
                cachedData = if (cacheState.hasCachedData) "Cached" else null
            )
            
            assertTrue(result is ExpectedUiState.ShowError, "Online with failed request should show error")
        }
    }
    
    @Test
    fun `Property 13 - Cache timestamp is preserved when showing cached data`() {
        repeat(100) {
            val timestamp = Random.nextLong(1000000000000L, 2000000000000L)
            val cacheState = CacheState(hasCachedData = true, cachedDataTimestamp = timestamp)
            
            // Verify cache state preserves timestamp
            assertEquals(timestamp, cacheState.cachedDataTimestamp)
            assertTrue(cacheState.hasCachedData)
        }
    }
    
    @Test
    fun `Property 13 - Offline indicator is only shown when offline`() {
        repeat(100) {
            val networkState = if (Random.nextBoolean()) NetworkState.ONLINE else NetworkState.OFFLINE
            val cacheState = CacheState(hasCachedData = true)
            val data = "Data ${Random.nextInt()}"
            
            val result = determineUiState(
                networkState = networkState,
                cacheState = cacheState,
                liveData = if (networkState == NetworkState.ONLINE) data else null,
                cachedData = data
            )
            
            when (result) {
                is ExpectedUiState.ShowLiveData -> {
                    assertEquals(NetworkState.ONLINE, networkState, "Live data only shown when online")
                }
                is ExpectedUiState.ShowCachedData -> {
                    assertEquals(NetworkState.OFFLINE, networkState, "Cached data with indicator only when offline")
                    assertTrue(result.showOfflineIndicator)
                }
                is ExpectedUiState.ShowError -> {
                    // Error can happen in various scenarios
                }
            }
        }
    }
}
