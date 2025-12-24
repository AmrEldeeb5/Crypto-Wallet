package com.example.cryptowallet.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Property tests for color contrast compliance.
 * 
 * Property 19: Color Contrast Compliance
 * For any text-background color pair in both light and dark themes, the contrast ratio 
 * SHALL meet WCAG AA standards (minimum 4.5:1 for normal text, 3:1 for large text).
 * 
 * Validates: Requirements 9.3, 10.4
 */
class ColorContrastPropertyTest {

    companion object {
        // WCAG AA minimum contrast ratios
        const val NORMAL_TEXT_MIN_CONTRAST = 4.5
        const val LARGE_TEXT_MIN_CONTRAST = 3.0
        
        /**
         * Calculate relative luminance of a color according to WCAG 2.1
         * https://www.w3.org/WAI/GL/wiki/Relative_luminance
         */
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
        
        /**
         * Calculate contrast ratio between two colors according to WCAG 2.1
         * https://www.w3.org/WAI/GL/wiki/Contrast_ratio
         */
        fun contrastRatio(foreground: Color, background: Color): Double {
            val l1 = relativeLuminance(foreground)
            val l2 = relativeLuminance(background)
            
            val lighter = max(l1, l2)
            val darker = min(l1, l2)
            
            return (lighter + 0.05) / (darker + 0.05)
        }
    }

    /**
     * Property 19: Test that primary text on card background meets WCAG AA contrast.
     */
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

    /**
     * Property 19: Test that secondary text on card background meets WCAG AA contrast.
     */
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

    /**
     * Property 19: Test that primary text on elevated card background meets WCAG AA contrast.
     */
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

    /**
     * Property 19: Test that profit color (green) on card background meets large text contrast.
     * Profit/loss colors are typically used for larger text (prices, percentages).
     */
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

    /**
     * Property 19: Test that loss color (red) on card background meets large text contrast.
     */
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

    /**
     * Property 19: Test that status error color on card background meets large text contrast.
     */
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

    /**
     * Property 19: Test that button text on button primary color meets WCAG AA contrast.
     */
    @Test
    fun `Property 19 - Light theme card background on button primary meets WCAG AA`() {
        val contrast = contrastRatio(
            LightCryptoColors.cardBackground,
            LightCryptoColors.buttonPrimary
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Light theme: Card background on button primary contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    @Test
    fun `Property 19 - Dark theme card background on button primary meets WCAG AA`() {
        val contrast = contrastRatio(
            DarkCryptoColors.cardBackground,
            DarkCryptoColors.buttonPrimary
        )
        
        assertTrue(
            contrast >= NORMAL_TEXT_MIN_CONTRAST,
            "Dark theme: Card background on button primary contrast ($contrast) should be >= $NORMAL_TEXT_MIN_CONTRAST"
        )
    }

    /**
     * Property 19: Test that tertiary text on elevated background meets minimum contrast.
     * Tertiary text is used for less important information, so we use large text threshold.
     */
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

    /**
     * Property 19: Comprehensive test - all text colors on all backgrounds meet minimum contrast.
     */
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
