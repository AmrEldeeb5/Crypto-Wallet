package com.example.valguard.app.dca

import com.example.valguard.app.dca.domain.DCAFrequency
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class DCAFrequencyPropertyTest : StringSpec({
    
    "Property 3: Enum Completeness (DCAFrequency) - contains all required values" {
        val frequencies = DCAFrequency.entries.map { it.name }
        
        frequencies.shouldContainExactlyInAnyOrder("DAILY", "WEEKLY", "BIWEEKLY", "MONTHLY")
    }
})
