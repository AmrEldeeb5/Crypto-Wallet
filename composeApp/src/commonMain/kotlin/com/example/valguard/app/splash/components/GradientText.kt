/**
 * GradientText.kt
 *
 * Gradient text component for the Valguard splash screen.
 * Renders text with a linear gradient effect (Blue → Purple → Pink)
 * for premium branding and visual distinction.
 *
 * Uses Compose's drawWithContent modifier to apply a gradient mask
 * over the text, creating a smooth color transition effect.
 */
package com.example.valguard.app.splash.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.valguard.theme.Blue400
import com.example.valguard.theme.Pink400
import com.example.valguard.theme.Purple400

/**
 * Text component with linear gradient effect.
 *
 * Renders text with a horizontal gradient from Blue400 → Purple400 → Pink400.
 * The gradient is applied as a mask over the text using the drawWithContent
 * modifier, ensuring proper text rendering with gradient coloring.
 *
 * @param text The text content to display
 * @param style The text style (typography, font size, weight, etc.)
 * @param modifier Modifier to be applied to the text
 */
@Composable
fun GradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(Blue400, Purple400, Pink400)
    
    Text(
        text = text,
        style = style.copy(
            brush = Brush.linearGradient(
                colors = gradientColors
            )
        ),
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}
