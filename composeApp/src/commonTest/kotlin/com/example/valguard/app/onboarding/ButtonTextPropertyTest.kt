package com.example.valguard.app.onboarding

import com.example.valguard.app.onboarding.presentation.components.getButtonText
import kotlin.test.Test
import kotlin.test.assertEquals

class ButtonTextPropertyTest {
    
    @Test
    fun `step 0 shows Continue`() {
        val text = getButtonText(currentStep = 0)
        assertEquals("Continue", text, "Step 0 should show 'Continue'")
    }
    
    @Test
    fun `step 1 shows Continue`() {
        val text = getButtonText(currentStep = 1)
        assertEquals("Continue", text, "Step 1 should show 'Continue'")
    }
    
    @Test
    fun `step 2 shows Continue`() {
        val text = getButtonText(currentStep = 2)
        assertEquals("Continue", text, "Step 2 should show 'Continue'")
    }
    
    @Test
    fun `step 3 shows Get Started`() {
        val text = getButtonText(currentStep = 3)
        assertEquals("Get Started", text, "Step 3 should show 'Get Started'")
    }
    
    @Test
    fun `only final step shows Get Started`() {
        for (step in 0..2) {
            val text = getButtonText(currentStep = step)
            assertEquals(
                "Continue",
                text,
                "Non-final step $step should show 'Continue'"
            )
        }
        
        val finalText = getButtonText(currentStep = 3)
        assertEquals(
            "Get Started",
            finalText,
            "Final step should show 'Get Started'"
        )
    }
    
    @Test
    fun `button text is never empty`() {
        for (step in 0..3) {
            val text = getButtonText(currentStep = step)
            assert(text.isNotEmpty()) { "Button text should never be empty for step $step" }
        }
    }
    
    @Test
    fun `button text changes only on final step`() {
        val texts = (0..3).map { getButtonText(it) }
        
        // First 3 should be the same
        assertEquals(texts[0], texts[1], "Steps 0 and 1 should have same text")
        assertEquals(texts[1], texts[2], "Steps 1 and 2 should have same text")
        
        // Last should be different
        assert(texts[2] != texts[3]) { "Step 3 should have different text than step 2" }
    }
}
