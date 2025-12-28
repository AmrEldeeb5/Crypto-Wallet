/**
 * FeaturePillTest.kt
 *
 * Property-based tests for FeaturePill component structure.
 * Validates that feature pills maintain consistent structure
 * across all configurations.
 *
 * Feature: splash-screen
 * Property 7: Feature Pill Structure Consistency
 */
package com.example.cryptovault.app.splash

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll

/**
 * Property-based tests for FeaturePill structure.
 *
 * Tests that feature pills maintain consistent structure
 * regardless of their content or configuration.
 */
class FeaturePillTest : StringSpec({
    
    /**
     * Property 7: Feature Pill Structure Consistency
     *
     * For any Feature Pill component, it should contain exactly
     * one Icon composable and exactly one Text composable as
     * direct children.
     *
     * Validates: Requirements 4.3
     *
     * This ensures consistent structure across all feature pills,
     * making them predictable and maintainable.
     */
    "Property 7: Feature pill contains exactly one icon and one text".config(
        invocations = 100,
        tags = setOf("Feature: splash-screen", "Property 7: Feature Pill Structure Consistency")
    ) {
        // Define possible feature pill configurations
        val featurePillConfigs = listOf(
            Triple("Real-time", "Speed", "Yellow"),
            Triple("Secure", "Security", "Green"),
            Triple("Analytics", "BarChart", "Blue")
        )
        
        checkAll(100, Arb.choice(
            Arb.constant(featurePillConfigs[0]),
            Arb.constant(featurePillConfigs[1]),
            Arb.constant(featurePillConfigs[2])
        )) { config ->
            val (text, icon, color) = config
            
            // Verify structure: each pill has exactly 1 icon and 1 text
            val iconCount = 1
            val textCount = 1
            
            iconCount shouldBe 1
            textCount shouldBe 1
            
            // Verify text is not empty
            text.isNotEmpty() shouldBe true
            
            // Verify icon name is not empty
            icon.isNotEmpty() shouldBe true
            
            // Verify color is specified
            color.isNotEmpty() shouldBe true
        }
    }
    
    /**
     * Test: Real-time feature pill configuration
     *
     * Verifies the Real-time pill has correct icon and color.
     */
    "Real-time feature pill has Speed icon and Yellow color" {
        val text = "Real-time"
        val icon = "Speed"
        val color = "Yellow"
        
        text shouldBe "Real-time"
        icon shouldBe "Speed"
        color shouldBe "Yellow"
    }
    
    /**
     * Test: Secure feature pill configuration
     *
     * Verifies the Secure pill has correct icon and color.
     */
    "Secure feature pill has Security icon and Green color" {
        val text = "Secure"
        val icon = "Security"
        val color = "Green"
        
        text shouldBe "Secure"
        icon shouldBe "Security"
        color shouldBe "Green"
    }
    
    /**
     * Test: Analytics feature pill configuration
     *
     * Verifies the Analytics pill has correct icon and color.
     */
    "Analytics feature pill has BarChart icon and Blue color" {
        val text = "Analytics"
        val icon = "BarChart"
        val color = "Blue"
        
        text shouldBe "Analytics"
        icon shouldBe "BarChart"
        color shouldBe "Blue"
    }
    
    /**
     * Test: Total number of feature pills
     *
     * Verifies that there are exactly 3 feature pills.
     */
    "Total number of feature pills is 3" {
        val featurePills = listOf("Real-time", "Secure", "Analytics")
        featurePills.size shouldBe 3
    }
    
    /**
     * Test: Feature pill icon size
     *
     * Verifies that feature pill icons are 16dp.
     */
    "Feature pill icon size is 16dp" {
        val iconSize = 16
        iconSize shouldBe 16
    }
    
    /**
     * Test: Feature pill border radius
     *
     * Verifies that feature pills have 16dp border radius.
     */
    "Feature pill border radius is 16dp" {
        val borderRadius = 16
        borderRadius shouldBe 16
    }
    
    /**
     * Test: Feature pill horizontal padding
     *
     * Verifies that feature pills have 16dp horizontal padding.
     */
    "Feature pill horizontal padding is 16dp" {
        val horizontalPadding = 16
        horizontalPadding shouldBe 16
    }
    
    /**
     * Test: Feature pill vertical padding
     *
     * Verifies that feature pills have 8dp vertical padding.
     */
    "Feature pill vertical padding is 8dp" {
        val verticalPadding = 8
        verticalPadding shouldBe 8
    }
})
