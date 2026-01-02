package com.example.valguard.app.referral.presentation

data class ReferralState(
    val referralCode: String = "",
    val totalEarned: Double = 0.0,
    val pendingRewards: Double = 0.0,
    val totalReferrals: Int = 0,
    val activeReferrals: Int = 0,
    val isLoading: Boolean = false,
    val codeCopied: Boolean = false
)

sealed class ReferralEvent {
    data object LoadReferralData : ReferralEvent()
    data object CopyCode : ReferralEvent()
    data object ShareCode : ReferralEvent()
    data object DismissCopiedMessage : ReferralEvent()
}

object ReferralCodeGenerator {
    
    fun generateCode(userId: String): String {
        if (userId.isEmpty()) return "CRYPTO000000"
        
        val suffix = if (userId.length >= 6) {
            userId.takeLast(6).uppercase()
        } else {
            userId.uppercase().padStart(6, '0')
        }
        
        return "CRYPTO$suffix"
    }
    
    fun isValidCode(code: String): Boolean {
        if (code.length != 12) return false
        if (!code.startsWith("CRYPTO")) return false
        return code.substring(6).all { it.isLetterOrDigit() }
    }
}
