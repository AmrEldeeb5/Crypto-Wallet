/**
 * OfflineBanner.kt
 *
 * Displays a warning banner when the app is offline.
 * Informs users that they're viewing cached data.
 *
 * Features:
 * - Warning icon
 * - "Offline - Showing cached data" message
 * - Error-colored background
 * - Accessibility support
 *
 * @see ConnectionStatusIndicator for more detailed connection states
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.LocalCryptoColors

/**
 * Banner component indicating offline status.
 *
 * Shows a warning message that cached data is being displayed
 * because the device is offline.
 *
 * @param modifier Optional modifier for the banner
 */
@Composable
fun OfflineBanner(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.statusError.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics { contentDescription = "You are offline. Showing cached data." },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = colors.statusError,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "Offline - Showing cached data",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.statusError
        )
    }
}
