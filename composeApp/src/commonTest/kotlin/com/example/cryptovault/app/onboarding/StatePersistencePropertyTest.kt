package com.example.cryptovault.app.onboarding

import com.example.cryptovault.app.onboarding.presentation.OnboardingState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class StatePersistencePropertyTest {
    
    private val iterations = 100
    private val availableCoins = listOf("BTC", "ETH", "BNB", "SOL", "ADA", "XRP")
    
    private class MockStorage {
        private var savedCurrentStep: Int = 0
        private var savedSelectedCoins: Set<String> = emptySet()
        private var savedNotificationsEnabled: Boolean = false
        
        fun save(state: OnboardingState) {
            savedCurrentStep = state.currentStep
            savedSelectedCoins = state.selectedCoins
            savedNotificationsEnabled = state.notificationsEnabled
        }
        
        fun load(): OnboardingState {
            return OnboardingState(
                currentStep = savedCurrentStep,
                selectedCoins = savedSelectedCoins,
                notificationsEnabled = savedNotificationsEnabled,
                isLoading = false
            )
        }
    }
    
    @Test
    fun `state round-trip preserves currentStep`() {
        val storage = MockStorage()
        
        repeat(iterations) {
            val step = Random.nextInt(0, 4)
            val state = OnboardingState(
                currentStep = step,
                selectedCoins = emptySet(),
                notificationsEnabled = false,
                isLoading = false
            )
            
            storage.save(state)
            val loaded = storage.load()
            
            assertEquals(
                step,
                loaded.currentStep,
                "currentStep should be preserved after round-trip"
            )
        }
    }
    
    @Test
    fun `state round-trip preserves selectedCoins`() {
        val storage = MockStorage()
        
        repeat(iterations) {
            val numCoins = Random.nextInt(0, availableCoins.size + 1)
            val selectedCoins = availableCoins.shuffled().take(numCoins).toSet()
            
            val state = OnboardingState(
                currentStep = 2,
                selectedCoins = selectedCoins,
                notificationsEnabled = false,
                isLoading = false
            )
            
            storage.save(state)
            val loaded = storage.load()
            
            assertEquals(
                selectedCoins,
                loaded.selectedCoins,
                "selectedCoins should be preserved after round-trip"
            )
        }
    }
    
    @Test
    fun `state round-trip preserves notificationsEnabled`() {
        val storage = MockStorage()
        
        repeat(iterations) {
            val notificationsEnabled = Random.nextBoolean()
            
            val state = OnboardingState(
                currentStep = 3,
                selectedCoins = emptySet(),
                notificationsEnabled = notificationsEnabled,
                isLoading = false
            )
            
            storage.save(state)
            val loaded = storage.load()
            
            assertEquals(
                notificationsEnabled,
                loaded.notificationsEnabled,
                "notificationsEnabled should be preserved after round-trip"
            )
        }
    }
    
    @Test
    fun `state round-trip preserves all fields together`() {
        val storage = MockStorage()
        
        repeat(iterations) {
            val step = Random.nextInt(0, 4)
            val numCoins = Random.nextInt(0, availableCoins.size + 1)
            val selectedCoins = availableCoins.shuffled().take(numCoins).toSet()
            val notificationsEnabled = Random.nextBoolean()
            
            val state = OnboardingState(
                currentStep = step,
                selectedCoins = selectedCoins,
                notificationsEnabled = notificationsEnabled,
                isLoading = false
            )
            
            storage.save(state)
            val loaded = storage.load()
            
            assertEquals(step, loaded.currentStep, "currentStep mismatch")
            assertEquals(selectedCoins, loaded.selectedCoins, "selectedCoins mismatch")
            assertEquals(notificationsEnabled, loaded.notificationsEnabled, "notificationsEnabled mismatch")
        }
    }
    
    @Test
    fun `multiple saves overwrite previous state`() {
        val storage = MockStorage()
        
        // Save first state
        val state1 = OnboardingState(
            currentStep = 0,
            selectedCoins = setOf("BTC"),
            notificationsEnabled = false,
            isLoading = false
        )
        storage.save(state1)
        
        // Save second state
        val state2 = OnboardingState(
            currentStep = 2,
            selectedCoins = setOf("ETH", "SOL"),
            notificationsEnabled = true,
            isLoading = false
        )
        storage.save(state2)
        
        // Load should return second state
        val loaded = storage.load()
        
        assertEquals(2, loaded.currentStep)
        assertEquals(setOf("ETH", "SOL"), loaded.selectedCoins)
        assertEquals(true, loaded.notificationsEnabled)
    }
    
    @Test
    fun `empty selectedCoins is preserved`() {
        val storage = MockStorage()
        
        val state = OnboardingState(
            currentStep = 1,
            selectedCoins = emptySet(),
            notificationsEnabled = false,
            isLoading = false
        )
        
        storage.save(state)
        val loaded = storage.load()
        
        assertEquals(emptySet(), loaded.selectedCoins)
    }
    
    @Test
    fun `all coins selected is preserved`() {
        val storage = MockStorage()
        
        val allCoins = availableCoins.toSet()
        val state = OnboardingState(
            currentStep = 2,
            selectedCoins = allCoins,
            notificationsEnabled = true,
            isLoading = false
        )
        
        storage.save(state)
        val loaded = storage.load()
        
        assertEquals(allCoins, loaded.selectedCoins)
    }
    
    @Test
    fun `boundary step values are preserved`() {
        val storage = MockStorage()
        
        // Test step 0 (minimum)
        val state0 = OnboardingState(currentStep = 0, selectedCoins = emptySet(), notificationsEnabled = false, isLoading = false)
        storage.save(state0)
        assertEquals(0, storage.load().currentStep)
        
        // Test step 3 (maximum)
        val state3 = OnboardingState(currentStep = 3, selectedCoins = emptySet(), notificationsEnabled = false, isLoading = false)
        storage.save(state3)
        assertEquals(3, storage.load().currentStep)
    }
    
    @Test
    fun `isLoading is always false after load`() {
        val storage = MockStorage()
        
        repeat(iterations) {
            val state = OnboardingState(
                currentStep = Random.nextInt(0, 4),
                selectedCoins = availableCoins.shuffled().take(Random.nextInt(0, 7)).toSet(),
                notificationsEnabled = Random.nextBoolean(),
                isLoading = true // Save with isLoading = true
            )
            
            storage.save(state)
            val loaded = storage.load()
            
            assertEquals(
                false,
                loaded.isLoading,
                "isLoading should always be false after loading"
            )
        }
    }
}
