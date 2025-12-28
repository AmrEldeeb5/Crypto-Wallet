package com.example.cryptovault.app.realtime.data.dto

import com.example.cryptovault.app.realtime.TestGenerators
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll

class PriceUpdateMessageDtoTest : FunSpec({

    val priceUpdateMessageArb: Arb<PriceUpdateMessageDto> = arbitrary {
        PriceUpdateMessageDto(
            type = TestGenerators.messageTypeArb.bind(),
            coinId = TestGenerators.simpleCoinIdArb.bind(),
            price = TestGenerators.priceStringArb.bind(),
            timestamp = TestGenerators.timestampArb.bind()
        )
    }

    test("Property 1: Message serialization round-trip - For any valid PriceUpdateMessageDto, serializing to JSON and parsing back produces equivalent object") {
        checkAll(100, priceUpdateMessageArb) { original ->
            val json = original.toJson()
            val parsed = json.toPriceUpdateMessageDto()
            
            parsed shouldBe original
        }
    }
})
