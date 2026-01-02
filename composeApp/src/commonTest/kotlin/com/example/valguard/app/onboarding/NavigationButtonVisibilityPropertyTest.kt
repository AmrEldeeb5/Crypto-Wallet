package com.example.valguard.app.onboarding

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationButtonVisibilityPropertyTest {
    
    private fun isBackButtonVisible(currentStep: Int): Boolean {
        return currentStep > 0
    }
    
    private fun isSkipButtonVisible(currentStep: Int): Boolean {
        return currentStep < 3
    }
    
    @Test
    fun `back button is hidden on step 0`() {
        assertFalse(
            isBackButtonVisible(0),
            "Back button should be hidden on step 0"
        )
    }
    
    @Test
    fun `back button is visible on step 1`() {
        assertTrue(
            isBackButtonVisible(1),
            "Back button should be visible on step 1"
        )
    }
    
    @Test
    fun `back button is visible on step 2`() {
        assertTrue(
            isBackButtonVisible(2),
            "Back button should be visible on step 2"
        )
    }
    
    @Test
    fun `back button is visible on step 3`() {
        assertTrue(
            isBackButtonVisible(3),
            "Back button should be visible on step 3"
        )
    }
    
    @Test
    fun `skip button is visible on step 0`() {
        assertTrue(
            isSkipButtonVisible(0),
            "Skip button should be visible on step 0"
        )
    }
    
    @Test
    fun `skip button is visible on step 1`() {
        assertTrue(
            isSkipButtonVisible(1),
            "Skip button should be visible on step 1"
        )
    }
    
    @Test
    fun `skip button is visible on step 2`() {
        assertTrue(
            isSkipButtonVisible(2),
            "Skip button should be visible on step 2"
        )
    }
    
    @Test
    fun `skip button is hidden on step 3`() {
        assertFalse(
            isSkipButtonVisible(3),
            "Skip button should be hidden on step 3"
        )
    }
    
    @Test
    fun `back button visibility follows step greater than 0 rule`() {
        for (step in 0..3) {
            val expected = step > 0
            val actual = isBackButtonVisible(step)
            assertTrue(
                actual == expected,
                "Back button visibility for step $step should be $expected, got $actual"
            )
        }
    }
    
    @Test
    fun `skip button visibility follows step less than 3 rule`() {
        for (step in 0..3) {
            val expected = step < 3
            val actual = isSkipButtonVisible(step)
            assertTrue(
                actual == expected,
                "Skip button visibility for step $step should be $expected, got $actual"
            )
        }
    }
    
    @Test
    fun `first step has skip but no back`() {
        assertFalse(isBackButtonVisible(0))
        assertTrue(isSkipButtonVisible(0))
    }
    
    @Test
    fun `last step has back but no skip`() {
        assertTrue(isBackButtonVisible(3))
        assertFalse(isSkipButtonVisible(3))
    }
    
    @Test
    fun `middle steps have both buttons`() {
        for (step in 1..2) {
            assertTrue(
                isBackButtonVisible(step),
                "Step $step should have back button"
            )
            assertTrue(
                isSkipButtonVisible(step),
                "Step $step should have skip button"
            )
        }
    }
}
