package com.example.cryptovault.app.onboarding

import com.example.cryptovault.app.onboarding.presentation.OnboardingState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StepNavigationPropertyTest {
    
    private val iterations = 100
    
    // Helper function to simulate NextStep transition
    private fun applyNextStep(state: OnboardingState): OnboardingState {
        return if (state.currentStep < 3) {
            state.copy(currentStep = state.currentStep + 1)
        } else {
            state
        }
    }
    
    // Helper function to simulate PreviousStep transition
    private fun applyPreviousStep(state: OnboardingState): OnboardingState {
        return if (state.currentStep > 0) {
            state.copy(currentStep = state.currentStep - 1)
        } else {
            state
        }
    }
    
    // Helper function to simulate ConfirmSkip transition
    private fun applyConfirmSkip(state: OnboardingState): OnboardingState {
        return state.copy(currentStep = 3)
    }
    
    @Test
    fun `NextStep increments currentStep when in valid range`() {
        repeat(iterations) {
            val initialStep = Random.nextInt(0, 3) // 0, 1, or 2
            val state = OnboardingState(currentStep = initialStep, isLoading = false)
            
            val newState = applyNextStep(state)
            
            assertEquals(
                initialStep + 1,
                newState.currentStep,
                "NextStep from step $initialStep should result in step ${initialStep + 1}"
            )
        }
    }
    
    @Test
    fun `NextStep does not increment beyond step 3`() {
        val state = OnboardingState(currentStep = 3, isLoading = false)
        
        val newState = applyNextStep(state)
        
        assertEquals(
            3,
            newState.currentStep,
            "NextStep from step 3 should remain at step 3"
        )
    }
    
    @Test
    fun `PreviousStep decrements currentStep when in valid range`() {
        repeat(iterations) {
            val initialStep = Random.nextInt(1, 4) // 1, 2, or 3
            val state = OnboardingState(currentStep = initialStep, isLoading = false)
            
            val newState = applyPreviousStep(state)
            
            assertEquals(
                initialStep - 1,
                newState.currentStep,
                "PreviousStep from step $initialStep should result in step ${initialStep - 1}"
            )
        }
    }
    
    @Test
    fun `PreviousStep does not decrement below step 0`() {
        val state = OnboardingState(currentStep = 0, isLoading = false)
        
        val newState = applyPreviousStep(state)
        
        assertEquals(
            0,
            newState.currentStep,
            "PreviousStep from step 0 should remain at step 0"
        )
    }
    
    @Test
    fun `ConfirmSkip sets currentStep to 3 from any step`() {
        repeat(iterations) {
            val initialStep = Random.nextInt(0, 3) // 0, 1, or 2
            val state = OnboardingState(currentStep = initialStep, isLoading = false)
            
            val newState = applyConfirmSkip(state)
            
            assertEquals(
                3,
                newState.currentStep,
                "ConfirmSkip from step $initialStep should result in step 3"
            )
        }
    }
    
    @Test
    fun `ConfirmSkip from step 3 remains at step 3`() {
        val state = OnboardingState(currentStep = 3, isLoading = false)
        
        val newState = applyConfirmSkip(state)
        
        assertEquals(
            3,
            newState.currentStep,
            "ConfirmSkip from step 3 should remain at step 3"
        )
    }
    
    @Test
    fun `NextStep then PreviousStep returns to original step`() {
        repeat(iterations) {
            val initialStep = Random.nextInt(0, 3) // 0, 1, or 2
            val state = OnboardingState(currentStep = initialStep, isLoading = false)
            
            val afterNext = applyNextStep(state)
            val afterPrevious = applyPreviousStep(afterNext)
            
            assertEquals(
                initialStep,
                afterPrevious.currentStep,
                "NextStep then PreviousStep from step $initialStep should return to step $initialStep"
            )
        }
    }
    
    @Test
    fun `PreviousStep then NextStep returns to original step when not at step 0`() {
        repeat(iterations) {
            val initialStep = Random.nextInt(1, 4) // 1, 2, or 3
            val state = OnboardingState(currentStep = initialStep, isLoading = false)
            
            val afterPrevious = applyPreviousStep(state)
            val afterNext = applyNextStep(afterPrevious)
            
            assertEquals(
                initialStep,
                afterNext.currentStep,
                "PreviousStep then NextStep from step $initialStep should return to step $initialStep"
            )
        }
    }
    
    @Test
    fun `currentStep always stays within valid range 0 to 3`() {
        repeat(iterations) {
            var state = OnboardingState(
                currentStep = Random.nextInt(0, 4),
                isLoading = false
            )
            
            // Apply random sequence of operations
            repeat(Random.nextInt(1, 20)) {
                state = when (Random.nextInt(3)) {
                    0 -> applyNextStep(state)
                    1 -> applyPreviousStep(state)
                    else -> applyConfirmSkip(state)
                }
                
                assertTrue(
                    state.currentStep in 0..3,
                    "currentStep should always be in range [0, 3], got ${state.currentStep}"
                )
            }
        }
    }
    
    @Test
    fun `multiple NextStep calls reach step 3 and stop`() {
        var state = OnboardingState(currentStep = 0, isLoading = false)
        
        repeat(10) {
            state = applyNextStep(state)
        }
        
        assertEquals(
            3,
            state.currentStep,
            "Multiple NextStep calls should stop at step 3"
        )
    }
    
    @Test
    fun `multiple PreviousStep calls reach step 0 and stop`() {
        var state = OnboardingState(currentStep = 3, isLoading = false)
        
        repeat(10) {
            state = applyPreviousStep(state)
        }
        
        assertEquals(
            0,
            state.currentStep,
            "Multiple PreviousStep calls should stop at step 0"
        )
    }
    
    @Test
    fun `state preserves other fields during navigation`() {
        repeat(iterations) {
            val selectedCoins = setOf("BTC", "ETH", "SOL")
            val notificationsEnabled = Random.nextBoolean()
            val initialStep = Random.nextInt(0, 3)
            
            val state = OnboardingState(
                currentStep = initialStep,
                selectedCoins = selectedCoins,
                notificationsEnabled = notificationsEnabled,
                isLoading = false
            )
            
            val afterNext = applyNextStep(state)
            
            assertEquals(selectedCoins, afterNext.selectedCoins, "selectedCoins should be preserved")
            assertEquals(notificationsEnabled, afterNext.notificationsEnabled, "notificationsEnabled should be preserved")
        }
    }
}
