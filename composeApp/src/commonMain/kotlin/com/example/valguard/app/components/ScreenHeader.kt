/**
 * ScreenHeader.kt
 *
 * Standard header component for secondary screens with back navigation.
 * Provides consistent navigation and title display across the app.
 *
 * Features:
 * - Back arrow button
 * - Screen title
 * - Optional subtitle
 * - Status bar padding
 * - Accessibility support
 *
 * @see ValguardHeader for the main app header
 */
package com.example.valguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.LocalCryptoColors

/**
 * Header with back navigation for secondary screens.
 *
 * Displays a back button, title, and optional subtitle.
 * Includes proper status bar padding.
 *
 * @param title Main screen title
 * @param subtitle Optional subtitle text
 * @param onBackClick Callback when back button is pressed
 * @param modifier Optional modifier for the header
 */
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "Go back" }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.textPrimary
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            }
        }
    }
}
