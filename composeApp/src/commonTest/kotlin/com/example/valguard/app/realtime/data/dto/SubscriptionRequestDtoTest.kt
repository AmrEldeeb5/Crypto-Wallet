package com.example.valguard.app.realtime.data.dto

import com.example.valguard.app.realtime.TestGenerators
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll

class SubscriptionRequestDtoTest : FunSpec({

    val subscriptionRequestArb: Arb<SubscriptionRequestDto> = arbitrary {
        SubscriptionRequestDto(
            action = TestGenerators.subscriptionActionArb.bind(),
            coinIds = TestGenerators.coinIdListArb(1, 10).bind()
        )
    }

    test("Property 2: Subscription request round-trip - For any valid SubscriptionRequestDto, serializing to JSON and parsing back produces equivalent object") {
        checkAll(100, subscriptionRequestArb) { original ->
            val json = original.toJson()
            val parsed = json.toSubscriptionRequestDto()
            
            parsed shouldBe original
        }
    }
})
