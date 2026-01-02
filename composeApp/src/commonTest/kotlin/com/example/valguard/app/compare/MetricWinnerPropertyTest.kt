package com.example.valguard.app.compare

import com.example.valguard.app.compare.presentation.ComparisonCalculator
import com.example.valguard.app.compare.presentation.MetricWinner
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetricWinnerPropertyTest {
    
    private val iterations = 100
    
    @Test
    fun `higher value wins when higherIsBetter is true`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(0.0, 1000000.0)
            val value2 = Random.nextDouble(0.0, 1000000.0)
            
            val winner = ComparisonCalculator.determineWinner(value1, value2, higherIsBetter = true)
            
            when {
                value1 > value2 -> assertEquals(MetricWinner.COIN_1, winner, "Coin 1 should win when value1 ($value1) > value2 ($value2)")
                value1 < value2 -> assertEquals(MetricWinner.COIN_2, winner, "Coin 2 should win when value1 ($value1) < value2 ($value2)")
                else -> assertEquals(MetricWinner.TIE, winner, "Should be tie when values are equal")
            }
        }
    }
    
    @Test
    fun `lower value wins when higherIsBetter is false`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(1.0, 100.0)
            val value2 = Random.nextDouble(1.0, 100.0)
            
            val winner = ComparisonCalculator.determineWinner(value1, value2, higherIsBetter = false)
            
            when {
                value1 < value2 -> assertEquals(MetricWinner.COIN_1, winner, "Coin 1 should win when value1 ($value1) < value2 ($value2) for lower-is-better")
                value1 > value2 -> assertEquals(MetricWinner.COIN_2, winner, "Coin 2 should win when value1 ($value1) > value2 ($value2) for lower-is-better")
                else -> assertEquals(MetricWinner.TIE, winner, "Should be tie when values are equal")
            }
        }
    }
    
    @Test
    fun `equal values result in tie`() {
        repeat(iterations) {
            val value = Random.nextDouble(0.0, 1000000.0)
            
            val winnerHigher = ComparisonCalculator.determineWinner(value, value, higherIsBetter = true)
            val winnerLower = ComparisonCalculator.determineWinner(value, value, higherIsBetter = false)
            
            assertEquals(MetricWinner.TIE, winnerHigher, "Equal values should result in tie (higher is better)")
            assertEquals(MetricWinner.TIE, winnerLower, "Equal values should result in tie (lower is better)")
        }
    }
    
    @Test
    fun `zero values are handled correctly`() {
        assertEquals(MetricWinner.TIE, ComparisonCalculator.determineWinner(0.0, 0.0, true))
        assertEquals(MetricWinner.COIN_2, ComparisonCalculator.determineWinner(0.0, 1.0, true))
        assertEquals(MetricWinner.COIN_1, ComparisonCalculator.determineWinner(1.0, 0.0, true))
        assertEquals(MetricWinner.COIN_1, ComparisonCalculator.determineWinner(0.0, 1.0, false))
        assertEquals(MetricWinner.COIN_2, ComparisonCalculator.determineWinner(1.0, 0.0, false))
    }
    
    @Test
    fun `negative values are handled correctly`() {
        // For price change, negative values are valid
        assertEquals(MetricWinner.COIN_1, ComparisonCalculator.determineWinner(-1.0, -5.0, true))
        assertEquals(MetricWinner.COIN_2, ComparisonCalculator.determineWinner(-5.0, -1.0, true))
        assertEquals(MetricWinner.COIN_2, ComparisonCalculator.determineWinner(-1.0, -5.0, false))
        assertEquals(MetricWinner.COIN_1, ComparisonCalculator.determineWinner(-5.0, -1.0, false))
    }
    
    @Test
    fun `winner determination is consistent`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(-100.0, 100.0)
            val value2 = Random.nextDouble(-100.0, 100.0)
            
            val winner1 = ComparisonCalculator.determineWinner(value1, value2, true)
            val winner2 = ComparisonCalculator.determineWinner(value1, value2, true)
            
            assertEquals(winner1, winner2, "Winner determination should be consistent")
        }
    }
    
    @Test
    fun `swapping values swaps winner`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(0.0, 1000.0)
            val value2 = Random.nextDouble(0.0, 1000.0)
            
            if (value1 != value2) {
                val winner1 = ComparisonCalculator.determineWinner(value1, value2, true)
                val winner2 = ComparisonCalculator.determineWinner(value2, value1, true)
                
                val expectedSwapped = when (winner1) {
                    MetricWinner.COIN_1 -> MetricWinner.COIN_2
                    MetricWinner.COIN_2 -> MetricWinner.COIN_1
                    MetricWinner.TIE -> MetricWinner.TIE
                }
                
                assertEquals(expectedSwapped, winner2, "Swapping values should swap winner")
            }
        }
    }
    
    @Test
    fun `price comparison uses higher is better`() {
        // Higher price is considered better (more valuable)
        val winner = ComparisonCalculator.determineWinner(67000.0, 3500.0, higherIsBetter = true)
        assertEquals(MetricWinner.COIN_1, winner, "Higher price should win")
    }
    
    @Test
    fun `rank comparison uses lower is better`() {
        // Lower rank is better (rank 1 is best)
        val winner = ComparisonCalculator.determineWinner(1.0, 5.0, higherIsBetter = false)
        assertEquals(MetricWinner.COIN_1, winner, "Lower rank should win")
    }
    
    @Test
    fun `market cap comparison uses higher is better`() {
        val winner = ComparisonCalculator.determineWinner(1_000_000_000_000.0, 500_000_000_000.0, higherIsBetter = true)
        assertEquals(MetricWinner.COIN_1, winner, "Higher market cap should win")
    }
    
    @Test
    fun `all MetricWinner values are covered`() {
        val winners = mutableSetOf<MetricWinner>()
        
        winners.add(ComparisonCalculator.determineWinner(10.0, 5.0, true))  // COIN_1
        winners.add(ComparisonCalculator.determineWinner(5.0, 10.0, true))  // COIN_2
        winners.add(ComparisonCalculator.determineWinner(5.0, 5.0, true))   // TIE
        
        assertEquals(3, winners.size, "All MetricWinner values should be reachable")
        assertTrue(MetricWinner.COIN_1 in winners)
        assertTrue(MetricWinner.COIN_2 in winners)
        assertTrue(MetricWinner.TIE in winners)
    }
}
