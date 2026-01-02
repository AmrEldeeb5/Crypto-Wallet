package com.example.valguard.app.coindetail

import com.example.valguard.app.coindetail.domain.ChartTimeframe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class ChartTimeframePropertyTest : StringSpec({
    
    "Property 3: Enum Completeness (ChartTimeframe) - contains all required values" {
        val timeframes = ChartTimeframe.entries.map { it.value }
        
        timeframes.shouldContainExactlyInAnyOrder("24h", "7d", "30d", "3m", "1y", "all")
    }
    
    "Property 3: Enum Completeness (ChartTimeframe) - display names match expected values" {
        ChartTimeframe.DAY_1.displayName shouldBe "24H"
        ChartTimeframe.WEEK_1.displayName shouldBe "7D"
        ChartTimeframe.MONTH_1.displayName shouldBe "1M"
        ChartTimeframe.MONTH_3.displayName shouldBe "3M"
        ChartTimeframe.YEAR_1.displayName shouldBe "1Y"
        ChartTimeframe.ALL.displayName shouldBe "ALL"
    }
})
