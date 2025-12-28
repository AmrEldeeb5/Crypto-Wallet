package com.example.cryptovault.app.dca

import com.example.cryptovault.app.dca.domain.DCAFrequency
import com.example.cryptovault.app.dca.presentation.DCACreateFormState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DCAFrequencyFormFieldsPropertyTest {
    
    private val iterations = 100
    
    @Test
    fun `daily frequency does not require day selection`() {
        val formState = DCACreateFormState(
            selectedCoinId = "bitcoin",
            selectedCoinName = "Bitcoin",
            selectedCoinSymbol = "BTC",
            selectedCoinIconUrl = "https://example.com/btc.png",
            amount = "100",
            frequency = DCAFrequency.DAILY
        ).validate()
        
        assertTrue(formState.isValid, "Daily frequency should be valid without day selection")
        assertEquals(DCAFrequency.DAILY, formState.frequency)
    }
    
    @Test
    fun `weekly frequency uses dayOfWeek`() {
        (1..7).forEach { day ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = "100",
                frequency = DCAFrequency.WEEKLY,
                dayOfWeek = day
            ).validate()
            
            assertTrue(formState.isValid, "Weekly frequency with day $day should be valid")
            assertEquals(day, formState.dayOfWeek, "Day of week should be preserved")
        }
    }
    
    @Test
    fun `biweekly frequency uses dayOfWeek`() {
        (1..7).forEach { day ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = "100",
                frequency = DCAFrequency.BIWEEKLY,
                dayOfWeek = day
            ).validate()
            
            assertTrue(formState.isValid, "Biweekly frequency with day $day should be valid")
            assertEquals(day, formState.dayOfWeek, "Day of week should be preserved")
        }
    }
    
    @Test
    fun `monthly frequency uses dayOfMonth`() {
        (1..28).forEach { day ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = "100",
                frequency = DCAFrequency.MONTHLY,
                dayOfMonth = day
            ).validate()
            
            assertTrue(formState.isValid, "Monthly frequency with day $day should be valid")
            assertEquals(day, formState.dayOfMonth, "Day of month should be preserved")
        }
    }
    
    @Test
    fun `all frequencies have display names`() {
        DCAFrequency.entries.forEach { frequency ->
            assertTrue(frequency.displayName.isNotEmpty(), "Frequency $frequency should have display name")
        }
    }
    
    @Test
    fun `frequency display names are user-friendly`() {
        assertEquals("Daily", DCAFrequency.DAILY.displayName)
        assertEquals("Weekly", DCAFrequency.WEEKLY.displayName)
        assertEquals("Bi-weekly", DCAFrequency.BIWEEKLY.displayName)
        assertEquals("Monthly", DCAFrequency.MONTHLY.displayName)
    }
    
    @Test
    fun `frequency values are lowercase strings`() {
        DCAFrequency.entries.forEach { frequency ->
            assertEquals(frequency.value, frequency.value.lowercase(), "Frequency value should be lowercase")
        }
    }
    
    @Test
    fun `default dayOfWeek is Monday (1)`() {
        val formState = DCACreateFormState()
        assertEquals(1, formState.dayOfWeek, "Default day of week should be Monday (1)")
    }
    
    @Test
    fun `default dayOfMonth is 1`() {
        val formState = DCACreateFormState()
        assertEquals(1, formState.dayOfMonth, "Default day of month should be 1")
    }
    
    @Test
    fun `default frequency is WEEKLY`() {
        val formState = DCACreateFormState()
        assertEquals(DCAFrequency.WEEKLY, formState.frequency, "Default frequency should be WEEKLY")
    }
    
    @Test
    fun `changing frequency preserves other form fields`() {
        repeat(iterations) {
            val amount = Random.nextDouble(1.0, 10000.0).toString()
            val dayOfWeek = Random.nextInt(1, 8)
            val dayOfMonth = Random.nextInt(1, 29)
            
            val initialState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = amount,
                frequency = DCAFrequency.WEEKLY,
                dayOfWeek = dayOfWeek,
                dayOfMonth = dayOfMonth
            )
            
            val newFrequency = DCAFrequency.entries[Random.nextInt(DCAFrequency.entries.size)]
            val updatedState = initialState.copy(frequency = newFrequency)
            
            assertEquals(amount, updatedState.amount, "Amount should be preserved")
            assertEquals("bitcoin", updatedState.selectedCoinId, "Coin ID should be preserved")
            assertEquals(dayOfWeek, updatedState.dayOfWeek, "Day of week should be preserved")
            assertEquals(dayOfMonth, updatedState.dayOfMonth, "Day of month should be preserved")
        }
    }
    
    @Test
    fun `dayOfWeek range is 1 to 7`() {
        // Valid days
        (1..7).forEach { day ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = "100",
                frequency = DCAFrequency.WEEKLY,
                dayOfWeek = day
            )
            assertTrue(formState.dayOfWeek in 1..7, "Day of week should be in range 1-7")
        }
    }
    
    @Test
    fun `dayOfMonth range is 1 to 28`() {
        // Valid days (1-28 to avoid month-end issues)
        (1..28).forEach { day ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = "100",
                frequency = DCAFrequency.MONTHLY,
                dayOfMonth = day
            )
            assertTrue(formState.dayOfMonth in 1..28, "Day of month should be in range 1-28")
        }
    }
    
    @Test
    fun `form state validation does not depend on frequency-specific fields`() {
        // Validation only checks amount and coin selection, not day fields
        DCAFrequency.entries.forEach { frequency ->
            val formState = DCACreateFormState(
                selectedCoinId = "bitcoin",
                selectedCoinName = "Bitcoin",
                selectedCoinSymbol = "BTC",
                selectedCoinIconUrl = "https://example.com/btc.png",
                amount = "100",
                frequency = frequency
            ).validate()
            
            assertTrue(formState.isValid, "Form should be valid for frequency $frequency with valid amount and coin")
        }
    }
    
    @Test
    fun `frequency can be changed multiple times`() {
        var formState = DCACreateFormState(
            selectedCoinId = "bitcoin",
            selectedCoinName = "Bitcoin",
            selectedCoinSymbol = "BTC",
            selectedCoinIconUrl = "https://example.com/btc.png",
            amount = "100"
        )
        
        // Cycle through all frequencies
        DCAFrequency.entries.forEach { frequency ->
            formState = formState.copy(frequency = frequency)
            assertEquals(frequency, formState.frequency, "Frequency should be updated to $frequency")
        }
    }
    
    @Test
    fun `all frequency entries are unique`() {
        val values = DCAFrequency.entries.map { it.value }
        val displayNames = DCAFrequency.entries.map { it.displayName }
        
        assertEquals(values.size, values.toSet().size, "All frequency values should be unique")
        assertEquals(displayNames.size, displayNames.toSet().size, "All display names should be unique")
    }
}
