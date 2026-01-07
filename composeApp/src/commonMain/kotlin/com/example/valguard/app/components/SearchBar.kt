/**
 * SearchBar.kt
 *
 * Text input component for searching cryptocurrencies.
 * Provides a styled search field with icon and clear button.
 *
 * Features:
 * - Search icon
 * - Placeholder text
 * - Clear button when text is entered
 * - Styled to match app theme
 *
 * @see TabNavigation for filter tabs that complement search
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.LocalCryptoColors

/**
 * Search input field for filtering cryptocurrencies.
 *
 * Displays a text field with search icon and optional clear button.
 * Styled with rounded corners and theme colors.
 *
 * @param query Current search query text
 * @param onQueryChange Callback when query text changes
 * @param placeholder Placeholder text when empty (default: "Search cryptocurrencies...")
 * @param modifier Optional modifier for the search bar
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search cryptocurrencies...",
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(12.dp)
    
    Row(
        modifier = modifier
            .background(colors.cardBackground.copy(alpha = 0.5f), shape)
            .border(1.dp, colors.cardBorder.copy(alpha = 0.5f), shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    color = colors.textTertiary,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 14.sp
                ),
                singleLine = true,
                cursorBrush = SolidColor(colors.accentBlue400),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
