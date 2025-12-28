package com.example.cryptovault.app.compare

import com.example.cryptovault.app.compare.data.local.SavedComparisonEntity
import com.example.cryptovault.app.compare.domain.SavedComparison
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class SavedComparisonPersistencePropertyTest {
    
    private val iterations = 100
    private val coinIds = listOf("bitcoin", "ethereum", "solana", "cardano", "dogecoin", "ripple", "polkadot")
    private val coinNames = listOf("Bitcoin", "Ethereum", "Solana", "Cardano", "Dogecoin", "Ripple", "Polkadot")
    private val coinSymbols = listOf("BTC", "ETH", "SOL", "ADA", "DOGE", "XRP", "DOT")
    
    @Test
    fun `SavedComparison to entity and back preserves all fields`() {
        repeat(iterations) {
            val coin1Index = Random.nextInt(coinIds.size)
            var coin2Index = Random.nextInt(coinIds.size)
            while (coin2Index == coin1Index) {
                coin2Index = Random.nextInt(coinIds.size)
            }
            
            val original = SavedComparison(
                id = Random.nextLong(1, 10000),
                coin1Id = coinIds[coin1Index],
                coin1Name = coinNames[coin1Index],
                coin1Symbol = coinSymbols[coin1Index],
                coin1IconUrl = "https://example.com/${coinIds[coin1Index]}.png",
                coin2Id = coinIds[coin2Index],
                coin2Name = coinNames[coin2Index],
                coin2Symbol = coinSymbols[coin2Index],
                coin2IconUrl = "https://example.com/${coinIds[coin2Index]}.png",
                savedAt = Random.nextLong(1000000000000, 2000000000000)
            )
            
            val entity = original.toEntity()
            val restored = SavedComparison.fromEntity(entity)
            
            assertEquals(original.id, restored.id, "ID should be preserved")
            assertEquals(original.coin1Id, restored.coin1Id, "Coin1 ID should be preserved")
            assertEquals(original.coin1Name, restored.coin1Name, "Coin1 name should be preserved")
            assertEquals(original.coin1Symbol, restored.coin1Symbol, "Coin1 symbol should be preserved")
            assertEquals(original.coin1IconUrl, restored.coin1IconUrl, "Coin1 icon URL should be preserved")
            assertEquals(original.coin2Id, restored.coin2Id, "Coin2 ID should be preserved")
            assertEquals(original.coin2Name, restored.coin2Name, "Coin2 name should be preserved")
            assertEquals(original.coin2Symbol, restored.coin2Symbol, "Coin2 symbol should be preserved")
            assertEquals(original.coin2IconUrl, restored.coin2IconUrl, "Coin2 icon URL should be preserved")
            assertEquals(original.savedAt, restored.savedAt, "Saved at should be preserved")
        }
    }
    
    @Test
    fun `entity to SavedComparison and back preserves all fields`() {
        repeat(iterations) {
            val coin1Index = Random.nextInt(coinIds.size)
            var coin2Index = Random.nextInt(coinIds.size)
            while (coin2Index == coin1Index) {
                coin2Index = Random.nextInt(coinIds.size)
            }
            
            val original = SavedComparisonEntity(
                id = Random.nextLong(1, 10000),
                coin1Id = coinIds[coin1Index],
                coin1Name = coinNames[coin1Index],
                coin1Symbol = coinSymbols[coin1Index],
                coin1IconUrl = "https://example.com/${coinIds[coin1Index]}.png",
                coin2Id = coinIds[coin2Index],
                coin2Name = coinNames[coin2Index],
                coin2Symbol = coinSymbols[coin2Index],
                coin2IconUrl = "https://example.com/${coinIds[coin2Index]}.png",
                savedAt = Random.nextLong(1000000000000, 2000000000000)
            )
            
            val comparison = SavedComparison.fromEntity(original)
            val restored = comparison.toEntity()
            
            assertEquals(original.id, restored.id, "ID should be preserved")
            assertEquals(original.coin1Id, restored.coin1Id, "Coin1 ID should be preserved")
            assertEquals(original.coin1Name, restored.coin1Name, "Coin1 name should be preserved")
            assertEquals(original.coin1Symbol, restored.coin1Symbol, "Coin1 symbol should be preserved")
            assertEquals(original.coin1IconUrl, restored.coin1IconUrl, "Coin1 icon URL should be preserved")
            assertEquals(original.coin2Id, restored.coin2Id, "Coin2 ID should be preserved")
            assertEquals(original.coin2Name, restored.coin2Name, "Coin2 name should be preserved")
            assertEquals(original.coin2Symbol, restored.coin2Symbol, "Coin2 symbol should be preserved")
            assertEquals(original.coin2IconUrl, restored.coin2IconUrl, "Coin2 icon URL should be preserved")
            assertEquals(original.savedAt, restored.savedAt, "Saved at should be preserved")
        }
    }
    
    @Test
    fun `default ID is 0 for new comparisons`() {
        val comparison = SavedComparison(
            coin1Id = "bitcoin",
            coin1Name = "Bitcoin",
            coin1Symbol = "BTC",
            coin1IconUrl = "https://example.com/btc.png",
            coin2Id = "ethereum",
            coin2Name = "Ethereum",
            coin2Symbol = "ETH",
            coin2IconUrl = "https://example.com/eth.png",
            savedAt = System.currentTimeMillis()
        )
        
        assertEquals(0L, comparison.id, "Default ID should be 0")
        assertEquals(0L, comparison.toEntity().id, "Entity default ID should be 0")
    }
    
    @Test
    fun `coin order is preserved after round-trip`() {
        repeat(iterations) {
            val comparison = SavedComparison(
                id = Random.nextLong(1, 10000),
                coin1Id = "bitcoin",
                coin1Name = "Bitcoin",
                coin1Symbol = "BTC",
                coin1IconUrl = "https://example.com/btc.png",
                coin2Id = "ethereum",
                coin2Name = "Ethereum",
                coin2Symbol = "ETH",
                coin2IconUrl = "https://example.com/eth.png",
                savedAt = System.currentTimeMillis()
            )
            
            val restored = SavedComparison.fromEntity(comparison.toEntity())
            
            // Coin 1 should still be Bitcoin
            assertEquals("bitcoin", restored.coin1Id)
            assertEquals("Bitcoin", restored.coin1Name)
            assertEquals("BTC", restored.coin1Symbol)
            
            // Coin 2 should still be Ethereum
            assertEquals("ethereum", restored.coin2Id)
            assertEquals("Ethereum", restored.coin2Name)
            assertEquals("ETH", restored.coin2Symbol)
        }
    }
    
    @Test
    fun `icon URLs with special characters are preserved`() {
        val specialUrls = listOf(
            "https://example.com/coin.png?size=64",
            "https://cdn.example.com/icons/btc-icon.svg",
            "https://api.example.com/v1/coins/bitcoin/image",
            "https://example.com/coins/bitcoin%20logo.png"
        )
        
        specialUrls.forEach { url ->
            val comparison = SavedComparison(
                coin1Id = "bitcoin",
                coin1Name = "Bitcoin",
                coin1Symbol = "BTC",
                coin1IconUrl = url,
                coin2Id = "ethereum",
                coin2Name = "Ethereum",
                coin2Symbol = "ETH",
                coin2IconUrl = url,
                savedAt = System.currentTimeMillis()
            )
            
            val restored = SavedComparison.fromEntity(comparison.toEntity())
            assertEquals(url, restored.coin1IconUrl, "Icon URL '$url' should be preserved")
            assertEquals(url, restored.coin2IconUrl, "Icon URL '$url' should be preserved")
        }
    }
    
    @Test
    fun `timestamp precision is preserved`() {
        repeat(iterations) {
            val timestamp = Random.nextLong(1000000000000, 2000000000000)
            
            val comparison = SavedComparison(
                coin1Id = "bitcoin",
                coin1Name = "Bitcoin",
                coin1Symbol = "BTC",
                coin1IconUrl = "https://example.com/btc.png",
                coin2Id = "ethereum",
                coin2Name = "Ethereum",
                coin2Symbol = "ETH",
                coin2IconUrl = "https://example.com/eth.png",
                savedAt = timestamp
            )
            
            val restored = SavedComparison.fromEntity(comparison.toEntity())
            assertEquals(timestamp, restored.savedAt, "Timestamp should be preserved exactly")
        }
    }
    
    @Test
    fun `all coin combinations can be saved`() {
        // Test that any pair of different coins can be saved
        for (i in coinIds.indices) {
            for (j in coinIds.indices) {
                if (i != j) {
                    val comparison = SavedComparison(
                        coin1Id = coinIds[i],
                        coin1Name = coinNames[i],
                        coin1Symbol = coinSymbols[i],
                        coin1IconUrl = "https://example.com/${coinIds[i]}.png",
                        coin2Id = coinIds[j],
                        coin2Name = coinNames[j],
                        coin2Symbol = coinSymbols[j],
                        coin2IconUrl = "https://example.com/${coinIds[j]}.png",
                        savedAt = System.currentTimeMillis()
                    )
                    
                    val restored = SavedComparison.fromEntity(comparison.toEntity())
                    assertEquals(coinIds[i], restored.coin1Id)
                    assertEquals(coinIds[j], restored.coin2Id)
                }
            }
        }
    }
}
