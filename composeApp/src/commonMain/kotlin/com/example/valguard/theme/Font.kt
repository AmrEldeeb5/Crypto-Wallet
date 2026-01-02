/**
 * Font.kt
 *
 * Custom font configuration for the Valguard application.
 * This file is prepared for loading custom fonts (Ubuntu font family)
 * using Compose Multiplatform resources.
 *
 * Currently commented out pending font file addition to the project.
 * To enable custom fonts:
 * 1. Add Ubuntu font files to composeResources/font/ directory
 * 2. Uncomment the imports and UbuntuFontFamily function
 * 3. Update CryptoTypography to use UbuntuFontFamily()
 *
 * Font weights available:
 * - Light (300)
 * - Regular (400)
 * - Medium (500)
 * - Bold (700)
 *
 * @see CryptoTypography for typography configuration
 */
package com.example.valguard.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// TODO: Add Ubuntu font files to composeResources/font/ directory
// import Valguard.composeapp.generated.resources.Res
// import Valguard.composeapp.generated.resources.Ubuntu_Bold
// import Valguard.composeapp.generated.resources.Ubuntu_Light
// import Valguard.composeapp.generated.resources.Ubuntu_Medium
// import Valguard.composeapp.generated.resources.Ubuntu_Regular
// import org.jetbrains.compose.resources.Font

/**
 * Creates the Ubuntu font family for use in typography.
 *
 * Uncomment this function after adding font files to enable custom fonts.
 *
 * @return A [FontFamily] containing Ubuntu font variants.
 */
// @Composable
// fun UbuntuFontFamily() = FontFamily(
//     Font(Res.font.Ubuntu_Light, weight = FontWeight.Light),
//     Font(Res.font.Ubuntu_Regular, weight = FontWeight.Normal),
//     Font(Res.font.Ubuntu_Medium, weight = FontWeight.Medium),
//     Font(Res.font.Ubuntu_Bold, weight = FontWeight.Bold),
// )
