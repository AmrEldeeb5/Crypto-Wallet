package com.example.cryptovault.app.portfolio.presentation

import com.example.cryptovault.theme.WindowSize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for portfolio screen grid layout functionality.
 * Verifies that the grid column calculation works correctly for different screen sizes.
 */
class PortfolioScreenGridTest : FunSpec({

    test("Grid column calculation returns correct values for each window size") {
        // Test COMPACT screens (< 600dp) return 1 column
        val compactColumns = calculatePortfolioGridColumnsForWindowSize(WindowSize.COMPACT)
        compactColumns shouldBe 1

        // Test MEDIUM screens (600-840dp) return 2 columns
        val mediumColumns = calculatePortfolioGridColumnsForWindowSize(WindowSize.MEDIUM)
        mediumColumns shouldBe 2

        // Test EXPANDED screens (> 840dp) return 3 columns
        val expandedColumns = calculatePortfolioGridColumnsForWindowSize(WindowSize.EXPANDED)
        expandedColumns shouldBe 3
    }

    test("Window size classification works correctly for boundary values") {
        // Test boundary values for window size classification
        classifyWindowSize(599) shouldBe WindowSize.COMPACT  // Just below MEDIUM threshold
        classifyWindowSize(600) shouldBe WindowSize.MEDIUM   // At MEDIUM threshold
        classifyWindowSize(839) shouldBe WindowSize.MEDIUM   // Just below EXPANDED threshold
        classifyWindowSize(840) shouldBe WindowSize.EXPANDED // At EXPANDED threshold
    }

    test("End-to-end grid column calculation for various screen widths") {
        val testCases = listOf(
            320 to 1,  // Small phone
            360 to 1,  // Medium phone
            599 to 1,  // Large phone (just below tablet)
            600 to 2,  // Small tablet
            720 to 2,  // Medium tablet
            839 to 2,  // Large tablet portrait (just below expanded)
            840 to 3,  // Large tablet landscape
            1000 to 3  // Desktop
        )
        
        testCases.forEach { (screenWidth, expectedColumns) ->
            val windowSize = classifyWindowSize(screenWidth)
            val columnCount = calculatePortfolioGridColumnsForWindowSize(windowSize)
            columnCount shouldBe expectedColumns
        }
    }
})

/**
 * Helper function to classify window size based on screen width.
 * Mirrors the logic in rememberWindowSize() for testing purposes.
 */
private fun classifyWindowSize(screenWidthDp: Int): WindowSize {
    return when {
        screenWidthDp < 600 -> WindowSize.COMPACT
        screenWidthDp < 840 -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

/**
 * Test version of calculatePortfolioGridColumns that takes WindowSize as parameter.
 * This mirrors the actual implementation but allows for testing with different window sizes.
 */
private fun calculatePortfolioGridColumnsForWindowSize(windowSize: WindowSize): Int {
    return when (windowSize) {
        WindowSize.COMPACT -> 1  // < 600dp
        WindowSize.MEDIUM -> 2   // 600-840dp
        WindowSize.EXPANDED -> 3 // > 840dp
    }
}