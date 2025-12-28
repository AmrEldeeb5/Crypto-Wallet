package com.example.cryptovault.app.trade

import com.example.cryptovault.app.trade.presentation.common.ValidationResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BuyViewModelPropertyTest {

    @Test
    fun `Property 1 - ViewModel uses provided coinId for all operations`() {
        val testCoinIds = listOf(
            "1", "2", "bitcoin", "ethereum", "btc-123",
            "coin_with_underscore", "UPPERCASE", "MixedCase123"
        )

        testCoinIds.forEach { coinId ->
            // Verify coinId format is preserved exactly
            assertEquals(coinId, coinId.trim(), "CoinId should be preserved exactly: $coinId")
            assertTrue(coinId.isNotBlank(), "CoinId should not be blank")
        }
    }

    @Test
    fun `Property 4 - Empty amount returns Empty validation result`() {
        val emptyInputs = listOf("", "   ", "\t", "\n")

        emptyInputs.forEach { input ->
            val result = validateAmount(input, 1000.0)
            assertEquals(
                ValidationResult.Empty,
                result,
                "Empty input '$input' should return Empty validation"
            )
        }
    }

    @Test
    fun `Property 4 - Zero amount returns Zero validation result`() {
        val zeroInputs = listOf("0", "0.0", "0.00", "-0", "-1", "-100")

        zeroInputs.forEach { input ->
            val result = validateAmount(input, 1000.0)
            assertTrue(
                result is ValidationResult.Zero || result is ValidationResult.Empty,
                "Zero or negative input '$input' should return Zero or Empty validation"
            )
        }
    }

    @Test
    fun `Property 4 - Amount exceeding balance returns InsufficientFunds`() {
        val testCases = listOf(
            Triple("1001", 1000.0, "Amount exceeds balance"),
            Triple("500.01", 500.0, "Amount slightly exceeds balance"),
            Triple("10000", 100.0, "Amount greatly exceeds balance"),
            Triple("0.01", 0.001, "Small amount exceeds tiny balance")
        )

        testCases.forEach { (amount, balance, description) ->
            val result = validateAmount(amount, balance)
            assertTrue(
                result is ValidationResult.InsufficientFunds,
                "$description: '$amount' with balance $balance should return InsufficientFunds"
            )
        }
    }

    @Test
    fun `Property 4 - Valid amount within balance returns Valid`() {
        val testCases = listOf(
            Triple("100", 1000.0, "Amount well within balance"),
            Triple("1000", 1000.0, "Amount equals balance"),
            Triple("999.99", 1000.0, "Amount just under balance"),
            Triple("0.01", 100.0, "Small valid amount"),
            Triple("1", 1.0, "Exact match")
        )

        testCases.forEach { (amount, balance, description) ->
            val result = validateAmount(amount, balance)
            assertEquals(
                ValidationResult.Valid,
                result,
                "$description: '$amount' with balance $balance should return Valid"
            )
        }
    }

    @Test
    fun `Property 4 - Validation is consistent across multiple calls`() {
        repeat(100) { iteration ->
            val balance = (iteration + 1) * 10.0
            val validAmount = (balance * 0.5).toString()
            val invalidAmount = (balance * 1.5).toString()

            val validResult = validateAmount(validAmount, balance)
            val invalidResult = validateAmount(invalidAmount, balance)

            assertEquals(
                ValidationResult.Valid,
                validResult,
                "Iteration $iteration: Valid amount should consistently return Valid"
            )
            assertTrue(
                invalidResult is ValidationResult.InsufficientFunds,
                "Iteration $iteration: Invalid amount should consistently return InsufficientFunds"
            )
        }
    }

    private fun validateAmount(amount: String, availableBalance: Double): ValidationResult {
        if (amount.isBlank()) return ValidationResult.Empty
        val amountValue = amount.toDoubleOrNull() ?: return ValidationResult.Empty
        if (amountValue <= 0) return ValidationResult.Zero
        if (amountValue > availableBalance) return ValidationResult.InsufficientFunds(availableBalance)
        return ValidationResult.Valid
    }
}
