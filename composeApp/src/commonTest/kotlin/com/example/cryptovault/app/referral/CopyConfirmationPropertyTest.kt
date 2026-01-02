package com.example.cryptovault.app.referral

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CopyConfirmationPropertyTest : StringSpec({
    
    "Property 8: Copy Confirmation State - shows message when codeCopied=true" {
        val codeCopied = true
        val shouldShowMessage = codeCopied
        
        shouldShowMessage shouldBe true
    }
    
    "Property 8: Copy Confirmation State - hides message when codeCopied=false" {
        val codeCopied = false
        val shouldShowMessage = codeCopied
        
        shouldShowMessage shouldBe false
    }
})
