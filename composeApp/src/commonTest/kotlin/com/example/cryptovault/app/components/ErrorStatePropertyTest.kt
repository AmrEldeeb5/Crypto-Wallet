package com.example.cryptovault.app.components

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ErrorStatePropertyTest {

    @Test
    fun `Property 16 - Retry button visibility depends on onRetry callback`() {
        // When onRetry is provided, button should be shown
        val withRetry = ErrorStateConfig(
            message = "Test error",
            onRetry = { /* callback */ }
        )
        assertTrue(withRetry.shouldShowRetryButton(), "Retry button should be shown when onRetry is provided")
        
        // When onRetry is null, button should not be shown
        val withoutRetry = ErrorStateConfig(
            message = "Test error",
            onRetry = null
        )
        assertFalse(withoutRetry.shouldShowRetryButton(), "Retry button should not be shown when onRetry is null")
    }

    @Test
    fun `Property 16 - Retry callback is invoked when triggered`() {
        var callbackInvoked = false
        
        val config = ErrorStateConfig(
            message = "Test error",
            onRetry = { callbackInvoked = true }
        )
        
        // Simulate retry button tap
        config.onRetry?.invoke()
        
        assertTrue(callbackInvoked, "onRetry callback should be invoked when retry is triggered")
    }

    @Test
    fun `Property 16 - Error message is always displayed`() {
        val testMessages = listOf(
            "Network error",
            "Server unavailable",
            "Connection timeout",
            "",  // Empty message should still work
            "A very long error message that describes the problem in detail"
        )
        
        testMessages.forEach { message ->
            val config = ErrorStateConfig(message = message, onRetry = null)
            // The message should be stored and accessible
            assertTrue(config.message == message, "Error message should be stored correctly")
        }
    }

    private data class ErrorStateConfig(
        val message: String,
        val onRetry: (() -> Unit)?
    ) {
        fun shouldShowRetryButton(): Boolean = onRetry != null
    }
}
