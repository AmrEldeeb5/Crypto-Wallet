package com.example.valguard.app.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AutoResizingText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
    minFontSizeSp: Float = 8f
) {
    var resizedStyle by remember { mutableStateOf(style) }
    var shouldDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        softWrap = false,
        maxLines = maxLines,
        overflow = TextOverflow.Clip, // Avoid ellipsis until we hit min size
        style = resizedStyle.copy(
            fontWeight = fontWeight ?: style.fontWeight
        ),
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (resizedStyle.fontSize.value > minFontSizeSp) {
                    val newSize = resizedStyle.fontSize * 0.9f
                    resizedStyle = resizedStyle.copy(fontSize = newSize)
                } else {
                    shouldDraw = true // Too small, just draw it (might clip)
                }
            } else {
                shouldDraw = true
            }
        }
    )
}
