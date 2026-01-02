package com.example.valguard.app.compare

import com.example.valguard.app.compare.presentation.ComparisonCalculator
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PercentageDifferencePropertyTest {
    
    private val iterations = 100
    private val tolerance = 0.0001
    
    @Test
    fun `percentage difference formula is correct`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(1.0, 10000.0)
            val value2 = Random.nextDouble(1.0, 10000.0)
            
            val result = ComparisonCalculator.calculatePercentageDifference(value1, value2)
            val expected = ((value1 - value2) / value2) * 100
            
            assertEquals(expected, result, tolerance, "Percentage difference should be ((v1-v2)/v2)*100")
        }
    }
    
    @Test
    fun `same values result in zero difference`() {
        repeat(iterations) {
            val value = Random.nextDouble(0.01, 10000.0)
            val result = ComparisonCalculator.calculatePercentageDifference(value, value)
            
            assertEquals(0.0, result, tolerance, "Same values should have 0% difference")
        }
    }
    
    @Test
    fun `double value results in 100 percent difference`() {
        repeat(iterations) {
            val value = Random.nextDouble(1.0, 1000.0)
            val result = ComparisonCalculator.calculatePercentageDifference(value * 2, value)
            
            assertEquals(100.0, result, tolerance, "Double value should be 100% difference")
        }
    }
    
    @Test
    fun `half value results in negative 50 percent difference`() {
        repeat(iterations) {
            val value = Random.nextDouble(2.0, 1000.0)
            val result = ComparisonCalculator.calculatePercentageDifference(value / 2, value)
            
            assertEquals(-50.0, result, tolerance, "Half value should be -50% difference")
        }
    }
    
    @Test
    fun `zero base value returns 100 when value1 is non-zero`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(0.01, 1000.0)
            val result = ComparisonCalculator.calculatePercentageDifference(value1, 0.0)
            
            assertEquals(100.0, result, tolerance, "Non-zero vs zero should return 100%")
        }
    }
    
    @Test
    fun `both zero values result in zero difference`() {
        val result = ComparisonCalculator.calculatePercentageDifference(0.0, 0.0)
        assertEquals(0.0, result, tolerance, "Both zero should result in 0% difference")
    }
    
    @Test
    fun `positive difference when value1 greater than value2`() {
        repeat(iterations) {
            val value2 = Random.nextDouble(1.0, 1000.0)
            val value1 = value2 + Random.nextDouble(0.01, 1000.0)
            
            val result = ComparisonCalculator.calculatePercentageDifference(value1, value2)
            
            assertTrue(result > 0, "Difference should be positive when value1 > value2")
        }
    }
    
    @Test
    fun `negative difference when value1 less than value2`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(1.0, 1000.0)
            val value2 = value1 + Random.nextDouble(0.01, 1000.0)
            
            val result = ComparisonCalculator.calculatePercentageDifference(value1, value2)
            
            assertTrue(result < 0, "Difference should be negative when value1 < value2")
        }
    }
    
    @Test
    fun `absolute difference calculation is correct`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(-100.0, 100.0)
            val value2 = Random.nextDouble(-100.0, 100.0)
            
            val result = ComparisonCalculator.calculateAbsoluteDifference(value1, value2)
            val expected = value1 - value2
            
            assertEquals(expected, result, tolerance, "Absolute difference should be v1 - v2")
        }
    }
    
    @Test
    fun `absolute difference of same values is zero`() {
        repeat(iterations) {
            val value = Random.nextDouble(-1000.0, 1000.0)
            val result = ComparisonCalculator.calculateAbsoluteDifference(value, value)
            
            assertEquals(0.0, result, tolerance, "Same values should have 0 absolute difference")
        }
    }
    
    @Test
    fun `swapping values negates absolute difference`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(-100.0, 100.0)
            val value2 = Random.nextDouble(-100.0, 100.0)
            
            val diff1 = ComparisonCalculator.calculateAbsoluteDifference(value1, value2)
            val diff2 = ComparisonCalculator.calculateAbsoluteDifference(value2, value1)
            
            assertEquals(-diff1, diff2, tolerance, "Swapping values should negate the difference")
        }
    }
    
    @Test
    fun `percentage difference handles large numbers`() {
        val value1 = 1_000_000_000_000.0  // 1 trillion
        val value2 = 500_000_000_000.0    // 500 billion
        
        val result = ComparisonCalculator.calculatePercentageDifference(value1, value2)
        
        assertEquals(100.0, result, tolerance, "Should handle large numbers correctly")
    }
    
    @Test
    fun `percentage difference handles small numbers`() {
        val value1 = 0.0001
        val value2 = 0.00005
        
        val result = ComparisonCalculator.calculatePercentageDifference(value1, value2)
        
        assertEquals(100.0, result, tolerance, "Should handle small numbers correctly")
    }
    
    @Test
    fun `percentage difference is antisymmetric`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(1.0, 1000.0)
            val value2 = Random.nextDouble(1.0, 1000.0)
            
            if (value1 != value2) {
                val diff1 = ComparisonCalculator.calculatePercentageDifference(value1, value2)
                val diff2 = ComparisonCalculator.calculatePercentageDifference(value2, value1)
                
                // Note: percentage difference is not perfectly antisymmetric due to different bases
                // But the signs should be opposite
                assertTrue(
                    (diff1 > 0 && diff2 < 0) || (diff1 < 0 && diff2 > 0),
                    "Signs should be opposite when swapping values"
                )
            }
        }
    }
    
    @Test
    fun `calculation is deterministic`() {
        repeat(iterations) {
            val value1 = Random.nextDouble(1.0, 10000.0)
            val value2 = Random.nextDouble(1.0, 10000.0)
            
            val result1 = ComparisonCalculator.calculatePercentageDifference(value1, value2)
            val result2 = ComparisonCalculator.calculatePercentageDifference(value1, value2)
            
            assertEquals(result1, result2, tolerance, "Calculation should be deterministic")
        }
    }
}
