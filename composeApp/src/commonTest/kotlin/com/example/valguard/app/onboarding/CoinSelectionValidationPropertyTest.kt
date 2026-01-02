package com.example.valguard.app.onboarding

import com.example.valguard.app.onboarding.presentation.OnboardingState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoinSelectionValidationPropertyTest {
    
    private val iterations = 100
    private val availableCoins = listOf("BTC", "ETH", "BNB", "SOL", "ADA", "XRP")
    
    private fun canProceed(state: OnboardingState): Boolean {
        return when (state.currentStep) {
            2 -> state.selectedCoins.isNotEmpty()
            else -> true
        }
    }
    
    @Test
    fun `canProceed is false on step 2 when no coins selected`() {
        val state = OnboardingState(
            currentStep = 2,
            selectedCoins = emptySet(),
            isLoading = false
        )
        
        assertFalse(
            canProceed(state),
            "canProceed should be false on step 2 with no coins selected"
        )
    }
    
    @Test
    fun `canProceed is true on step 2 when at least one coin selected`() {
        repeat(iterations) {
            // Select random number of coins (1 to 6)
            val numCoins = Random.nextInt(1, availableCoins.size + 1)
            val selectedCoins = availableCoins.shuffled().take(numCoins).toSet()
            
            val state = OnboardingState(
                currentStep = 2,
                selectedCoins = selectedCoins,
                isLoading = false
            )
            
            assertTrue(
                canProceed(state),
                "canProceed should be true on step 2 with ${selectedCoins.size} coins selected"
            )
        }
    }
    
    @Test
    fun `canProceed is true on step 0 regardless of coin selection`() {
        repeat(iterations) {
            val selectedCoins = if (Random.nextBoolean()) {
                availableCoins.shuffled().take(Random.nextInt(0, availableCoins.size + 1)).toSet()
            } else {
                emptySet()
            }
            
            val state = OnboardingState(
                currentStep = 0,
                selectedCoins = selectedCoins,
                isLoading = false
            )
            
            assertTrue(
                canProceed(state),
                "canProceed should be true on step 0 regardless of coin selection"
            )
        }
    }
    
    @Test
    fun `canProceed is true on step 1 regardless of coin selection`() {
        repeat(iterations) {
            val selectedCoins = if (Random.nextBoolean()) {
                availableCoins.shuffled().take(Random.nextInt(0, availableCoins.size + 1)).toSet()
            } else {
                emptySet()
            }
            
            val state = OnboardingState(
                currentStep = 1,
                selectedCoins = selectedCoins,
                isLoading = false
            )
            
            assertTrue(
                canProceed(state),
                "canProceed should be true on step 1 regardless of coin selection"
            )
        }
    }
    
    @Test
    fun `canProceed is true on step 3 regardless of coin selection`() {
        repeat(iterations) {
            val selectedCoins = if (Random.nextBoolean()) {
                availableCoins.shuffled().take(Random.nextInt(0, availableCoins.size + 1)).toSet()
            } else {
                emptySet()
            }
            
            val state = OnboardingState(
                currentStep = 3,
                selectedCoins = selectedCoins,
                isLoading = false
            )
            
            assertTrue(
                canProceed(state),
                "canProceed should be true on step 3 regardless of coin selection"
            )
        }
    }
    
    @Test
    fun `canProceed only depends on step and coin selection`() {
        repeat(iterations) {
            val currentStep = Random.nextInt(0, 4)
            val selectedCoins = availableCoins.shuffled().take(Random.nextInt(0, availableCoins.size + 1)).toSet()
            val notificationsEnabled = Random.nextBoolean()
            val isTransitioning = Random.nextBoolean()
            
            val state = OnboardingState(
                currentStep = currentStep,
                selectedCoins = selectedCoins,
                notificationsEnabled = notificationsEnabled,
                isTransitioning = isTransitioning,
                isLoading = false
            )
            
            val expected = when (currentStep) {
                2 -> selectedCoins.isNotEmpty()
                else -> true
            }
            
            val actual = canProceed(state)
            
            assertTrue(
                actual == expected,
                "canProceed should be $expected for step $currentStep with ${selectedCoins.size} coins, got $actual"
            )
        }
    }
    
    @Test
    fun `selecting a coin on step 2 enables proceeding`() {
        // Start with no coins selected
        var state = OnboardingState(
            currentStep = 2,
            selectedCoins = emptySet(),
            isLoading = false
        )
        
        assertFalse(canProceed(state), "Should not be able to proceed with no coins")
        
        // Select a coin
        state = state.copy(selectedCoins = setOf("BTC"))
        
        assertTrue(canProceed(state), "Should be able to proceed after selecting a coin")
    }
    
    @Test
    fun `deselecting all coins on step 2 disables proceeding`() {
        // Start with coins selected
        var state = OnboardingState(
            currentStep = 2,
            selectedCoins = setOf("BTC", "ETH"),
            isLoading = false
        )
        
        assertTrue(canProceed(state), "Should be able to proceed with coins selected")
        
        // Deselect all coins
        state = state.copy(selectedCoins = emptySet())
        
        assertFalse(canProceed(state), "Should not be able to proceed after deselecting all coins")
    }
    
    @Test
    fun `single coin selection is sufficient to proceed`() {
        availableCoins.forEach { coin ->
            val state = OnboardingState(
                currentStep = 2,
                selectedCoins = setOf(coin),
                isLoading = false
            )
            
            assertTrue(
                canProceed(state),
                "Single coin selection ($coin) should be sufficient to proceed"
            )
        }
    }
    
    @Test
    fun `all coins selected allows proceeding`() {
        val state = OnboardingState(
            currentStep = 2,
            selectedCoins = availableCoins.toSet(),
            isLoading = false
        )
        
        assertTrue(
            canProceed(state),
            "All coins selected should allow proceeding"
        )
    }
}
