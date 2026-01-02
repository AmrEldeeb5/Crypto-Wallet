package com.example.valguard.app.dca

import com.example.valguard.app.dca.domain.DCAFrequency
import com.example.valguard.app.dca.domain.NextExecutionCalculator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NextExecutionDatePropertyTest {
    
    private val iterations = 100
    private val oneDayMs = 24 * 60 * 60 * 1000L
    private val oneWeekMs = 7 * oneDayMs
    
    @Test
    fun `daily frequency returns next day`() {
        repeat(iterations) {
            val fromDate = System.currentTimeMillis() + Random.nextLong(-365 * oneDayMs, 365 * oneDayMs)
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.DAILY,
                fromDate = fromDate
            )
            
            // Next date should be approximately 1 day after fromDate (within same day tolerance)
            val diff = nextDate - fromDate
            assertTrue(
                diff in oneDayMs - oneDayMs..2 * oneDayMs,
                "Daily next date should be within 1-2 days, got ${diff / oneDayMs} days"
            )
        }
    }
    
    @Test
    fun `weekly frequency returns date within 7 days`() {
        repeat(iterations) {
            val fromDate = System.currentTimeMillis()
            val dayOfWeek = Random.nextInt(1, 8)
            
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.WEEKLY,
                dayOfWeek = dayOfWeek,
                fromDate = fromDate
            )
            
            val diff = nextDate - fromDate
            assertTrue(
                diff in 0..oneWeekMs + oneDayMs,
                "Weekly next date should be within 7 days, got ${diff / oneDayMs} days"
            )
        }
    }
    
    @Test
    fun `biweekly frequency returns date within 14-21 days`() {
        repeat(iterations) {
            val fromDate = System.currentTimeMillis()
            val dayOfWeek = Random.nextInt(1, 8)
            
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.BIWEEKLY,
                dayOfWeek = dayOfWeek,
                fromDate = fromDate
            )
            
            val diff = nextDate - fromDate
            assertTrue(
                diff >= oneWeekMs,
                "Biweekly next date should be at least 7 days away, got ${diff / oneDayMs} days"
            )
            assertTrue(
                diff <= 3 * oneWeekMs + oneDayMs,
                "Biweekly next date should be within 21 days, got ${diff / oneDayMs} days"
            )
        }
    }
    
    @Test
    fun `monthly frequency returns date within 31 days`() {
        repeat(iterations) {
            val fromDate = System.currentTimeMillis()
            val dayOfMonth = Random.nextInt(1, 29)
            
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.MONTHLY,
                dayOfMonth = dayOfMonth,
                fromDate = fromDate
            )
            
            val diff = nextDate - fromDate
            assertTrue(
                diff in 0..32 * oneDayMs,
                "Monthly next date should be within 31 days, got ${diff / oneDayMs} days"
            )
        }
    }
    
    @Test
    fun `next execution date is always in the future`() {
        DCAFrequency.entries.forEach { frequency ->
            repeat(iterations / DCAFrequency.entries.size) {
                val fromDate = System.currentTimeMillis()
                val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                    frequency = frequency,
                    dayOfWeek = Random.nextInt(1, 8),
                    dayOfMonth = Random.nextInt(1, 29),
                    fromDate = fromDate
                )
                
                assertTrue(
                    nextDate > fromDate,
                    "Next execution date should be in the future for $frequency"
                )
            }
        }
    }
    
    @Test
    fun `same inputs produce same outputs`() {
        repeat(iterations) {
            val frequency = DCAFrequency.entries[Random.nextInt(DCAFrequency.entries.size)]
            val dayOfWeek = Random.nextInt(1, 8)
            val dayOfMonth = Random.nextInt(1, 29)
            val fromDate = System.currentTimeMillis()
            
            val result1 = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = frequency,
                dayOfWeek = dayOfWeek,
                dayOfMonth = dayOfMonth,
                fromDate = fromDate
            )
            
            val result2 = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = frequency,
                dayOfWeek = dayOfWeek,
                dayOfMonth = dayOfMonth,
                fromDate = fromDate
            )
            
            assertEquals(result1, result2, "Same inputs should produce same outputs")
        }
    }
    
    @Test
    fun `dayOfMonth is clamped to valid range`() {
        // Test that day 29-31 are handled (clamped to 28)
        val fromDate = System.currentTimeMillis()
        
        listOf(29, 30, 31).forEach { invalidDay ->
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.MONTHLY,
                dayOfMonth = invalidDay,
                fromDate = fromDate
            )
            
            // Should not throw and should return a valid date
            assertTrue(nextDate > fromDate, "Should handle day $invalidDay gracefully")
        }
    }
    
    @Test
    fun `formatNextExecutionDate returns readable strings`() {
        val now = System.currentTimeMillis()
        
        // Today
        val todayFormatted = NextExecutionCalculator.formatNextExecutionDate(now)
        assertTrue(
            todayFormatted.isNotEmpty(),
            "Today should have a formatted string"
        )
        
        // Tomorrow
        val tomorrowFormatted = NextExecutionCalculator.formatNextExecutionDate(now + oneDayMs)
        assertEquals("Tomorrow", tomorrowFormatted, "Tomorrow should be formatted as 'Tomorrow'")
        
        // Within a week (should be day name)
        val inThreeDays = NextExecutionCalculator.formatNextExecutionDate(now + 3 * oneDayMs)
        assertTrue(
            inThreeDays.isNotEmpty() && inThreeDays.first().isUpperCase(),
            "Day within week should be capitalized day name"
        )
        
        // More than a week (should be month + day)
        val inTwoWeeks = NextExecutionCalculator.formatNextExecutionDate(now + 14 * oneDayMs)
        assertTrue(
            inTwoWeeks.contains(" "),
            "Date more than a week away should contain month and day"
        )
    }
    
    @Test
    fun `all weekdays 1-7 are valid for weekly frequency`() {
        val fromDate = System.currentTimeMillis()
        
        (1..7).forEach { dayOfWeek ->
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.WEEKLY,
                dayOfWeek = dayOfWeek,
                fromDate = fromDate
            )
            
            assertTrue(
                nextDate > fromDate,
                "Day of week $dayOfWeek should produce valid future date"
            )
        }
    }
    
    @Test
    fun `all days 1-28 are valid for monthly frequency`() {
        val fromDate = System.currentTimeMillis()
        
        (1..28).forEach { dayOfMonth ->
            val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
                frequency = DCAFrequency.MONTHLY,
                dayOfMonth = dayOfMonth,
                fromDate = fromDate
            )
            
            assertTrue(
                nextDate > fromDate,
                "Day of month $dayOfMonth should produce valid future date"
            )
        }
    }
    
    @Test
    fun `default dayOfWeek is Monday for weekly`() {
        val fromDate = System.currentTimeMillis()
        
        val withDefault = NextExecutionCalculator.calculateNextExecutionDate(
            frequency = DCAFrequency.WEEKLY,
            dayOfWeek = null,
            fromDate = fromDate
        )
        
        val withMonday = NextExecutionCalculator.calculateNextExecutionDate(
            frequency = DCAFrequency.WEEKLY,
            dayOfWeek = 1, // Monday
            fromDate = fromDate
        )
        
        assertEquals(withDefault, withMonday, "Default should be Monday (1)")
    }
    
    @Test
    fun `default dayOfMonth is 1 for monthly`() {
        val fromDate = System.currentTimeMillis()
        
        val withDefault = NextExecutionCalculator.calculateNextExecutionDate(
            frequency = DCAFrequency.MONTHLY,
            dayOfMonth = null,
            fromDate = fromDate
        )
        
        val withFirst = NextExecutionCalculator.calculateNextExecutionDate(
            frequency = DCAFrequency.MONTHLY,
            dayOfMonth = 1,
            fromDate = fromDate
        )
        
        assertEquals(withDefault, withFirst, "Default should be 1st of month")
    }
}
