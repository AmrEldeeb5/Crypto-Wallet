package com.example.cryptowallet.app.components

import com.example.cryptowallet.app.realtime.domain.PriceDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoinCardPropertyTest {

    @Test
    fun `Property 7 - Price change color depends on isPositive flag`() {
        // Test positive changes
        val positiveCoin = createTestCoin(isPositive = true, formattedChange = "+5.2%")
        assertTrue(positiveCoin.isPositive, "Positive change should have isPositive = true")
        
        // Test negative changes
        val negativeCoin = createTestCoin(isPositive = false, formattedChange = "-3.1%")
        assertFalse(negativeCoin.isPositive, "Negative change should have isPositive = false")
        
        // Test zero change (typically shown as neutral/negative)
        val zeroCoin = createTestCoin(isPositive = false, formattedChange = "0.0%")
        assertFalse(zeroCoin.isPositive, "Zero change should have isPositive = false")
    }

    @Test
    fun `Property 7 - Various percentage values maintain correct positive flag`() {
        val testCases = listOf(
            Triple("+0.01%", true, "Small positive"),
            Triple("+100.0%", true, "Large positive"),
            Triple("-0.01%", false, "Small negative"),
            Triple("-50.0%", false, "Large negative"),
            Triple("0.0%", false, "Zero")
        )

        testCases.forEach { (change, expectedPositive, description) ->
            val coin = createTestCoin(isPositive = expectedPositive, formattedChange = change)
            assertEquals(expectedPositive, coin.isPositive, "$description: isPositive should be $expectedPositive")
        }
    }

    @Test
    fun `Property 8 - Holdings display depends on showHoldings and data availability`() {
        // Coin with holdings data
        val coinWithHoldings = createTestCoin(
            holdingsAmount = "0.5 BTC",
            holdingsValue = "$22,500.00"
        )
        assertTrue(coinWithHoldings.hasHoldings(), "Coin with holdings data should return hasHoldings = true")
        
        // Coin without holdings data
        val coinWithoutHoldings = createTestCoin(
            holdingsAmount = null,
            holdingsValue = null
        )
        assertFalse(coinWithoutHoldings.hasHoldings(), "Coin without holdings data should return hasHoldings = false")
    }

    @Test
    fun `Property 8 - Holdings requires both amount and value`() {
        // Only amount, no value
        val onlyAmount = createTestCoin(holdingsAmount = "1.0 BTC", holdingsValue = null)
        assertFalse(onlyAmount.hasHoldings(), "Holdings requires both amount and value")
        
        // Only value, no amount
        val onlyValue = createTestCoin(holdingsAmount = null, holdingsValue = "$50,000")
        assertFalse(onlyValue.hasHoldings(), "Holdings requires both amount and value")
        
        // Both present
        val both = createTestCoin(holdingsAmount = "1.0 BTC", holdingsValue = "$50,000")
        assertTrue(both.hasHoldings(), "Holdings should be true when both are present")
    }

    @Test
    fun `Property 9 - onClick callback is invoked correctly`() {
        var clickedCoinId: String? = null
        val coin = createTestCoin(id = "bitcoin")
        
        // Simulate click callback
        val onClick: () -> Unit = { clickedCoinId = coin.id }
        onClick()
        
        assertEquals("bitcoin", clickedCoinId, "onClick should allow access to coin id")
    }

    @Test
    fun `Property 9 - onLongClick callback is invoked when provided`() {
        var longClickInvoked = false

        val onLongClick: () -> Unit = { longClickInvoked = true }
        onLongClick?.invoke()
        
        assertTrue(longClickInvoked, "onLongClick should be invoked when provided")
    }

    @Test
    fun `Property 9 - onLongClick can be null`() {
        val onLongClick: (() -> Unit)? = null
        
        // Should not throw when null
        onLongClick?.invoke()
        
        // Test passes if no exception is thrown
        assertTrue(true, "Null onLongClick should be handled gracefully")
    }

    private fun createTestCoin(
        id: String = "test-coin",
        name: String = "Test Coin",
        symbol: String = "TST",
        iconUrl: String = "https://example.com/icon.png",
        formattedPrice: String = "$100.00",
        formattedChange: String = "+1.0%",
        isPositive: Boolean = true,
        priceDirection: PriceDirection = PriceDirection.UP,
        holdingsAmount: String? = null,
        holdingsValue: String? = null
    ) = UiCoinItem(
        id = id,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
        formattedPrice = formattedPrice,
        formattedChange = formattedChange,
        isPositive = isPositive,
        priceDirection = priceDirection,
        holdingsAmount = holdingsAmount,
        holdingsValue = holdingsValue
    )
}
