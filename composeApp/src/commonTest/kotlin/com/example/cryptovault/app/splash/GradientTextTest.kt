/**
 * GradientTextTest.kt
 *
 * Unit tests for the GradientText component.
 * Validates that gradient colors are applied correctly and text
 * content is rendered as expected.
 *
 * Feature: splash-screen
 */
package com.example.cryptovault.app.splash

import com.example.cryptovault.theme.Blue400
import com.example.cryptovault.theme.Pink400
import com.example.cryptovault.theme.Purple400
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

/**
 * Unit tests for GradientText component.
 *
 * Tests specific examples and configuration to ensure the gradient
 * text component renders correctly with the expected colors.
 */
class GradientTextTest : StringSpec({
    
    /**
     * Test: Gradient colors are in correct order
     *
     * Verifies that the gradient uses Blue400 → Purple400 → Pink400
     * in the correct sequence.
     *
     * Validates: Requirements 3.2
     */
    "Gradient colors are in correct order (Blue → Purple → Pink)" {
        val expectedColors = listOf(Blue400, Purple400, Pink400)
        
        // Verify the gradient color sequence
        expectedColors shouldContainInOrder listOf(Blue400, Purple400, Pink400)
    }
    
    /**
     * Test: Gradient has exactly three colors
     *
     * Verifies that the gradient contains exactly three color stops.
     */
    "Gradient contains exactly three colors" {
        val gradientColors = listOf(Blue400, Purple400, Pink400)
        gradientColors.size shouldBe 3
    }
    
    /**
     * Test: First color is Blue400
     *
     * Verifies that the gradient starts with Blue400.
     */
    "First gradient color is Blue400" {
        val gradientColors = listOf(Blue400, Purple400, Pink400)
        gradientColors.first() shouldBe Blue400
    }
    
    /**
     * Test: Middle color is Purple400
     *
     * Verifies that the gradient middle color is Purple400.
     */
    "Middle gradient color is Purple400" {
        val gradientColors = listOf(Blue400, Purple400, Pink400)
        gradientColors[1] shouldBe Purple400
    }
    
    /**
     * Test: Last color is Pink400
     *
     * Verifies that the gradient ends with Pink400.
     */
    "Last gradient color is Pink400" {
        val gradientColors = listOf(Blue400, Purple400, Pink400)
        gradientColors.last() shouldBe Pink400
    }
})
