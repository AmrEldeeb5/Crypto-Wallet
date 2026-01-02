package com.example.valguard.app.coindetail

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll

class PriceChangeFormattingPropertyTest : StringSpec({
    
    "Property 1: Price Change Formatting - positive values have + prefix" {
        checkAll(iterations = 100, Arb.double(0.01, 1000.0)) { change ->
            val formatted = formatPriceChange(change)
            formatted.shouldStartWith("+")
        }
    }
    
    "Property 1: Price Change Formatting - negative values have no + prefix" {
        checkAll(iterations = 100, Arb.double(-1000.0, -0.01)) { change ->
            val formatted = formatPriceChange(change)
            formatted.first() shouldBe '-'
        }
    }
    
    "Property 1: Price Change Formatting - zero has + prefix" {
        val formatted = formatPriceChange(0.0)
        formatted.shouldStartWith("+")
    }
})

/**
 * Formats price change with + prefix for positive values
 */
fun formatPriceChange(change: Double): String {
    val prefix = if (change >= 0) "+" else ""
    return "$prefix${String.format("%.2f", change)}%"
}
