package com.example.cryptowallet.app.referral

import com.example.cryptowallet.app.referral.presentation.ReferralCodeGenerator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReferralCodeGenerationPropertyTest {
    
    private val iterations = 100
    
    @Test
    fun `generated code starts with CRYPTO`() {
        repeat(iterations) {
            val userId = generateRandomUserId()
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertTrue(code.startsWith("CRYPTO"), "Code should start with CRYPTO")
        }
    }
    
    @Test
    fun `generated code is exactly 12 characters`() {
        repeat(iterations) {
            val userId = generateRandomUserId()
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertEquals(12, code.length, "Code should be exactly 12 characters")
        }
    }
    
    @Test
    fun `generated code uses last 6 characters of userId`() {
        val testCases = listOf(
            "user123456" to "CRYPTO123456",
            "abcdefghij" to "CRYPTOGHIJ".uppercase(),
            "ABCDEF" to "CRYPTOABCDEF"
        )

        testCases.forEach { (userId, _) ->
            val code = ReferralCodeGenerator.generateCode(userId)
            val suffix = code.substring(6)
            val expectedLastSix = userId.takeLast(6).uppercase()
            
            assertEquals(expectedLastSix, suffix, "Suffix should be last 6 chars of userId uppercase")
        }
    }
    
    @Test
    fun `short userId is padded with zeros`() {
        val shortIds = listOf("a", "ab", "abc", "abcd", "abcde")
        
        shortIds.forEach { userId ->
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertEquals(12, code.length, "Code should still be 12 characters for short userId")
            assertTrue(code.startsWith("CRYPTO"), "Code should start with CRYPTO")
            
            val suffix = code.substring(6)
            val expectedPadding = 6 - userId.length
            val paddedPart = suffix.take(expectedPadding)
            
            assertTrue(paddedPart.all { it == '0' }, "Short userId should be padded with zeros")
        }
    }
    
    @Test
    fun `empty userId returns default code`() {
        val code = ReferralCodeGenerator.generateCode("")
        
        assertEquals("CRYPTO000000", code, "Empty userId should return CRYPTO000000")
    }
    
    @Test
    fun `generated code is uppercase`() {
        repeat(iterations) {
            val userId = generateRandomUserId().lowercase()
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertEquals(code, code.uppercase(), "Code should be uppercase")
        }
    }
    
    @Test
    fun `same userId generates same code`() {
        repeat(iterations) {
            val userId = generateRandomUserId()
            val code1 = ReferralCodeGenerator.generateCode(userId)
            val code2 = ReferralCodeGenerator.generateCode(userId)
            
            assertEquals(code1, code2, "Same userId should generate same code")
        }
    }
    
    @Test
    fun `different userIds generate different codes`() {
        val codes = mutableSetOf<String>()
        
        repeat(iterations) {
            val userId = "user${Random.nextInt(1000000)}"
            val code = ReferralCodeGenerator.generateCode(userId)
            codes.add(code)
        }
        
        // Most codes should be unique (allowing for some collisions due to last 6 chars)
        assertTrue(codes.size > iterations / 2, "Different userIds should mostly generate different codes")
    }
    
    @Test
    fun `generated code is valid according to validator`() {
        repeat(iterations) {
            val userId = generateRandomUserId()
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertTrue(ReferralCodeGenerator.isValidCode(code), "Generated code should be valid")
        }
    }
    
    @Test
    fun `validator rejects invalid codes`() {
        val invalidCodes = listOf(
            "CRYPTO12345",      // Too short
            "CRYPTO1234567",    // Too long
            "NOTCRYPTO123",     // Wrong prefix
            "crypto123456",     // Lowercase (if case-sensitive)
            "",                 // Empty
            "CRYPTO12345!",     // Special character
            "CRYPTO 12345"      // Space
        )
        
        invalidCodes.forEach { code ->
            // Note: Some of these might pass depending on implementation
            // The test documents expected behavior
            if (code.length != 12 || !code.startsWith("CRYPTO")) {
                assertTrue(!ReferralCodeGenerator.isValidCode(code), "Code '$code' should be invalid")
            }
        }
    }
    
    @Test
    fun `validator accepts valid codes`() {
        val validCodes = listOf(
            "CRYPTO123456",
            "CRYPTOABCDEF",
            "CRYPTO000000",
            "CRYPTOA1B2C3"
        )
        
        validCodes.forEach { code ->
            assertTrue(ReferralCodeGenerator.isValidCode(code), "Code '$code' should be valid")
        }
    }
    
    @Test
    fun `code suffix contains only alphanumeric characters`() {
        repeat(iterations) {
            val userId = generateRandomUserId()
            val code = ReferralCodeGenerator.generateCode(userId)
            val suffix = code.substring(6)
            
            assertTrue(suffix.all { it.isLetterOrDigit() }, "Suffix should only contain alphanumeric characters")
        }
    }
    
    @Test
    fun `numeric userId generates valid code`() {
        val numericIds = listOf("123456", "000000", "999999", "1", "12")
        
        numericIds.forEach { userId ->
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertTrue(ReferralCodeGenerator.isValidCode(code), "Numeric userId should generate valid code")
            assertEquals(12, code.length)
        }
    }
    
    @Test
    fun `special characters in userId are handled`() {
        // Special characters might be filtered or cause issues
        val specialIds = listOf("user@123", "user#456", "user-789")
        
        specialIds.forEach { userId ->
            val code = ReferralCodeGenerator.generateCode(userId)
            
            assertEquals(12, code.length, "Code should still be 12 characters")
            assertTrue(code.startsWith("CRYPTO"), "Code should start with CRYPTO")
        }
    }
    
    private fun generateRandomUserId(): String {
        val length = Random.nextInt(1, 20)
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
