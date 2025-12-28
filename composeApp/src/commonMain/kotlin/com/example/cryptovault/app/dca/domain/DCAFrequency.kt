package com.example.cryptovault.app.dca.domain

enum class DCAFrequency(val value: String, val displayName: String) {
    DAILY("daily", "Daily"),
    WEEKLY("weekly", "Weekly"),
    BIWEEKLY("biweekly", "2-Week"),
    MONTHLY("monthly", "Monthly");
    
    companion object {
        fun fromValue(value: String): DCAFrequency {
            return entries.find { it.value == value } ?: WEEKLY
        }
    }
}
