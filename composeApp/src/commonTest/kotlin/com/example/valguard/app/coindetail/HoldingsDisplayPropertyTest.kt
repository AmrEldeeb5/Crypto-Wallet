package com.example.valguard.app.coindetail

import com.example.valguard.app.coindetail.domain.CoinHoldings
import com.example.valguard.app.coindetail.presentation.CoinDetailState
import com.example.valguard.app.core.util.UiState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HoldingsDisplayPropertyTest {
    
    private val iterations = 100
    
    @Test
    fun `hasHoldings is true when holdings exist with positive amount`() {
        repeat(iterations) {
            val positiveAmount = Random.nextDouble(0.00001, 1000000.0)
            
            val holdings = CoinHoldings(
                coinId = "bitcoin",
                amountOwned = positiveAmount,
                averagePurchasePrice = Random.nextDouble(1.0, 100000.0),
                currentValue = positiveAmount * Random.nextDouble(1.0, 100000.0),
                profitLoss = Random.nextDouble(-10000.0, 10000.0),
                profitLossPercentage = Random.nextDouble(-100.0, 1000.0)
            )
            
            val state = CoinDetailState(
                coinId = "bitcoin",
                holdings = holdings
            )
            
            assertTrue(
                state.hasHoldings,
                "hasHoldings should be true when amountOwned is $positiveAmount"
            )
        }
    }
    
    @Test
    fun `hasHoldings is false when holdings is null`() {
        val state = CoinDetailState(
            coinId = "bitcoin",
            holdings = null
        )
        
        assertFalse(
            state.hasHoldings,
            "hasHoldings should be false when holdings is null"
        )
    }
    
    @Test
    fun `hasHoldings is false when amountOwned is zero`() {
        val holdings = CoinHoldings(
            coinId = "bitcoin",
            amountOwned = 0.0,
            averagePurchasePrice = 50000.0,
            currentValue = 0.0,
            profitLoss = 0.0,
            profitLossPercentage = 0.0
        )
        
        val state = CoinDetailState(
            coinId = "bitcoin",
            holdings = holdings
        )
        
        assertFalse(
            state.hasHoldings,
            "hasHoldings should be false when amountOwned is 0"
        )
    }
    
    @Test
    fun `hasHoldings is false when amountOwned is negative`() {
        // Edge case: negative amounts should not show holdings
        val holdings = CoinHoldings(
            coinId = "bitcoin",
            amountOwned = -1.0,
            averagePurchasePrice = 50000.0,
            currentValue = -50000.0,
            profitLoss = 0.0,
            profitLossPercentage = 0.0
        )
        
        val state = CoinDetailState(
            coinId = "bitcoin",
            holdings = holdings
        )
        
        assertFalse(
            state.hasHoldings,
            "hasHoldings should be false when amountOwned is negative"
        )
    }
    
    @Test
    fun `very small positive amounts still show holdings`() {
        val smallAmounts = listOf(0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001)
        
        smallAmounts.forEach { amount ->
            val holdings = CoinHoldings(
                coinId = "bitcoin",
                amountOwned = amount,
                averagePurchasePrice = 50000.0,
                currentValue = amount * 50000.0,
                profitLoss = 0.0,
                profitLossPercentage = 0.0
            )
            
            val state = CoinDetailState(
                coinId = "bitcoin",
                holdings = holdings
            )
            
            assertTrue(
                state.hasHoldings,
                "hasHoldings should be true for small amount $amount"
            )
        }
    }
    
    @Test
    fun `holdings display is independent of profit or loss`() {
        // Holdings should show regardless of whether user is in profit or loss
        repeat(iterations) {
            val profitLoss = Random.nextDouble(-100000.0, 100000.0)
            
            val holdings = CoinHoldings(
                coinId = "bitcoin",
                amountOwned = 1.0,
                averagePurchasePrice = 50000.0,
                currentValue = 50000.0 + profitLoss,
                profitLoss = profitLoss,
                profitLossPercentage = (profitLoss / 50000.0) * 100
            )
            
            val state = CoinDetailState(
                coinId = "bitcoin",
                holdings = holdings
            )
            
            assertTrue(
                state.hasHoldings,
                "hasHoldings should be true regardless of profit/loss ($profitLoss)"
            )
        }
    }
    
    @Test
    fun `holdings display is independent of coin price`() {
        repeat(iterations) {
            val price = Random.nextDouble(0.00001, 100000.0)
            val amount = Random.nextDouble(0.00001, 1000.0)
            
            val holdings = CoinHoldings(
                coinId = "bitcoin",
                amountOwned = amount,
                averagePurchasePrice = price,
                currentValue = amount * price,
                profitLoss = 0.0,
                profitLossPercentage = 0.0
            )
            
            val state = CoinDetailState(
                coinId = "bitcoin",
                holdings = holdings
            )
            
            assertTrue(
                state.hasHoldings,
                "hasHoldings should be true regardless of price ($price)"
            )
        }
    }
    
    @Test
    fun `holdings display works for any coin`() {
        val coinIds = listOf("bitcoin", "ethereum", "solana", "cardano", "dogecoin")
        
        coinIds.forEach { coinId ->
            val holdings = CoinHoldings(
                coinId = coinId,
                amountOwned = 1.0,
                averagePurchasePrice = 100.0,
                currentValue = 100.0,
                profitLoss = 0.0,
                profitLossPercentage = 0.0
            )
            
            val state = CoinDetailState(
                coinId = coinId,
                holdings = holdings
            )
            
            assertTrue(
                state.hasHoldings,
                "hasHoldings should be true for coin $coinId"
            )
        }
    }
    
    @Test
    fun `state defaults have no holdings`() {
        val defaultState = CoinDetailState()
        
        assertFalse(
            defaultState.hasHoldings,
            "Default state should have no holdings"
        )
        assertEquals(null, defaultState.holdings, "Default holdings should be null")
    }
    
    @Test
    fun `holdings with different coinId still shows if amount is positive`() {
        // Edge case: holdings coinId doesn't match state coinId
        // This shouldn't happen in practice, but the hasHoldings check is based on amount
        val holdings = CoinHoldings(
            coinId = "ethereum",
            amountOwned = 1.0,
            averagePurchasePrice = 3000.0,
            currentValue = 3000.0,
            profitLoss = 0.0,
            profitLossPercentage = 0.0
        )
        
        val state = CoinDetailState(
            coinId = "bitcoin",
            holdings = holdings
        )
        
        // hasHoldings only checks if holdings exists and amount > 0
        assertTrue(
            state.hasHoldings,
            "hasHoldings checks amount, not coinId match"
        )
    }
    
    @Test
    fun `CoinHoldings profit loss calculation is preserved`() {
        repeat(iterations) {
            val amountOwned = Random.nextDouble(0.1, 100.0)
            val avgPrice = Random.nextDouble(100.0, 100000.0)
            val currentPrice = Random.nextDouble(100.0, 100000.0)
            val currentValue = amountOwned * currentPrice
            val costBasis = amountOwned * avgPrice
            val profitLoss = currentValue - costBasis
            val profitLossPercentage = (profitLoss / costBasis) * 100
            
            val holdings = CoinHoldings(
                coinId = "bitcoin",
                amountOwned = amountOwned,
                averagePurchasePrice = avgPrice,
                currentValue = currentValue,
                profitLoss = profitLoss,
                profitLossPercentage = profitLossPercentage
            )
            
            assertEquals(amountOwned, holdings.amountOwned, 0.0001)
            assertEquals(avgPrice, holdings.averagePurchasePrice, 0.0001)
            assertEquals(currentValue, holdings.currentValue, 0.0001)
            assertEquals(profitLoss, holdings.profitLoss, 0.01)
            assertEquals(profitLossPercentage, holdings.profitLossPercentage, 0.01)
        }
    }
}
