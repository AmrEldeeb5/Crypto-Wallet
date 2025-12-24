package com.example.cryptowallet.app.realtime.domain

import com.example.cryptowallet.app.realtime.TestGenerators
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.math.abs
import kotlin.test.Test

/**
 * Property-based tests for PortfolioValueCalculator.
 */
class PortfolioValueCalculatorTest {

    private val randomSource = RandomSource.default()

    /**
     * **Feature: realtime-price-updates, Property 5: Portfolio value calculation on price update**
     *
     * For any portfolio with owned coins and a price update for one of those coins,
     * the new portfolio value SHALL equal the sum of (amount owned × current price) for all coins.
     *
     * Validates: Requirements 2.2, 5.2
     */
    @Test
    fun `Property 5 - portfolio value calculation on price update`() = runTest {
        val holdingsArb = Arb.list(
            io.kotest.property.arbitrary.arbitrary {
                PortfolioValueCalculator.CoinHolding(
                    coinId = TestGenerators.simpleCoinIdArb.bind(),
                    amountOwned = Arb.double(min = 0.001, max = 1000.0).bind()
                )
            },
            1..10
        )

        checkAll(100, holdingsArb) { holdings ->
            // Generate prices for all holdings
            val prices = holdings.associate { it.coinId to TestGenerators.priceArb.sample(randomSource).value }

            // Calculate total value
            val totalValue = PortfolioValueCalculator.calculateTotalValue(holdings, prices)

            // Verify it equals sum of (amount × price)
            val expectedValue = holdings.sumOf { holding ->
                val price = prices[holding.coinId] ?: 0.0
                holding.amountOwned * price
            }

            // Use approximate equality due to floating point
            abs(totalValue - expectedValue) shouldBeLessThan 0.0001
        }
    }

    /**
     * Test that empty portfolio has zero value.
     */
    @Test
    fun `empty portfolio has zero value`() = runTest {
        val prices = mapOf("bitcoin" to 50000.0, "ethereum" to 3000.0)
        val totalValue = PortfolioValueCalculator.calculateTotalValue(emptyList(), prices)
        totalValue shouldBe 0.0
    }

    /**
     * Test that missing prices are treated as zero.
     */
    @Test
    fun `missing prices are treated as zero`() = runTest {
        val holdings = listOf(
            PortfolioValueCalculator.CoinHolding("bitcoin", 1.0),
            PortfolioValueCalculator.CoinHolding("unknown", 100.0)
        )
        val prices = mapOf("bitcoin" to 50000.0)

        val totalValue = PortfolioValueCalculator.calculateTotalValue(holdings, prices)

        // Only bitcoin contributes (1.0 * 50000 = 50000)
        totalValue shouldBe 50000.0
    }

    /**
     * Test that portfolio value is always non-negative.
     */
    @Test
    fun `portfolio value is always non-negative`() = runTest {
        val holdingsArb = Arb.list(
            io.kotest.property.arbitrary.arbitrary {
                PortfolioValueCalculator.CoinHolding(
                    coinId = TestGenerators.simpleCoinIdArb.bind(),
                    amountOwned = Arb.double(min = 0.0, max = 1000.0).bind()
                )
            },
            0..10
        )

        checkAll(100, holdingsArb) { holdings ->
            val prices = holdings.associate { it.coinId to TestGenerators.priceArb.sample(randomSource).value }
            val totalValue = PortfolioValueCalculator.calculateTotalValue(holdings, prices)
            totalValue shouldBeGreaterThanOrEqual 0.0
        }
    }

    /**
     * Test incremental update calculation.
     */
    @Test
    fun `updateValueOnPriceChange correctly updates portfolio value`() = runTest {
        checkAll(100, TestGenerators.priceArb, TestGenerators.priceArb, Arb.double(min = 0.001, max = 100.0)) { oldPrice, newPrice, amount ->
            val holding = PortfolioValueCalculator.CoinHolding("bitcoin", amount)

            // Start with a portfolio containing just this coin
            val initialValue = amount * oldPrice

            // Update the value
            val updatedValue = PortfolioValueCalculator.updateValueOnPriceChange(
                currentValue = initialValue,
                holding = holding,
                oldPrice = oldPrice,
                newPrice = newPrice
            )

            // Should equal amount * newPrice
            val expectedValue = amount * newPrice
            abs(updatedValue - expectedValue) shouldBeLessThan 0.0001
        }
    }

    /**
     * Test that price update with same price doesn't change value.
     */
    @Test
    fun `same price update doesn't change value`() = runTest {
        checkAll(100, TestGenerators.priceArb, Arb.double(min = 0.001, max = 100.0)) { price, amount ->
            val holding = PortfolioValueCalculator.CoinHolding("bitcoin", amount)
            val initialValue = amount * price

            val updatedValue = PortfolioValueCalculator.updateValueOnPriceChange(
                currentValue = initialValue,
                holding = holding,
                oldPrice = price,
                newPrice = price
            )

            abs(updatedValue - initialValue) shouldBeLessThan 0.0001
        }
    }
}
