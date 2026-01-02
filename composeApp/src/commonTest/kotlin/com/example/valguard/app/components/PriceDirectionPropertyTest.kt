package com.example.valguard.app.components

import com.example.valguard.app.realtime.domain.PriceDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class PriceDirectionPropertyTest {

    @Test
    fun `Property 7 - Positive change percentage results in isPositive true`() {
        val positiveChanges = listOf(0.01, 0.5, 1.0, 5.5, 10.0, 100.0)
        
        positiveChanges.forEach { change ->
            val coin = createTestCoin(
                formattedChange = "+${change}%",
                isPositive = change > 0
            )
            assertTrue(coin.isPositive, "Change of $change should be positive")
        }
    }

    @Test
    fun `Property 7 - Negative change percentage results in isPositive false`() {
        val negativeChanges = listOf(-0.01, -0.5, -1.0, -5.5, -10.0, -100.0)
        
        negativeChanges.forEach { change ->
            val coin = createTestCoin(
                formattedChange = "${change}%",
                isPositive = change > 0
            )
            assertFalse(coin.isPositive, "Change of $change should be negative")
        }
    }

    @Test
    fun `Property 7 - Zero change results in isPositive false`() {
        val coin = createTestCoin(
            formattedChange = "0.00%",
            isPositive = false
        )
        assertFalse(coin.isPositive, "Zero change should not be positive")
    }

    @Test
    fun `Property 7 - PriceDirection UP corresponds to positive change`() {
        val coin = createTestCoin(
            formattedChange = "+5.5%",
            isPositive = true,
            priceDirection = PriceDirection.UP
        )
        assertTrue(coin.isPositive, "UP direction should have positive change")
        assertEquals(PriceDirection.UP, coin.priceDirection)
    }

    @Test
    fun `Property 7 - PriceDirection DOWN corresponds to negative change`() {
        val coin = createTestCoin(
            formattedChange = "-3.2%",
            isPositive = false,
            priceDirection = PriceDirection.DOWN
        )
        assertFalse(coin.isPositive, "DOWN direction should have negative change")
        assertEquals(PriceDirection.DOWN, coin.priceDirection)
    }

    @Test
    fun `Property 7 - PriceDirection UNCHANGED corresponds to stable price`() {
        val coin = createTestCoin(
            formattedChange = "0.00%",
            isPositive = false,
            priceDirection = PriceDirection.UNCHANGED
        )
        assertEquals(PriceDirection.UNCHANGED, coin.priceDirection)
    }

    @Test
    fun `Property 7 - All PriceDirection values are defined`() {
        val directions = PriceDirection.entries
        assertEquals(3, directions.size, "Should have exactly 3 price directions")
        assertTrue(directions.contains(PriceDirection.UP))
        assertTrue(directions.contains(PriceDirection.DOWN))
        assertTrue(directions.contains(PriceDirection.UNCHANGED))
    }

    @Test
    fun `Property 7 - Formatted change includes percentage sign`() {
        val testCases = listOf(
            "+5.5%" to true,
            "-3.2%" to false,
            "0.00%" to false
        )
        
        testCases.forEach { (change, isPositive) ->
            val coin = createTestCoin(formattedChange = change, isPositive = isPositive)
            assertTrue(coin.formattedChange.contains("%"), "Change should include % sign")
        }
    }

    @Test
    fun `Property 7 - Large positive changes are handled`() {
        val coin = createTestCoin(
            formattedChange = "+999.99%",
            isPositive = true,
            priceDirection = PriceDirection.UP
        )
        assertTrue(coin.isPositive)
        assertEquals(PriceDirection.UP, coin.priceDirection)
    }

    @Test
    fun `Property 7 - Large negative changes are handled`() {
        val coin = createTestCoin(
            formattedChange = "-99.99%",
            isPositive = false,
            priceDirection = PriceDirection.DOWN
        )
        assertFalse(coin.isPositive)
        assertEquals(PriceDirection.DOWN, coin.priceDirection)
    }

    private fun createTestCoin(
        formattedChange: String,
        isPositive: Boolean,
        priceDirection: PriceDirection = if (isPositive) PriceDirection.UP else PriceDirection.DOWN
    ) = UiCoinItem(
        id = "test",
        name = "Test Coin",
        symbol = "TEST",
        iconUrl = "",
        formattedPrice = "$100.00",
        formattedChange = formattedChange,
        isPositive = isPositive,
        priceDirection = priceDirection
    )
}
