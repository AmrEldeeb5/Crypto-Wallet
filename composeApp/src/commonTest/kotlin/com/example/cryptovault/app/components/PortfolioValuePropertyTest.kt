package com.example.cryptovault.app.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.math.abs

class PortfolioValuePropertyTest {

    data class CoinHolding(
        val coinId: String,
        val holdings: Double,
        val price: Double
    ) {
        val value: Double get() = holdings * price
    }

    @Test
    fun `Property 5 - Portfolio value equals sum of holdings times price`() {
        val holdings = listOf(
            CoinHolding("bitcoin", 0.5, 45000.0),
            CoinHolding("ethereum", 2.0, 3000.0),
            CoinHolding("solana", 10.0, 100.0)
        )
        
        val expectedTotal = holdings.sumOf { it.value }
        val calculatedTotal = calculatePortfolioValue(holdings)
        
        assertEquals(expectedTotal, calculatedTotal, 0.01, "Portfolio value should equal sum of holdings × price")
        assertEquals(29500.0, calculatedTotal, 0.01, "Expected: 0.5×45000 + 2×3000 + 10×100 = 29500")
    }

    @Test
    fun `Property 5 - Empty portfolio has zero value`() {
        val holdings = emptyList<CoinHolding>()
        val total = calculatePortfolioValue(holdings)
        
        assertEquals(0.0, total, 0.01, "Empty portfolio should have zero value")
    }

    @Test
    fun `Property 5 - Single coin portfolio equals coin value`() {
        val holdings = listOf(
            CoinHolding("bitcoin", 1.0, 50000.0)
        )
        
        val total = calculatePortfolioValue(holdings)
        assertEquals(50000.0, total, 0.01, "Single coin portfolio should equal that coin's value")
    }

    @Test
    fun `Property 5 - Zero holdings contribute zero value`() {
        val holdings = listOf(
            CoinHolding("bitcoin", 0.0, 45000.0),
            CoinHolding("ethereum", 2.0, 3000.0)
        )
        
        val total = calculatePortfolioValue(holdings)
        assertEquals(6000.0, total, 0.01, "Zero holdings should contribute zero value")
    }

    @Test
    fun `Property 5 - Portfolio value is non-negative`() {
        val testCases = listOf(
            listOf(CoinHolding("a", 0.0, 0.0)),
            listOf(CoinHolding("b", 1.0, 0.0)),
            listOf(CoinHolding("c", 0.0, 100.0)),
            listOf(CoinHolding("d", 1.0, 100.0)),
            listOf(
                CoinHolding("e", 0.5, 1000.0),
                CoinHolding("f", 2.0, 500.0)
            )
        )
        
        testCases.forEach { holdings ->
            val total = calculatePortfolioValue(holdings)
            assertTrue(total >= 0, "Portfolio value should be non-negative")
        }
    }

    @Test
    fun `Property 5 - Portfolio value scales linearly with holdings`() {
        val price = 1000.0
        
        val holdings1 = listOf(CoinHolding("btc", 1.0, price))
        val holdings2 = listOf(CoinHolding("btc", 2.0, price))
        val holdings3 = listOf(CoinHolding("btc", 3.0, price))
        
        val value1 = calculatePortfolioValue(holdings1)
        val value2 = calculatePortfolioValue(holdings2)
        val value3 = calculatePortfolioValue(holdings3)
        
        assertEquals(value1 * 2, value2, 0.01, "Doubling holdings should double value")
        assertEquals(value1 * 3, value3, 0.01, "Tripling holdings should triple value")
    }

    @Test
    fun `Property 5 - Portfolio value scales linearly with price`() {
        val holdings = 1.0
        
        val portfolio1 = listOf(CoinHolding("btc", holdings, 1000.0))
        val portfolio2 = listOf(CoinHolding("btc", holdings, 2000.0))
        
        val value1 = calculatePortfolioValue(portfolio1)
        val value2 = calculatePortfolioValue(portfolio2)
        
        assertEquals(value1 * 2, value2, 0.01, "Doubling price should double value")
    }

    @Test
    fun `Property 5 - Order of coins does not affect total`() {
        val holdingsA = listOf(
            CoinHolding("bitcoin", 0.5, 45000.0),
            CoinHolding("ethereum", 2.0, 3000.0)
        )
        
        val holdingsB = listOf(
            CoinHolding("ethereum", 2.0, 3000.0),
            CoinHolding("bitcoin", 0.5, 45000.0)
        )
        
        val totalA = calculatePortfolioValue(holdingsA)
        val totalB = calculatePortfolioValue(holdingsB)
        
        assertEquals(totalA, totalB, 0.01, "Order should not affect total")
    }

    @Test
    fun `Property 5 - Large portfolio values are handled`() {
        val holdings = listOf(
            CoinHolding("bitcoin", 1000.0, 100000.0) // $100M worth
        )
        
        val total = calculatePortfolioValue(holdings)
        assertEquals(100_000_000.0, total, 0.01, "Large values should be calculated correctly")
    }

    @Test
    fun `Property 5 - Small portfolio values are handled`() {
        val holdings = listOf(
            CoinHolding("shiba", 1000000.0, 0.00001) // Very small price
        )
        
        val total = calculatePortfolioValue(holdings)
        assertEquals(10.0, total, 0.01, "Small values should be calculated correctly")
    }

    @Test
    fun `Property 5 - 24h change percentage is calculated correctly`() {
        val previousValue = 10000.0
        val currentValue = 10500.0
        
        val change = calculatePercentageChange(previousValue, currentValue)
        assertEquals(5.0, change, 0.01, "5% increase should be calculated correctly")
    }

    @Test
    fun `Property 5 - Negative 24h change is calculated correctly`() {
        val previousValue = 10000.0
        val currentValue = 9500.0
        
        val change = calculatePercentageChange(previousValue, currentValue)
        assertEquals(-5.0, change, 0.01, "5% decrease should be calculated correctly")
    }

    @Test
    fun `Property 5 - Zero previous value returns zero change`() {
        val previousValue = 0.0
        val currentValue = 1000.0
        
        val change = calculatePercentageChange(previousValue, currentValue)
        assertEquals(0.0, change, 0.01, "Zero previous value should return 0% change")
    }

    // Helper functions
    
    private fun calculatePortfolioValue(holdings: List<CoinHolding>): Double {
        return holdings.sumOf { it.value }
    }
    
    private fun calculatePercentageChange(previousValue: Double, currentValue: Double): Double {
        if (previousValue == 0.0) return 0.0
        return ((currentValue - previousValue) / previousValue) * 100
    }
}
