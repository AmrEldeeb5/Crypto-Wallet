package com.example.cryptowallet.app.realtime.domain

import com.example.cryptowallet.app.realtime.TestGenerators
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.checkAll

/**
 * Property-based tests for PriceUpdate domain model.
 * 
 * **Feature: realtime-price-updates, Property 4: Price direction calculation**
 * **Validates: Requirements 1.4**
 */
class PriceUpdateTest : FunSpec({

    test("Property 4: Price direction calculation - UP when current price is greater than previous") {
        checkAll(100, TestGenerators.priceArb, TestGenerators.priceArb.filter { it > 0 }) { currentPrice, previousPrice ->
            // Ensure current > previous for UP test
            val adjustedCurrent = previousPrice + kotlin.math.abs(currentPrice) + 0.01
            
            val priceUpdate = PriceUpdate(
                coinId = "test-coin",
                price = adjustedCurrent,
                previousPrice = previousPrice,
                timestamp = System.currentTimeMillis()
            )
            
            priceUpdate.priceDirection shouldBe PriceDirection.UP
        }
    }

    test("Property 4: Price direction calculation - DOWN when current price is less than previous") {
        checkAll(100, TestGenerators.priceArb, TestGenerators.priceArb.filter { it > 0.01 }) { currentPrice, previousPrice ->
            // Ensure current < previous for DOWN test
            val adjustedCurrent = previousPrice - kotlin.math.abs(currentPrice.coerceAtMost(previousPrice - 0.001))
            val safeCurrent = if (adjustedCurrent >= previousPrice) previousPrice - 0.01 else adjustedCurrent
            
            val priceUpdate = PriceUpdate(
                coinId = "test-coin",
                price = safeCurrent.coerceAtLeast(0.0),
                previousPrice = previousPrice,
                timestamp = System.currentTimeMillis()
            )
            
            if (priceUpdate.price < priceUpdate.previousPrice) {
                priceUpdate.priceDirection shouldBe PriceDirection.DOWN
            }
        }
    }

    test("Property 4: Price direction calculation - UNCHANGED when prices are equal") {
        checkAll(100, TestGenerators.priceArb) { price ->
            val priceUpdate = PriceUpdate(
                coinId = "test-coin",
                price = price,
                previousPrice = price,
                timestamp = System.currentTimeMillis()
            )
            
            priceUpdate.priceDirection shouldBe PriceDirection.UNCHANGED
        }
    }

    test("Price direction is consistent with mathematical comparison") {
        checkAll(100, TestGenerators.priceArb, TestGenerators.priceArb) { current, previous ->
            val priceUpdate = PriceUpdate(
                coinId = "test-coin",
                price = current,
                previousPrice = previous,
                timestamp = System.currentTimeMillis()
            )
            
            val expectedDirection = when {
                current > previous -> PriceDirection.UP
                current < previous -> PriceDirection.DOWN
                else -> PriceDirection.UNCHANGED
            }
            
            priceUpdate.priceDirection shouldBe expectedDirection
        }
    }
})
