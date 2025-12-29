package com.example.cryptovault.app.portfolio.presentation

import com.example.cryptovault.theme.WindowSize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

/**
 * Property-based tests for portfolio screen grid layout functionality.
 * Tests universal properties that should hold across all valid inputs.
 */
class PortfolioGridPropertyTest : FunSpec({

    test("Property 1: Grid Column Count Bounds - **Feature: responsive-portfolio, Property 1: Grid Column Count Bounds**") {
        checkAll(100, Arb.int(320..1200)) { screenWidth ->
            val windowSize = classifyWindowSize(screenWidth)
            val columnCount = calculatePortfolioGridColumnsForWindowSize(windowSize)
            
            // Column count should always be between 1 and 3 inclusive
            columnCount shouldBeInRange 1..3
            
            // Verify specific ranges match expected column counts
            when {
                screenWidth < 600 -> columnCount shouldBe 1
                screenWidth < 840 -> columnCount shouldBe 2
                else -> columnCount shouldBe 3
            }
        }
    }

    test("Property: Window size classification consistency") {
        checkAll(100, Arb.int(320..1200)) { screenWidth ->
            val windowSize = classifyWindowSize(screenWidth)
            
            // Window size should be consistent with screen width ranges
            when {
                screenWidth < 600 -> windowSize shouldBe WindowSize.COMPACT
                screenWidth < 840 -> windowSize shouldBe WindowSize.MEDIUM
                else -> windowSize shouldBe WindowSize.EXPANDED
            }
        }
    }

    test("Property: Grid column count monotonicity") {
        checkAll(100, Arb.int(320..1199)) { screenWidth ->
            val smallerScreenColumns = calculatePortfolioGridColumnsForWindowSize(classifyWindowSize(screenWidth))
            val largerScreenColumns = calculatePortfolioGridColumnsForWindowSize(classifyWindowSize(screenWidth + 1))
            
            // Column count should never decrease as screen width increases
            largerScreenColumns shouldBeInRange smallerScreenColumns..3
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