package com.example.valguard.app.trade.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.valguard.app.components.CoinHeader
import com.example.valguard.app.components.TradeConfirmation
import com.example.valguard.app.components.TradeConfirmationDialog
import com.example.valguard.app.core.util.formatFiat
import com.example.valguard.app.trade.presentation.common.component.rememberCurrencyVisualTransformation
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.LocalCryptoTypography
import org.jetbrains.compose.resources.stringResource


@Composable
fun TradeScreen(
    state: TradeState,
    tradeType: TradeType,
    onAmountChange: (String) -> Unit,
    onSubmitClicked: () -> Unit,
    onConfirmTrade: () -> Unit = {},
    onCancelConfirmation: () -> Unit = {},
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    
    val actionColor = when (tradeType) {
        TradeType.BUY -> colors.profit
        TradeType.SELL -> colors.loss
    }

    val focusManager = LocalFocusManager.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
            .statusBarsPadding()
            .imePadding() // 1. Move UI up with keyboard
            .clickable( // 3. Dismiss keyboard on tap outside
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState()) // 2. Allow scrolling if content is pushed too high
        ) {
            CoinHeader(
                iconUrl = state.coin?.iconUrl ?: "",
                name = state.coin?.name ?: ""
            )
            
            Spacer(modifier = Modifier.height(spacing.lg))
            
            Text(
                text = when (tradeType) {
                    TradeType.BUY -> "Buy Amount"
                    TradeType.SELL -> "Sell Amount"
                },
                style = typography.bodyMedium,
                color = colors.textSecondary
            )
            
            CenteredDollarTextField(
                amountText = state.amount,
                onAmountChange = onAmountChange
            )
            
            Text(
                text = state.availableAmount,
                style = typography.labelLarge,
                color = colors.textSecondary,
                modifier = Modifier.padding(spacing.xs)
            )
            
            // Validation error display
            if (state.validationError != null) {
                Text(
                    text = stringResource(state.validationError),
                    style = typography.labelLarge,
                    color = colors.statusError,
                    modifier = Modifier.padding(spacing.xs)
                )
            }
            
            // General error display
            if (state.error != null) {
                Text(
                    text = stringResource(state.error),
                    style = typography.labelLarge,
                    color = colors.statusError,
                    modifier = Modifier.padding(spacing.xs)
                )
            }
        }
        
        Button(
            onClick = onSubmitClicked,
            enabled = state.isAmountValid && !state.isExecuting,
            colors = ButtonDefaults.buttonColors(containerColor = actionColor),
            contentPadding = PaddingValues(horizontal = 64.dp, vertical = 12.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = spacing.xl)
        ) {
            Text(
                text = when (tradeType) {
                    TradeType.BUY -> "Buy Now"
                    TradeType.SELL -> "Sell Now"
                },
                style = typography.labelLarge,
                color = colors.cardBackground
            )
        }
    }
    
    // Confirmation dialog
    if (state.showConfirmation && state.coin != null) {
        val amountValue = state.amount.toDoubleOrNull() ?: 0.0
        val totalValue = amountValue
        
        TradeConfirmationDialog(
            confirmation = TradeConfirmation(
                coinName = state.coin.name,
                coinSymbol = state.coin.symbol,
                amount = formatFiat(amountValue),
                price = formatFiat(state.coin.price),
                totalValue = formatFiat(totalValue),
                isBuy = tradeType == TradeType.BUY
            ),
            onConfirm = onConfirmTrade,
            onCancel = onCancelConfirmation
        )
    }
}

@Composable
fun CenteredDollarTextField(
    modifier: Modifier = Modifier,
    amountText: String,
    onAmountChange: (String) -> Unit
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    val focusManager = LocalFocusManager.current

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val currencyVisualTransformation = rememberCurrencyVisualTransformation()
    val displayText = amountText.trimStart('0')

    BasicTextField(
        value = displayText,
        onValueChange = { newValue ->
            val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
            if (trimmed.isEmpty() || trimmed.toInt() <= 10000) {
                onAmountChange(trimmed)
            }
        },
        modifier = modifier.focusRequester(focusRequester).padding(spacing.md),
        textStyle = TextStyle(
            color = colors.textPrimary,
            fontSize = typography.displayMedium.fontSize,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(56.dp).wrapContentWidth()
            ) { innerTextField() }
        },
        cursorBrush = SolidColor(colors.textPrimary),
        visualTransformation = currencyVisualTransformation,
    )
}

enum class TradeType { BUY, SELL }
