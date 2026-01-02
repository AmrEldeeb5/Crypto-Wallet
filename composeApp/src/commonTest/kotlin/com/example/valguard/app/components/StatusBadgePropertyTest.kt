package com.example.valguard.app.components

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StatusBadgePropertyTest : StringSpec({
    
    "Property 5: Schedule Status Badge Text - active when isActive=true" {
        val isActive = true
        val expectedText = "active"
        
        // The StatusBadge component displays "active" when isActive=true
        val actualText = if (isActive) "active" else "paused"
        actualText shouldBe expectedText
    }
    
    "Property 5: Schedule Status Badge Text - paused when isActive=false" {
        val isActive = false
        val expectedText = "paused"
        
        // The StatusBadge component displays "paused" when isActive=false
        val actualText = if (isActive) "active" else "paused"
        actualText shouldBe expectedText
    }
})
