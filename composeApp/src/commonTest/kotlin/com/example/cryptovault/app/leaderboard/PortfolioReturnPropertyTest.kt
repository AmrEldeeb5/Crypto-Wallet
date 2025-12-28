package com.example.cryptovault.app.leaderboard

import com.example.cryptovault.app.leaderboard.presentation.LeaderboardCalculator
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PortfolioReturnPropertyTest {
    
    private val iterations = 100
    private val tolerance = 0.0001
    
    @Test
    fun `return formula is correct`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 100000.0)
            val current = Random.nextDouble(50.0, 200000.0)
            
            val result = LeaderboardCalculator.calculateReturn(current, invested)
            val expected = ((current - invested) / invested) * 100
            
            assertEquals(expected, result, tolerance, "Return should be ((current-invested)/invested)*100")
        }
    }
    
    @Test
    fun `zero invested returns zero`() {
        repeat(iterations) {
            val current = Random.nextDouble(0.0, 100000.0)
            val result = LeaderboardCalculator.calculateReturn(current, 0.0)
            
            assertEquals(0.0, result, tolerance, "Zero invested should return 0%")
        }
    }
    
    @Test
    fun `same value returns zero percent`() {
        repeat(iterations) {
            val value = Random.nextDouble(100.0, 100000.0)
            val result = LeaderboardCalculator.calculateReturn(value, value)
            
            assertEquals(0.0, result, tolerance, "Same current and invested should return 0%")
        }
    }
    
    @Test
    fun `double value returns 100 percent`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 50000.0)
            val result = LeaderboardCalculator.calculateReturn(invested * 2, invested)
            
            assertEquals(100.0, result, tolerance, "Double value should return 100%")
        }
    }
    
    @Test
    fun `half value returns negative 50 percent`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 50000.0)
            val result = LeaderboardCalculator.calculateReturn(invested / 2, invested)
            
            assertEquals(-50.0, result, tolerance, "Half value should return -50%")
        }
    }
    
    @Test
    fun `positive return when current greater than invested`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 50000.0)
            val current = invested + Random.nextDouble(1.0, 50000.0)
            
            val result = LeaderboardCalculator.calculateReturn(current, invested)
            
            assertTrue(result > 0, "Return should be positive when current > invested")
        }
    }
    
    @Test
    fun `negative return when current less than invested`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 50000.0)
            val current = invested - Random.nextDouble(1.0, invested * 0.9)
            
            val result = LeaderboardCalculator.calculateReturn(current, invested)
            
            assertTrue(result < 0, "Return should be negative when current < invested")
        }
    }
    
    @Test
    fun `calculation is deterministic`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 100000.0)
            val current = Random.nextDouble(50.0, 200000.0)
            
            val result1 = LeaderboardCalculator.calculateReturn(current, invested)
            val result2 = LeaderboardCalculator.calculateReturn(current, invested)
            
            assertEquals(result1, result2, tolerance, "Calculation should be deterministic")
        }
    }
    
    @Test
    fun `handles large numbers`() {
        val invested = 1_000_000_000.0  // 1 billion
        val current = 1_500_000_000.0   // 1.5 billion
        
        val result = LeaderboardCalculator.calculateReturn(current, invested)
        
        assertEquals(50.0, result, tolerance, "Should handle large numbers correctly")
    }
    
    @Test
    fun `handles small numbers`() {
        val invested = 0.001
        val current = 0.002
        
        val result = LeaderboardCalculator.calculateReturn(current, invested)
        
        assertEquals(100.0, result, tolerance, "Should handle small numbers correctly")
    }
    
    @Test
    fun `total loss returns negative 100 percent`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 50000.0)
            val result = LeaderboardCalculator.calculateReturn(0.0, invested)
            
            assertEquals(-100.0, result, tolerance, "Total loss should return -100%")
        }
    }
    
    @Test
    fun `10x returns 900 percent`() {
        repeat(iterations) {
            val invested = Random.nextDouble(100.0, 10000.0)
            val result = LeaderboardCalculator.calculateReturn(invested * 10, invested)
            
            assertEquals(900.0, result, tolerance, "10x should return 900%")
        }
    }
}
