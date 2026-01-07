package com.example.valguard.app.core.domain


sealed interface DataError: Error {
    enum class Remote: DataError{
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER_ERROR,
        UNKNOWN_ERROR;

        override val message: String
            get() = when(this) {
                REQUEST_TIMEOUT -> "Request timeout"
                TOO_MANY_REQUESTS -> "Too many requests"
                NO_INTERNET -> "No internet connection"
                SERVER_ERROR -> "Server error"
                UNKNOWN_ERROR -> "Unknown error"
            }

        override val cause: Throwable?
            get() = null
    }
    enum class Local: DataError{
        DISK_FULL,
        INSUFFICIENT_FUNDS,
        UNKNOWN_ERROR;

        override val message: String
            get() = when(this) {
                DISK_FULL -> "Disk full"
                INSUFFICIENT_FUNDS -> "Insufficient funds"
                UNKNOWN_ERROR -> "Unknown error"
            }

        override val cause: Throwable?
            get() = null
    }
}


