package com.example.valguard.app.referral

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll

class AmountFormattingPropertyTest : StringSpec({
    
    "Property 7: Amount Formatting - matches $X.XX pattern" {
        checkAll(iterations = 100, Arb.double(0.0, 1000000.0)) { amount ->
            val formatted = formatAmount(amount)
            formatted.shouldMatch(Regex("""\$\d+\.\d{2}"""))
        }
    }
})

/**
 * Formats amount as $X.XX
 */
fun formatAmount(amount: Double): String {
    return String.format("$%.2f", amount)
}
