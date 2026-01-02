package com.example.valguard.app.realtime

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern

object TestGenerators {

    val coinIdArb: Arb<String> = Arb.stringPattern("[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}")

    val simpleCoinIdArb: Arb<String> = Arb.stringPattern("[a-z]{3,10}")

    val priceArb: Arb<Double> = Arb.double(min = 0.00001, max = 1_000_000.0)

    val timestampArb: Arb<Long> = Arb.long(
        min = 1700000000000L, // ~Nov 2023
        max = 2000000000000L  // ~May 2033
    )

    fun coinIdListArb(minSize: Int = 1, maxSize: Int = 10): Arb<List<String>> =
        Arb.list(simpleCoinIdArb, minSize..maxSize)

    val screenIdArb: Arb<String> = Arb.stringPattern("screen_[a-z]{5,10}")

    val priceStringArb: Arb<String> = arbitrary {
        val price = priceArb.bind()
        price.toString()
    }

    val messageTypeArb: Arb<String> = arbitrary {
        listOf("price_update", "ticker", "trade").random()
    }

    val subscriptionActionArb: Arb<String> = arbitrary {
        listOf("subscribe", "unsubscribe").random()
    }
}
