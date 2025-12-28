package com.example.cryptovault.app.dca

import com.example.cryptovault.app.dca.data.local.DCAScheduleEntity
import com.example.cryptovault.app.dca.domain.DCAFrequency
import com.example.cryptovault.app.dca.domain.DCASchedule
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DCASchedulePersistencePropertyTest {
    
    private val iterations = 100
    private val coinIds = listOf("bitcoin", "ethereum", "solana", "cardano", "dogecoin")
    private val coinNames = listOf("Bitcoin", "Ethereum", "Solana", "Cardano", "Dogecoin")
    private val coinSymbols = listOf("BTC", "ETH", "SOL", "ADA", "DOGE")
    
    @Test
    fun `DCASchedule to entity and back preserves all fields`() {
        repeat(iterations) {
            val coinIndex = Random.nextInt(coinIds.size)
            val original = DCASchedule(
                id = Random.nextLong(1, 10000),
                coinId = coinIds[coinIndex],
                coinName = coinNames[coinIndex],
                coinSymbol = coinSymbols[coinIndex],
                coinIconUrl = "https://example.com/${coinIds[coinIndex]}.png",
                amount = Random.nextDouble(1.0, 10000.0),
                frequency = DCAFrequency.entries[Random.nextInt(DCAFrequency.entries.size)],
                dayOfWeek = if (Random.nextBoolean()) Random.nextInt(1, 8) else null,
                dayOfMonth = if (Random.nextBoolean()) Random.nextInt(1, 29) else null,
                isActive = Random.nextBoolean(),
                createdAt = Random.nextLong(1000000000000, 2000000000000),
                nextExecutionDate = Random.nextLong(1000000000000, 2000000000000),
                totalInvested = Random.nextDouble(0.0, 100000.0),
                executionCount = Random.nextInt(0, 1000)
            )
            
            val entity = original.toEntity()
            val restored = DCASchedule.fromEntity(entity)
            
            assertEquals(original.id, restored.id, "ID should be preserved")
            assertEquals(original.coinId, restored.coinId, "Coin ID should be preserved")
            assertEquals(original.coinName, restored.coinName, "Coin name should be preserved")
            assertEquals(original.coinSymbol, restored.coinSymbol, "Coin symbol should be preserved")
            assertEquals(original.coinIconUrl, restored.coinIconUrl, "Icon URL should be preserved")
            assertEquals(original.amount, restored.amount, 0.0001, "Amount should be preserved")
            assertEquals(original.frequency, restored.frequency, "Frequency should be preserved")
            assertEquals(original.dayOfWeek, restored.dayOfWeek, "Day of week should be preserved")
            assertEquals(original.dayOfMonth, restored.dayOfMonth, "Day of month should be preserved")
            assertEquals(original.isActive, restored.isActive, "Active state should be preserved")
            assertEquals(original.createdAt, restored.createdAt, "Created at should be preserved")
            assertEquals(original.nextExecutionDate, restored.nextExecutionDate, "Next execution date should be preserved")
            assertEquals(original.totalInvested, restored.totalInvested, 0.0001, "Total invested should be preserved")
            assertEquals(original.executionCount, restored.executionCount, "Execution count should be preserved")
        }
    }
    
    @Test
    fun `entity to DCASchedule and back preserves all fields`() {
        repeat(iterations) {
            val coinIndex = Random.nextInt(coinIds.size)
            val original = DCAScheduleEntity(
                id = Random.nextLong(1, 10000),
                coinId = coinIds[coinIndex],
                coinName = coinNames[coinIndex],
                coinSymbol = coinSymbols[coinIndex],
                coinIconUrl = "https://example.com/${coinIds[coinIndex]}.png",
                amount = Random.nextDouble(1.0, 10000.0),
                frequency = DCAFrequency.entries[Random.nextInt(DCAFrequency.entries.size)].value,
                dayOfWeek = if (Random.nextBoolean()) Random.nextInt(1, 8) else null,
                dayOfMonth = if (Random.nextBoolean()) Random.nextInt(1, 29) else null,
                isActive = Random.nextBoolean(),
                createdAt = Random.nextLong(1000000000000, 2000000000000),
                nextExecutionDate = Random.nextLong(1000000000000, 2000000000000),
                totalInvested = Random.nextDouble(0.0, 100000.0),
                executionCount = Random.nextInt(0, 1000)
            )
            
            val schedule = DCASchedule.fromEntity(original)
            val restored = schedule.toEntity()
            
            assertEquals(original.id, restored.id, "ID should be preserved")
            assertEquals(original.coinId, restored.coinId, "Coin ID should be preserved")
            assertEquals(original.coinName, restored.coinName, "Coin name should be preserved")
            assertEquals(original.coinSymbol, restored.coinSymbol, "Coin symbol should be preserved")
            assertEquals(original.coinIconUrl, restored.coinIconUrl, "Icon URL should be preserved")
            assertEquals(original.amount, restored.amount, 0.0001, "Amount should be preserved")
            assertEquals(original.frequency, restored.frequency, "Frequency should be preserved")
            assertEquals(original.dayOfWeek, restored.dayOfWeek, "Day of week should be preserved")
            assertEquals(original.dayOfMonth, restored.dayOfMonth, "Day of month should be preserved")
            assertEquals(original.isActive, restored.isActive, "Active state should be preserved")
            assertEquals(original.createdAt, restored.createdAt, "Created at should be preserved")
            assertEquals(original.nextExecutionDate, restored.nextExecutionDate, "Next execution date should be preserved")
            assertEquals(original.totalInvested, restored.totalInvested, 0.0001, "Total invested should be preserved")
            assertEquals(original.executionCount, restored.executionCount, "Execution count should be preserved")
        }
    }
    
    @Test
    fun `all frequency values can be converted to entity and back`() {
        DCAFrequency.entries.forEach { frequency ->
            val schedule = DCASchedule(
                coinId = "bitcoin",
                coinName = "Bitcoin",
                coinSymbol = "BTC",
                coinIconUrl = "https://example.com/btc.png",
                amount = 100.0,
                frequency = frequency,
                createdAt = System.currentTimeMillis(),
                nextExecutionDate = System.currentTimeMillis() + 86400000
            )
            
            val entity = schedule.toEntity()
            val restored = DCASchedule.fromEntity(entity)
            
            assertEquals(frequency, restored.frequency, "Frequency $frequency should be preserved")
        }
    }
    
    @Test
    fun `DCAFrequency fromValue handles all valid values`() {
        assertEquals(DCAFrequency.DAILY, DCAFrequency.fromValue("daily"))
        assertEquals(DCAFrequency.WEEKLY, DCAFrequency.fromValue("weekly"))
        assertEquals(DCAFrequency.BIWEEKLY, DCAFrequency.fromValue("biweekly"))
        assertEquals(DCAFrequency.MONTHLY, DCAFrequency.fromValue("monthly"))
    }
    
    @Test
    fun `DCAFrequency fromValue returns WEEKLY for unknown values`() {
        assertEquals(DCAFrequency.WEEKLY, DCAFrequency.fromValue("unknown"))
        assertEquals(DCAFrequency.WEEKLY, DCAFrequency.fromValue(""))
        assertEquals(DCAFrequency.WEEKLY, DCAFrequency.fromValue("invalid"))
    }
    
    @Test
    fun `amount is always positive after round-trip`() {
        repeat(iterations) {
            val positiveAmount = Random.nextDouble(0.01, 100000.0)
            val schedule = DCASchedule(
                coinId = "bitcoin",
                coinName = "Bitcoin",
                coinSymbol = "BTC",
                coinIconUrl = "https://example.com/btc.png",
                amount = positiveAmount,
                frequency = DCAFrequency.WEEKLY,
                createdAt = System.currentTimeMillis(),
                nextExecutionDate = System.currentTimeMillis() + 86400000
            )
            
            val restored = DCASchedule.fromEntity(schedule.toEntity())
            assertTrue(restored.amount > 0, "Amount should be positive")
        }
    }
    
    @Test
    fun `dayOfWeek is preserved correctly for weekly and biweekly`() {
        listOf(DCAFrequency.WEEKLY, DCAFrequency.BIWEEKLY).forEach { frequency ->
            (1..7).forEach { day ->
                val schedule = DCASchedule(
                    coinId = "bitcoin",
                    coinName = "Bitcoin",
                    coinSymbol = "BTC",
                    coinIconUrl = "https://example.com/btc.png",
                    amount = 100.0,
                    frequency = frequency,
                    dayOfWeek = day,
                    createdAt = System.currentTimeMillis(),
                    nextExecutionDate = System.currentTimeMillis() + 86400000
                )
                
                val restored = DCASchedule.fromEntity(schedule.toEntity())
                assertEquals(day, restored.dayOfWeek, "Day of week $day should be preserved for $frequency")
            }
        }
    }
    
    @Test
    fun `dayOfMonth is preserved correctly for monthly`() {
        (1..28).forEach { day ->
            val schedule = DCASchedule(
                coinId = "bitcoin",
                coinName = "Bitcoin",
                coinSymbol = "BTC",
                coinIconUrl = "https://example.com/btc.png",
                amount = 100.0,
                frequency = DCAFrequency.MONTHLY,
                dayOfMonth = day,
                createdAt = System.currentTimeMillis(),
                nextExecutionDate = System.currentTimeMillis() + 86400000
            )
            
            val restored = DCASchedule.fromEntity(schedule.toEntity())
            assertEquals(day, restored.dayOfMonth, "Day of month $day should be preserved")
        }
    }
    
    @Test
    fun `default values are applied correctly`() {
        val entity = DCAScheduleEntity(
            coinId = "bitcoin",
            coinName = "Bitcoin",
            coinSymbol = "BTC",
            coinIconUrl = "https://example.com/btc.png",
            amount = 100.0,
            frequency = "weekly",
            createdAt = System.currentTimeMillis(),
            nextExecutionDate = System.currentTimeMillis() + 86400000
        )
        
        assertEquals(0L, entity.id, "Default ID should be 0")
        assertEquals(null, entity.dayOfWeek, "Default dayOfWeek should be null")
        assertEquals(null, entity.dayOfMonth, "Default dayOfMonth should be null")
        assertEquals(true, entity.isActive, "Default isActive should be true")
        assertEquals(0.0, entity.totalInvested, 0.0001, "Default totalInvested should be 0")
        assertEquals(0, entity.executionCount, "Default executionCount should be 0")
    }
}
