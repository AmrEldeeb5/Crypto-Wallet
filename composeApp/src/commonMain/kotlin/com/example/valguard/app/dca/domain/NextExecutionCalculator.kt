package com.example.valguard.app.dca.domain

import kotlinx.datetime.*

object NextExecutionCalculator {
    
    fun calculateNextExecutionDate(
        frequency: DCAFrequency,
        dayOfWeek: Int? = null,
        dayOfMonth: Int? = null,
        fromDate: Long = Clock.System.now().toEpochMilliseconds()
    ): Long {
        val instant = Instant.fromEpochMilliseconds(fromDate)
        val currentDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val nextDate = when (frequency) {
            DCAFrequency.DAILY -> currentDate.plus(1, DateTimeUnit.DAY)
            
            DCAFrequency.WEEKLY -> {
                val targetDay = dayOfWeek ?: 1 // Default to Monday
                calculateNextWeekday(currentDate, targetDay)
            }
            
            DCAFrequency.BIWEEKLY -> {
                val targetDay = dayOfWeek ?: 1
                val nextWeek = calculateNextWeekday(currentDate, targetDay)
                // If next week is within 7 days, add another week
                if (nextWeek.toEpochDays() - currentDate.toEpochDays() <= 7) {
                    nextWeek.plus(14, DateTimeUnit.DAY)
                } else {
                    nextWeek.plus(7, DateTimeUnit.DAY)
                }
            }
            
            DCAFrequency.MONTHLY -> {
                val targetDayOfMonth = (dayOfMonth ?: 1).coerceIn(1, 28)
                calculateNextMonthDay(currentDate, targetDayOfMonth)
            }
        }
        
        // Convert to start of day in local timezone
        val nextDateTime = LocalDateTime(nextDate, LocalTime(0, 0, 0))
        return nextDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }
    
    private fun calculateNextWeekday(fromDate: LocalDate, targetDayOfWeek: Int): LocalDate {
        val currentDayOfWeek = fromDate.dayOfWeek.isoDayNumber
        val daysUntilTarget = if (targetDayOfWeek > currentDayOfWeek) {
            targetDayOfWeek - currentDayOfWeek
        } else {
            7 - (currentDayOfWeek - targetDayOfWeek)
        }
        return fromDate.plus(daysUntilTarget, DateTimeUnit.DAY)
    }
    
    private fun calculateNextMonthDay(fromDate: LocalDate, targetDayOfMonth: Int): LocalDate {
        val currentDayOfMonth = fromDate.dayOfMonth
        
        return if (targetDayOfMonth > currentDayOfMonth) {
            // Target day is later this month
            LocalDate(fromDate.year, fromDate.month, targetDayOfMonth)
        } else {
            // Target day is next month
            val nextMonth = fromDate.plus(1, DateTimeUnit.MONTH)
            LocalDate(nextMonth.year, nextMonth.month, targetDayOfMonth)
        }
    }
    
    fun formatNextExecutionDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val daysUntil = date.toEpochDays() - today.toEpochDays()
        
        return when {
            daysUntil == 0 -> "Today"
            daysUntil == 1 -> "Tomorrow"
            daysUntil < 7 -> date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            else -> "${date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}"
        }
    }
}
