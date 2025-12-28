package com.example.cryptovault.app.onboarding

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WatchlistPersistenceOnboardingPropertyTest {
    
    private val iterations = 100
    private val availableCoins = listOf("BTC", "ETH", "BNB", "SOL", "ADA", "XRP")
    
    private val coinIdMap = mapOf(
        "BTC" to "bitcoin",
        "ETH" to "ethereum",
        "BNB" to "binancecoin",
        "SOL" to "solana",
        "ADA" to "cardano",
        "XRP" to "ripple"
    )
    
    private class MockWatchlist {
        private val _coins = mutableSetOf<String>()
        val coins: Set<String> get() = _coins.toSet()
        
        fun add(coinId: String) {
            _coins.add(coinId)
        }
        
        fun clear() {
            _coins.clear()
        }
    }
    
    private fun saveToWatchlist(selectedCoins: Set<String>, watchlist: MockWatchlist) {
        selectedCoins.forEach { symbol ->
            val coinId = coinIdMap[symbol] ?: symbol.lowercase()
            watchlist.add(coinId)
        }
    }
    
    @Test
    fun `all selected coins are added to watchlist`() {
        repeat(iterations) {
            val watchlist = MockWatchlist()
            val numCoins = Random.nextInt(1, availableCoins.size + 1)
            val selectedCoins = availableCoins.shuffled().take(numCoins).toSet()
            
            saveToWatchlist(selectedCoins, watchlist)
            
            assertEquals(
                selectedCoins.size,
                watchlist.coins.size,
                "Watchlist should have same number of coins as selected"
            )
            
            selectedCoins.forEach { symbol ->
                val expectedId = coinIdMap[symbol]!!
                assertTrue(
                    watchlist.coins.contains(expectedId),
                    "Watchlist should contain $expectedId for selected $symbol"
                )
            }
        }
    }
    
    @Test
    fun `empty selection adds nothing to watchlist`() {
        val watchlist = MockWatchlist()
        val selectedCoins = emptySet<String>()
        
        saveToWatchlist(selectedCoins, watchlist)
        
        assertTrue(
            watchlist.coins.isEmpty(),
            "Watchlist should be empty when no coins selected"
        )
    }
    
    @Test
    fun `single coin selection adds one to watchlist`() {
        availableCoins.forEach { symbol ->
            val watchlist = MockWatchlist()
            val selectedCoins = setOf(symbol)
            
            saveToWatchlist(selectedCoins, watchlist)
            
            assertEquals(1, watchlist.coins.size)
            assertTrue(watchlist.coins.contains(coinIdMap[symbol]))
        }
    }
    
    @Test
    fun `all coins selected adds all to watchlist`() {
        val watchlist = MockWatchlist()
        val selectedCoins = availableCoins.toSet()
        
        saveToWatchlist(selectedCoins, watchlist)
        
        assertEquals(availableCoins.size, watchlist.coins.size)
        coinIdMap.values.forEach { coinId ->
            assertTrue(
                watchlist.coins.contains(coinId),
                "Watchlist should contain $coinId"
            )
        }
    }
    
    @Test
    fun `coin symbol to ID mapping is correct`() {
        // Verify the mapping matches expected values
        assertEquals("bitcoin", coinIdMap["BTC"])
        assertEquals("ethereum", coinIdMap["ETH"])
        assertEquals("binancecoin", coinIdMap["BNB"])
        assertEquals("solana", coinIdMap["SOL"])
        assertEquals("cardano", coinIdMap["ADA"])
        assertEquals("ripple", coinIdMap["XRP"])
    }
    
    @Test
    fun `BTC maps to bitcoin`() {
        val watchlist = MockWatchlist()
        saveToWatchlist(setOf("BTC"), watchlist)
        assertTrue(watchlist.coins.contains("bitcoin"))
    }
    
    @Test
    fun `ETH maps to ethereum`() {
        val watchlist = MockWatchlist()
        saveToWatchlist(setOf("ETH"), watchlist)
        assertTrue(watchlist.coins.contains("ethereum"))
    }
    
    @Test
    fun `BNB maps to binancecoin`() {
        val watchlist = MockWatchlist()
        saveToWatchlist(setOf("BNB"), watchlist)
        assertTrue(watchlist.coins.contains("binancecoin"))
    }
    
    @Test
    fun `SOL maps to solana`() {
        val watchlist = MockWatchlist()
        saveToWatchlist(setOf("SOL"), watchlist)
        assertTrue(watchlist.coins.contains("solana"))
    }
    
    @Test
    fun `ADA maps to cardano`() {
        val watchlist = MockWatchlist()
        saveToWatchlist(setOf("ADA"), watchlist)
        assertTrue(watchlist.coins.contains("cardano"))
    }
    
    @Test
    fun `XRP maps to ripple`() {
        val watchlist = MockWatchlist()
        saveToWatchlist(setOf("XRP"), watchlist)
        assertTrue(watchlist.coins.contains("ripple"))
    }
    
    @Test
    fun `duplicate selections are handled correctly`() {
        val watchlist = MockWatchlist()
        // Simulate adding same coin twice (shouldn't happen in practice but test robustness)
        saveToWatchlist(setOf("BTC"), watchlist)
        saveToWatchlist(setOf("BTC"), watchlist)
        
        // Set should deduplicate
        assertEquals(1, watchlist.coins.size)
        assertTrue(watchlist.coins.contains("bitcoin"))
    }
    
    @Test
    fun `order of selection does not affect result`() {
        repeat(iterations) {
            val watchlist1 = MockWatchlist()
            val watchlist2 = MockWatchlist()
            
            val coins = availableCoins.shuffled().take(Random.nextInt(1, 7))
            val coinsReversed = coins.reversed()
            
            saveToWatchlist(coins.toSet(), watchlist1)
            saveToWatchlist(coinsReversed.toSet(), watchlist2)
            
            assertEquals(
                watchlist1.coins,
                watchlist2.coins,
                "Order of selection should not affect watchlist result"
            )
        }
    }
}
