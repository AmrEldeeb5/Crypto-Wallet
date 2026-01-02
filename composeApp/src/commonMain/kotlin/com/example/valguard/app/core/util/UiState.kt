package com.example.valguard.app.core.util

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    
    data class Success<T>(val data: T) : UiState<T>()
    
    data class Error(
        val message: String,
        val retry: (() -> Unit)? = null
    ) : UiState<Nothing>()
    
    data object Empty : UiState<Nothing>()
    
    val isLoading: Boolean get() = this is Loading
    
    val isSuccess: Boolean get() = this is Success
    
    val isError: Boolean get() = this is Error
    
    val isEmpty: Boolean get() = this is Empty
    
    fun getOrNull(): T? = (this as? Success)?.data
    
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default
    
    fun errorOrNull(): String? = (this as? Error)?.message
}

inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> UiState.Error(message, retry)
    is UiState.Empty -> UiState.Empty
}

inline fun <T> UiState<T>.onSuccess(block: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) block(data)
    return this
}

inline fun <T> UiState<T>.onError(block: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) block(message)
    return this
}

inline fun <T> UiState<T>.onLoading(block: () -> Unit): UiState<T> {
    if (this is UiState.Loading) block()
    return this
}

inline fun <T> UiState<T>.onEmpty(block: () -> Unit): UiState<T> {
    if (this is UiState.Empty) block()
    return this
}

fun <T> T?.toUiState(): UiState<T> = if (this != null) UiState.Success(this) else UiState.Empty

fun <T> Result<T>.toUiState(
    emptyCheck: (T) -> Boolean = { false },
    errorMessage: (Throwable) -> String = { it.message ?: "Unknown error" }
): UiState<T> = fold(
    onSuccess = { data ->
        if (emptyCheck(data)) UiState.Empty else UiState.Success(data)
    },
    onFailure = { error ->
        UiState.Error(errorMessage(error))
    }
)

fun <T> List<T>.toUiState(): UiState<List<T>> = 
    if (isEmpty()) UiState.Empty else UiState.Success(this)
