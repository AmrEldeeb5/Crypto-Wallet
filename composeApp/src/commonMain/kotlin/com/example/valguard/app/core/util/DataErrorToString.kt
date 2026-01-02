package com.example.valguard.app.core.util

import com.example.valguard.app.core.domain.DataError
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.error_disk_full
import valguard.composeapp.generated.resources.error_insufficient_balance
import valguard.composeapp.generated.resources.error_no_internet
import valguard.composeapp.generated.resources.error_request_timeout
import valguard.composeapp.generated.resources.error_too_many_requests
import valguard.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource


fun DataError.toUiText(): StringResource {
    val stringRes = when(this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN_ERROR -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER_ERROR -> Res.string.error_unknown
        DataError.Remote.UNKNOWN_ERROR -> Res.string.error_unknown
        DataError.Local.INSUFFICIENT_FUNDS -> Res.string.error_insufficient_balance
    }
    return stringRes
}