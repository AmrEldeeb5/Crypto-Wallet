package com.example.cryptovault.app.components

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class BarChartPropertyTest : StringSpec({
    
    "Property 2: Bar Height Normalization - all heights in [0, 1] range" {
        checkAll(iterations = 100, Arb.list(Arb.double(0.0, 1000000.0), 1..50)) { values ->
            val normalized = normalizeBarHeights(values)
            
            normalized.forEach { height ->
                height.toDouble().shouldBeBetween(0.0, 1.0, 0.0001)
            }
        }
    }
    
    "Property 2: Bar Height Normalization - max value becomes 1.0" {
        checkAll(iterations = 100, Arb.list(Arb.double(1.0, 1000000.0), 2..50)) { values ->
            val normalized = normalizeBarHeights(values)
            val maxNormalized = normalized.maxOrNull() ?: 0f
            
            maxNormalized.toDouble().shouldBeBetween(0.999, 1.0, 0.0)
        }
    }
    
    "Property 2: Bar Height Normalization - empty list returns empty" {
        normalizeBarHeights(emptyList()) shouldBe emptyList()
    }
    
    "Property 2: Bar Height Normalization - all zeros returns all zeros" {
        val result = normalizeBarHeights(listOf(0.0, 0.0, 0.0))
        result shouldBe listOf(0f, 0f, 0f)
    }
})
