/**
 * MoreMenuDropdown.kt
 *
 * Dropdown menu component for additional app actions.
 * Appears as a popup from the header's "more" button.
 *
 * Current menu items:
 * - Referral Program: Opens the referral/invite screen
 *
 * @see CryptoVaultHeader for the trigger button
 */
package com.example.cryptovault.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.cryptovault.theme.LocalCryptoColors

/**
 * Dropdown menu popup with additional app actions.
 *
 * Displays a card with menu items that appears below the header.
 * Dismisses when tapping outside the menu.
 *
 * @param onDismiss Callback when the menu should close
 * @param onReferralClick Callback when "Referral Program" is tapped
 * @param modifier Optional modifier for the menu card
 */
@Composable
fun MoreMenuDropdown(
    onDismiss: () -> Unit,
    onReferralClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss)
        ) {
            Card(
                modifier = modifier
                    .padding(top = 60.dp, end = 16.dp)
                    .align(Alignment.TopEnd)
                    .width(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.cardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Referral menu item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onReferralClick)
                            .padding(12.dp)
                            .semantics { contentDescription = "Open referral program" },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = colors.accentBlue400,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Referral Program",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
