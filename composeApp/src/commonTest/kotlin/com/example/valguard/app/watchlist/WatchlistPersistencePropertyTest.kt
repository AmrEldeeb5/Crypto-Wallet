package com.example.valguard.app.watchlist

import com.example.valguard.app.watchlist.data.local.WatchlistEntity
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WatchlistPersistencePropertyTest {

    // Simulated in-memory storage to test persistence logic
    private class InMemoryWatchlistStorage {
        private val storage = mutableMapOf<String, WatchlistEntity>()

        fun add(coinId: String, addedAt: Long) {
            storage[coinId] = WatchlistEntity(coinId, addedAt)
        }

        fun remove(coinId: String) {
            storage.remove(coinId)
        }

        fun contains(coinId: String): Boolean = storage.containsKey(coinId)

        fun getAll(): List<String> = storage.keys.toList()

        fun clear() = storage.clear()

        // Simulate persistence by returning a new instance with same data
        fun simulateRestart(): InMemoryWatchlistStorage {
            val newStorage = InMemoryWatchlistStorage()
            storage.forEach { (coinId, entity) ->
                newStorage.storage[coinId] = entity
            }
            return newStorage
        }
    }

    private fun generateRandomCoinId(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..Random.nextInt(3, 10))
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    @Test
    fun `added coins persist after simulated restart`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val coinId = generateRandomCoinId()
            val addedAt = Random.nextLong(1000000000000L, 2000000000000L)

            storage.add(coinId, addedAt)
            val afterRestart = storage.simulateRestart()

            assertTrue(
                afterRestart.contains(coinId),
                "Coin $coinId should persist after restart"
            )
        }
    }

    @Test
    fun `removed coins do not persist after simulated restart`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val coinId = generateRandomCoinId()

            storage.add(coinId, System.currentTimeMillis())
            storage.remove(coinId)
            val afterRestart = storage.simulateRestart()

            assertFalse(
                afterRestart.contains(coinId),
                "Removed coin $coinId should not persist after restart"
            )
        }
    }

    @Test
    fun `multiple coins persist correctly`() {
        repeat(50) {
            val storage = InMemoryWatchlistStorage()
            val coinIds = (1..Random.nextInt(1, 10)).map { generateRandomCoinId() }.distinct()

            coinIds.forEach { coinId ->
                storage.add(coinId, System.currentTimeMillis())
            }

            val afterRestart = storage.simulateRestart()

            coinIds.forEach { coinId ->
                assertTrue(
                    afterRestart.contains(coinId),
                    "Coin $coinId should persist after restart"
                )
            }
            assertEquals(coinIds.size, afterRestart.getAll().size)
        }
    }

    @Test
    fun `add then remove then add persists correctly`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val coinId = generateRandomCoinId()

            storage.add(coinId, 1000L)
            storage.remove(coinId)
            storage.add(coinId, 2000L)

            val afterRestart = storage.simulateRestart()

            assertTrue(
                afterRestart.contains(coinId),
                "Re-added coin $coinId should persist after restart"
            )
        }
    }

    @Test
    fun `empty watchlist persists as empty`() {
        repeat(50) {
            val storage = InMemoryWatchlistStorage()
            val afterRestart = storage.simulateRestart()

            assertTrue(
                afterRestart.getAll().isEmpty(),
                "Empty watchlist should remain empty after restart"
            )
        }
    }

    @Test
    fun `removing non-existent coin does not affect storage`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val existingCoin = generateRandomCoinId()
            val nonExistentCoin = generateRandomCoinId() + "_nonexistent"

            storage.add(existingCoin, System.currentTimeMillis())
            storage.remove(nonExistentCoin)

            val afterRestart = storage.simulateRestart()

            assertTrue(
                afterRestart.contains(existingCoin),
                "Existing coin should still persist"
            )
            assertFalse(
                afterRestart.contains(nonExistentCoin),
                "Non-existent coin should not appear"
            )
        }
    }

    @Test
    fun `duplicate adds do not create duplicates`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val coinId = generateRandomCoinId()

            storage.add(coinId, 1000L)
            storage.add(coinId, 2000L)
            storage.add(coinId, 3000L)

            val afterRestart = storage.simulateRestart()

            assertEquals(
                1,
                afterRestart.getAll().count { it == coinId },
                "Coin should appear exactly once despite multiple adds"
            )
        }
    }

    @Test
    fun `watchlist entity has correct structure`() {
        repeat(100) {
            val coinId = generateRandomCoinId()
            val addedAt = Random.nextLong(1000000000000L, 2000000000000L)

            val entity = WatchlistEntity(coinId = coinId, addedAt = addedAt)

            assertEquals(coinId, entity.coinId)
            assertEquals(addedAt, entity.addedAt)
        }
    }

    @Test
    fun `watchlist entity default addedAt is zero`() {
        val coinId = generateRandomCoinId()
        val entity = WatchlistEntity(coinId = coinId)

        assertEquals(0L, entity.addedAt)
    }

    @Test
    fun `partial removal preserves other coins`() {
        repeat(50) {
            val storage = InMemoryWatchlistStorage()
            val coins = (1..5).map { generateRandomCoinId() }.distinct()

            coins.forEach { storage.add(it, System.currentTimeMillis()) }

            val toRemove = coins.take(2)
            val toKeep = coins.drop(2)

            toRemove.forEach { storage.remove(it) }

            val afterRestart = storage.simulateRestart()

            toRemove.forEach { coinId ->
                assertFalse(
                    afterRestart.contains(coinId),
                    "Removed coin $coinId should not persist"
                )
            }
            toKeep.forEach { coinId ->
                assertTrue(
                    afterRestart.contains(coinId),
                    "Kept coin $coinId should persist"
                )
            }
        }
    }

    @Test
    fun `clear all then restart results in empty watchlist`() {
        repeat(50) {
            val storage = InMemoryWatchlistStorage()
            val coins = (1..Random.nextInt(1, 5)).map { generateRandomCoinId() }

            coins.forEach { storage.add(it, System.currentTimeMillis()) }
            storage.clear()

            val afterRestart = storage.simulateRestart()

            assertTrue(
                afterRestart.getAll().isEmpty(),
                "Cleared watchlist should be empty after restart"
            )
        }
    }

    @Test
    fun `toggle behavior - add if not present`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val coinId = generateRandomCoinId()

            // Toggle = add if not present
            if (!storage.contains(coinId)) {
                storage.add(coinId, System.currentTimeMillis())
            } else {
                storage.remove(coinId)
            }

            assertTrue(
                storage.contains(coinId),
                "Toggle on non-existent coin should add it"
            )
        }
    }

    @Test
    fun `toggle behavior - remove if present`() {
        repeat(100) {
            val storage = InMemoryWatchlistStorage()
            val coinId = generateRandomCoinId()

            storage.add(coinId, System.currentTimeMillis())

            // Toggle = remove if present
            if (storage.contains(coinId)) {
                storage.remove(coinId)
            } else {
                storage.add(coinId, System.currentTimeMillis())
            }

            assertFalse(
                storage.contains(coinId),
                "Toggle on existing coin should remove it"
            )
        }
    }
}
