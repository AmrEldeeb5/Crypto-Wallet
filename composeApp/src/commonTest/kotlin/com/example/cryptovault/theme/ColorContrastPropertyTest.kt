package com.example.cryptovault.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertTrue

class ColorContrastPropertyTest {

    companion object {
        // WCAG AA minimum contrast ratios
        const val NORMAL_TEXT_MIN_CONTRAST = 4.5
        const val LARGE_TEXT_MIN_CONTRAST = 3.0
        
        fun relativeLuminance(color: Color): Double {
            fun adjustChannel(channel: Float): Double {
                return if (channel <= 0.03928) {
                    channel / 12.92
                } else {
                    Math.pow(((channel + 0.055) / 1.055).toDouble(), 2.4)
                }
            }
            
            val r = adjustChannel(color.red)
            val g = adjustChannel(color.green)
            val b = adjustChannel(color.blue)
            
            return 0.2126 * r + 0.7152 * g + 0.0722 * b
        }
        
        fun contrastRatio(foreground: Color, background: Color): Double {
            val l1 = relativeLuminance(foreground)
            val l2 = relativeLuminance(background)
            
            val lighter = max(l1, l2)
            val darker = min(l1, l2)
            
            return (lighter + 0.05) / (darker + 0.05)
        }
    }

    @Test
    fun `Property 19 - Light theme primary text on card background meets WCAG AA`() {
        val contrast = contrastRatio(
            LightCryptoColors.textPrimary,
            LightCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Light theme: Primary text on card background contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme primary text on card background meets WCAG AA`() {
        val contrast = contrastRatio(
            DarkCryptoColors.textPrimary,
            DarkCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Dark theme: Primary text on card background contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme secondary text on card background meets WCAG AA`() {
        val contrast = contrastRatio(
            LightCryptoColors.textSecondary,
            LightCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Light theme: Secondary text on card background contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme secondary text on card background meets WCAG AA`() {
        val contrast = contrastRatio(
            DarkCryptoColors.textSecondary,
            DarkCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Dark theme: Secondary text on card background contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme primary text on elevated background meets WCAG AA`() {
        val contrast = contrastRatio(
            LightCryptoColors.textPrimary,
            LightCryptoColors.cardBackgroundElevated
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Light theme: Primary text on elevated background contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme primary text on elevated background meets WCAG AA`() {
        val contrast = contrastRatio(
            DarkCryptoColors.textPrimary,
            DarkCryptoColors.cardBackgroundElevated
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Dark theme: Primary text on elevated background contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme profit color on card background meets large text contrast`() {
        val contrast = contrastRatio(
            LightCryptoColors.profit,
            LightCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Light theme: Profit color on card background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme profit color on card background meets large text contrast`() {
        val contrast = contrastRatio(
            DarkCryptoColors.profit,
            DarkCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Dark theme: Profit color on card background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme loss color on card background meets large text contrast`() {
        val contrast = contrastRatio(
            LightCryptoColors.loss,
            LightCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Light theme: Loss color on card background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme loss color on card background meets large text contrast`() {
        val contrast = contrastRatio(
            DarkCryptoColors.loss,
            DarkCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Dark theme: Loss color on card background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme error color on card background meets large text contrast`() {
        val contrast = contrastRatio(
            LightCryptoColors.statusError,
            LightCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Light theme: Error color on card background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme error color on card background meets large text contrast`() {
        val contrast = contrastRatio(
            DarkCryptoColors.statusError,
            DarkCryptoColors.cardBackground
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Dark theme: Error color on card background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme white text on button primary meets WCAG AA`() {
        // Button text is typically white (textPrimary in dark contexts)
        val whiteText = Color(0xFFFFFFFF)
        val contrast = contrastRatio(
            whiteText,
            LightCryptoColors.buttonPrimary
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Light theme: White text on button primary contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme white text on button primary meets WCAG AA`() {
        // Button text is typically white (textPrimary)
        val whiteText = Color(0xFFFFFFFF)
        val contrast = contrastRatio(
            whiteText,
            DarkCryptoColors.buttonPrimary
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Dark theme: White text on button primary contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Light theme tertiary text on elevated background meets large text contrast`() {
        val contrast = contrastRatio(
            LightCryptoColors.textTertiary,
            LightCryptoColors.cardBackgroundElevated
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Light theme: Tertiary text on elevated background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme tertiary text on elevated background meets large text contrast`() {
        val contrast = contrastRatio(
            DarkCryptoColors.textTertiary,
            DarkCryptoColors.cardBackgroundElevated
        )
        
        assertTrue(
            contrast >= LARGE_TEXT_MIN_CONTRAST,
            "Dark theme: Tertiary text on elevated background contrast ($contrast) should be >= $LARGE_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - All primary and secondary text colors meet WCAG AA on all backgrounds`() {
        val themes = listOf(
            "Light" to LightCryptoColors,
            "Dark" to DarkCryptoColors
        )
        
        themes.forEach { (themeName, colors) ->
            val textColors = listOf(
                "textPrimary" to colors.textPrimary,
                "textSecondary" to colors.textSecondary
            )
            
            val backgrounds = listOf(
                "cardBackground" to colors.cardBackground,
                "cardBackgroundElevated" to colors.cardBackgroundElevated
            )
            
            textColors.forEach { (textName, textColor) ->
                backgrounds.forEach { (bgName, bgColor) ->
                    val contrast = contrastRatio(textColor, bgColor)
                    assertTrue(
                        contrast >= NORMAL_TEXT_MIN_CONTRAST,
                        "$themeName theme: $textName on $bgName contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
                    )
                }
            }
        }
    }
}
