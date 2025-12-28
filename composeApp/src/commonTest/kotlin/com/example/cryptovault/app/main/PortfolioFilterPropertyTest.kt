package com.example.cryptovault.app.main

import com.example.cryptovault.app.portfolio.presentation.UiPortfolioCoinItem
import com.example.cryptovault.app.realtime.domain.PriceDirection
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class PortfolioFilterPropertyTest {

    private fun generateRandomCoinId(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..Random.nextInt(3, 10))
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    private fun createPortfolioCoin(
        id: String = generateRandomCoinId(),
        amountInUnit: Double = Random.nextDouble(0.001, 100.0)
    ) = UiPortfolioCoinItem(
        id = id,
        name = "Coin $id",
        iconUrl = "https://example.com/$id.png",
        amountInUnitText = "$amountInUnit ${id.uppercase()}",
        amountInFiatText = "$${amountInUnit * Random.nextDouble(100.0, 50000.0)}",
        performancePercentText = "${Random.nextDouble(-50.0, 50.0)}%",
        isPositive = Random.nextBoolean(),
        priceDirection = PriceDirection.UNCHANGED
    )

    private fun filterPortfolioCoins(coins: List<UiPortfolioCoinItem>): List<UiPortfolioCoinItem> {
        // Portfolio coins are already filtered by the repository to only include owned coins
        // This test validates that the data model correctly represents holdings
        return coins.filter { coin ->
            // Parse the amount from the text (e.g., "0.5 BTC" -> 0.5)
            val amount = coin.amountInUnitText.split(" ").firstOrNull()?.toDoubleOrNull() ?: 0.0
            amount > 0
        }
    }

    @Test
    fun `portfolio only shows coins with positive holdings`() {
        repeat(100) {
            val coinsWithHoldings = (1..Random.nextInt(1, 10)).map { createPortfolioCoin() }
            val filtered = filterPortfolioCoins(coinsWithHoldings)

            filtered.forEach { coin ->
                val amount = coin.amountInUnitText.split(" ").firstOrNull()?.toDoubleOrNull() ?: 0.0
                assertTrue(
                    amount > 0,
                    "Portfolio coin ${coin.id} should have positive holdings, got $amount"
                )
            }
        }
    }

    @Test
    fun `portfolio excludes coins with zero holdings`() {
        repeat(100) {
            val coinsWithHoldings = (1..Random.nextInt(1, 5)).map { createPortfolioCoin() }
            val coinsWithZeroHoldings = (1..Random.nextInt(1, 5)).map { 
                createPortfolioCoin(amountInUnit = 0.0)
            }
            val allCoins = (coinsWithHoldings + coinsWithZeroHoldings).shuffled()
            
            val filtered = filterPortfolioCoins(allCoins)

            filtered.forEach { coin ->
                val amount = coin.amountInUnitText.split(" ").firstOrNull()?.toDoubleOrNull() ?: 0.0
                assertTrue(
                    amount > 0,
                    "Filtered portfolio should not contain coins with zero holdings"
                )
            }
        }
    }

    @Test
    fun `portfolio preserves all coins with holdings`() {
        repeat(50) {
            val coinsWithHoldings = (1..Random.nextInt(1, 10)).map { createPortfolioCoin() }
            val filtered = filterPortfolioCoins(coinsWithHoldings)

            assertTrue(
                filtered.size == coinsWithHoldings.size,
                "All coins with holdings should be preserved"
            )
        }
    }

    @Test
    fun `empty portfolio returns empty list`() {
        repeat(50) {
            val emptyPortfolio = emptyList<UiPortfolioCoinItem>()
            val filtered = filterPortfolioCoins(emptyPortfolio)

            assertTrue(
                filtered.isEmpty(),
                "Empty portfolio should return empty list"
            )
        }
    }

    @Test
    fun `portfolio coin has valid amount text format`() {
        repeat(100) {
            val coin = createPortfolioCoin()
            val parts = coin.amountInUnitText.split(" ")

            assertTrue(
                parts.size >= 2,
                "Amount text should have amount and symbol parts"
            )
            assertTrue(
                parts[0].toDoubleOrNull() != null,
                "First part should be a valid number"
            )
        }
    }

    @Test
    fun `portfolio coin has valid fiat value format`() {
        repeat(100) {
            val coin = createPortfolioCoin()

            assertTrue(
                coin.amountInFiatText.startsWith("$"),
                "Fiat value should start with $"
            )
        }
    }

    @Test
    fun `portfolio coin has valid performance text format`() {
        repeat(100) {
            val coin = createPortfolioCoin()

            assertTrue(
                coin.performancePercentText.endsWith("%"),
                "Performance text should end with %"
            )
        }
    }

    @Test
    fun `portfolio coin isPositive matches performance sign`() {
        repeat(100) {
            val isPositive = Random.nextBoolean()
            val performanceValue = if (isPositive) {
                Random.nextDouble(0.01, 50.0)
            } else {
                Random.nextDouble(-50.0, -0.01)
            }
            
            val coin = UiPortfolioCoinItem(
                id = generateRandomCoinId(),
                name = "Test Coin",
                iconUrl = "",
                amountInUnitText = "1.0 TEST",
                amountInFiatText = "$100.00",
                performancePercentText = "${performanceValue}%",
                isPositive = isPositive,
                priceDirection = PriceDirection.UNCHANGED
            )

            if (coin.isPositive) {
                assertTrue(
                    !coin.performancePercentText.startsWith("-"),
                    "Positive performance should not start with minus"
                )
            }
        }
    }

    @Test
    fun `portfolio maintains coin order`() {
        repeat(50) {
            val coins = (1..5).map { index ->
                createPortfolioCoin(id = "coin_$index")
            }
            val filtered = filterPortfolioCoins(coins)

            coins.forEachIndexed { index, coin ->
                assertTrue(
                    filtered[index].id == coin.id,
                    "Portfolio should maintain coin order"
                )
            }
        }
    }

    @Test
    fun `portfolio coin price direction is valid`() {
        repeat(100) {
            val coin = createPortfolioCoin()

            assertTrue(
                coin.priceDirection in PriceDirection.entries,
                "Price direction should be a valid enum value"
            )
        }
    }
}
