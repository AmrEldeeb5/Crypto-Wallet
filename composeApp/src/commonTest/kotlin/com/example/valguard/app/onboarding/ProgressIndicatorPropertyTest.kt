package com.example.valguard.app.onboarding

import com.example.valguard.app.onboarding.presentation.components.calculateProgressPercentage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgressIndicatorPropertyTest {
    
    @Test
    fun `step 0 shows 25 percent progress`() {
        val progress = calculateProgressPercentage(currentStep = 0, totalSteps = 4)
        assertEquals(25, progress, "Step 0 should show 25% progress")
    }
    
    @Test
    fun `step 1 shows 50 percent progress`() {
        val progress = calculateProgressPercentage(currentStep = 1, totalSteps = 4)
        assertEquals(50, progress, "Step 1 should show 50% progress")
    }
    
    @Test
    fun `step 2 shows 75 percent progress`() {
        val progress = calculateProgressPercentage(currentStep = 2, totalSteps = 4)
        assertEquals(75, progress, "Step 2 should show 75% progress")
    }
    
    @Test
    fun `step 3 shows 100 percent progress`() {
        val progress = calculateProgressPercentage(currentStep = 3, totalSteps = 4)
        assertEquals(100, progress, "Step 3 should show 100% progress")
    }
    
    @Test
    fun `progress is always between 0 and 100`() {
        for (step in 0..3) {
            val progress = calculateProgressPercentage(currentStep = step, totalSteps = 4)
            assertTrue(
                progress in 0..100,
                "Progress should be between 0 and 100, got $progress for step $step"
            )
        }
    }
    
    @Test
    fun `progress increases monotonically with step`() {
        var previousProgress = 0
        for (step in 0..3) {
            val progress = calculateProgressPercentage(currentStep = step, totalSteps = 4)
            assertTrue(
                progress >= previousProgress,
                "Progress should increase monotonically, got $progress after $previousProgress"
            )
            previousProgress = progress
        }
    }
    
    @Test
    fun `progress formula is correct for any total steps`() {
        // Test with different total steps
        val testCases = listOf(
            Triple(0, 2, 50),   // step 0 of 2 = 50%
            Triple(1, 2, 100),  // step 1 of 2 = 100%
            Triple(0, 5, 20),   // step 0 of 5 = 20%
            Triple(4, 5, 100),  // step 4 of 5 = 100%
            Triple(0, 10, 10),  // step 0 of 10 = 10%
            Triple(9, 10, 100)  // step 9 of 10 = 100%
        )
        
        testCases.forEach { (step, total, expected) ->
            val progress = calculateProgressPercentage(currentStep = step, totalSteps = total)
            assertEquals(
                expected,
                progress,
                "Step $step of $total should be $expected%, got $progress%"
            )
        }
    }
    
    @Test
    fun `each step increases progress by 25 percent`() {
        val step0 = calculateProgressPercentage(0, 4)
        val step1 = calculateProgressPercentage(1, 4)
        val step2 = calculateProgressPercentage(2, 4)
        val step3 = calculateProgressPercentage(3, 4)
        
        assertEquals(25, step1 - step0, "Step 0 to 1 should increase by 25%")
        assertEquals(25, step2 - step1, "Step 1 to 2 should increase by 25%")
        assertEquals(25, step3 - step2, "Step 2 to 3 should increase by 25%")
    }
    
    @Test
    fun `first step is never 0 percent`() {
        val progress = calculateProgressPercentage(currentStep = 0, totalSteps = 4)
        assertTrue(
            progress > 0,
            "First step should show some progress, not 0%"
        )
    }
    
    @Test
    fun `last step is always 100 percent`() {
        for (totalSteps in 2..10) {
            val progress = calculateProgressPercentage(
                currentStep = totalSteps - 1,
                totalSteps = totalSteps
            )
            assertEquals(
                100,
                progress,
                "Last step should always be 100%, got $progress% for $totalSteps total steps"
            )
        }
    }
}
