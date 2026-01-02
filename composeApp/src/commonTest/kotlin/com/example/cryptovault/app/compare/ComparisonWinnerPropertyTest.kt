package com.example.cryptovault.app.compare

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ComparisonWinnerPropertyTest : StringSpec({
    
    "Property 6: Comparison Winner Determination - higher value wins for price" {
        checkAll(iterations = 100, Arb.double(1.0, 100000.0), Arb.double(1.0, 100000.0)) { price1, price2 ->
            val winner = determineWinner(price1, price2, higherIsBetter = true)
            winner shouldBe (price1 > price2)
        }
    }
    
    "Property 6: Comparison Winner Determination - higher value wins for marketCap" {
        checkAll(iterations = 100, Arb.double(1000000.0, 1000000000.0), Arb.double(1000000.0, 1000000000.0)) { mc1, mc2 ->
            val winner = determineWinner(mc1, mc2, higherIsBetter = true)
            winner shouldBe (mc1 > mc2)
        }
    }
    
    "Property 6: Comparison Winner Determination - higher value wins for volume" {
        checkAll(iterations = 100, Arb.double(100000.0, 10000000.0), Arb.double(100000.0, 10000000.0)) { vol1, vol2 ->
            val winner = determineWinner(vol1, vol2, higherIsBetter = true)
            winner shouldBe (vol1 > vol2)
        }
    }
    
    "Property 6: Comparison Winner Determination - lower value wins for rank" {
        checkAll(iterations = 100, Arb.int(1, 1000), Arb.int(1, 1000)) { rank1, rank2 ->
            val winner = determineWinner(rank1.toDouble(), rank2.toDouble(), higherIsBetter = false)
            winner shouldBe (rank1 < rank2)
        }
    }
})

/**
 * Determines if value1 wins the comparison
 */
fun determineWinner(value1: Double, value2: Double, higherIsBetter: Boolean): Boolean {
    return if (higherIsBetter) value1 > value2 else value1 < value2
}
