package com.example.cryptovault.app.dca

import com.example.cryptovault.app.dca.presentation.DCACreateFormState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DCAAmountValidationPropertyTest {
    
    private val iterations = 100
    
    @Test
    fun `empty amount is invalid with error message`() {
        val formState = DCACreateFormState(
            selectedCoinId = "bitcoin",
            selectedCoinName = "Bitcoin",
            selectedCoinSymbol = "BTC",
            selectedCoinIconUrl = "https://example.com/btc.png",
            amount = ""
        ).validate()
        
        assertFalse(formState.isValid, "Empty amount should be invalid")
        assertNotNull(formState.amountError, "Empty amount should have error message")
        assertEquals("Amount is required", formState.amountError)
    }
    
    @Test
    fun `non-numeric amount is invalid`() {
        val invalidAmounts = listOf("abc", "12.34.56", "1,000", "$100", "100USD", "one hundred")
        
        invalidAmounts.forEach { amount ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = amount
            ).validate()
            
            assertFalse(formState.isValid, "Non-numeric amount '$amount' should be invalid")
            assertNotNull(formState.amountError, "Non-numeric amount '$amount' should have error message")
        }
    }
    
    @Test
    fun `zero amount is invalid`() {
        val formState = DCACreateFormState(
            selectedCoinId = "bitcoin",
            selectedCoinName = "Bitcoin",
            selectedCoinSymbol = "BTC",
            selectedCoinIconUrl = "https://example.com/btc.png",
            amount = "0"
        ).validate()
        
        assertFalse(formState.isValid, "Zero amount should be invalid")
        assertNotNull(formState.amountError, "Zero amount should have error message")
        assertEquals("Amount must be positive", formState.amountError)
    }
    
    @Test
    fun `negative amount is invalid`() {
        repeat(iterations) {
            val negativeAmount = -Random.nextDouble(0.01, 10000.0)
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = negativeAmount.toString()
            ).validate()
            
            assertFalse(formState.isValid, "Negative amount $negativeAmount should be invalid")
            assertNotNull(formState.amountError, "Negative amount should have error message")
            assertEquals("Amount must be positive", formState.amountError)
        }
    }
    
    @Test
    fun `positive amount with coin selected is valid`() {
        repeat(iterations) {
            val positiveAmount = Random.nextDouble(0.01, 100000.0)
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = positiveAmount.toString()
            ).validate()
            
            assertTrue(formState.isValid, "Positive amount $positiveAmount with coin should be valid")
            assertNull(formState.amountError, "Valid amount should have no error")
        }
    }
    
    @Test
    fun `positive amount without coin selected is invalid`() {
        repeat(iterations) {
            val positiveAmount = Random.nextDouble(0.01, 100000.0)
            val formState = DCACreateFormState(
                selectedCoinId = "",
                selectedCoinName = "",
                selectedCoinSymbol = "",
                selectedCoinIconUrl = "",
                amount = positiveAmount.toString()
            ).validate()
            
            assertFalse(formState.isValid, "Positive amount without coin should be invalid")
            assertNull(formState.amountError, "Amount itself is valid, just no coin selected")
        }
    }
    
    @Test
    fun `small positive amounts are valid`() {
        val smallAmounts = listOf("0.01", "0.001", "0.0001", "1", "0.1")
        
        smallAmounts.forEach { amount ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = amount
            ).validate()
            
            assertTrue(formState.isValid, "Small positive amount '$amount' should be valid")
            assertNull(formState.amountError, "Valid amount should have no error")
        }
    }
    
    @Test
    fun `large positive amounts are valid`() {
        val largeAmounts = listOf("1000", "10000", "100000", "999999.99")
        
        largeAmounts.forEach { amount ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = amount
            ).validate()
            
            assertTrue(formState.isValid, "Large positive amount '$amount' should be valid")
            assertNull(formState.amountError, "Valid amount should have no error")
        }
    }
    
    @Test
    fun `validation is idempotent`() {
        repeat(iterations) {
            val amount = if (Random.nextBoolean()) {
                Random.nextDouble(0.01, 10000.0).toString()
            } else {
                listOf("", "abc", "-100", "0")[Random.nextInt(4)]
            }
            
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = amount
            )
            
            val validated1 = formState.validate()
            val validated2 = validated1.validate()
            
            assertEquals(validated1.isValid, validated2.isValid, "Validation should be idempotent")
            assertEquals(validated1.amountError, validated2.amountError, "Error message should be consistent")
        }
    }
    
    @Test
    fun `whitespace-only amount is invalid`() {
        val whitespaceAmounts = listOf(" ", "  ", "\t", "\n", " \t\n ")
        
        whitespaceAmounts.forEach { amount ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = amount
            ).validate()
            
            assertFalse(formState.isValid, "Whitespace-only amount should be invalid")
            assertNotNull(formState.amountError, "Whitespace amount should have error message")
        }
    }
    
    @Test
    fun `amount with leading and trailing whitespace is handled`() {
        // Note: This tests current behavior - amounts with whitespace are treated as invalid
        // because toDoubleOrNull() returns null for " 100 "
        val formState = DCACreateFormState(
            selectedCoinId = "bitcoin",
            selectedCoinName = "Bitcoin",
            selectedCoinSymbol = "BTC",
            selectedCoinIconUrl = "https://example.com/btc.png",
            amount = " 100 "
        ).validate()
        
        // Current implementation treats this as invalid
        assertFalse(formState.isValid, "Amount with whitespace should be invalid")
    }
}
