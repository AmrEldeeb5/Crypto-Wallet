package com.example.cryptowallet.app.core.util

/**
 * Sealed class representing the state of UI data loading operations.
 * Provides a consistent pattern for handling Loading, Success, Error, and Empty states.
 */
sealed class UiState<out T> {
    /**
     * Represents a loading state while data is being fetched.
     */
    data object Loading : UiState<Nothing>()
    
    /**
     * Represents a successful state with data.
     * @param data The successfully loaded data
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Represents an error state with an error message and optional retry action.
     * @param message The error message to display
     * @param retry Optional retry action callback
     */
    data class Error(
        val message: String,
        val retry: (() -> Unit)? = null
    ) : UiState<Nothing>()
    
    /**
     * Represents an empty state when no data is available.
     */
    data object Empty : UiState<Nothing>()
    
    /**
     * Returns true if this state is Loading.
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Returns true if this state is Success.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns true if this state is Error.
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Returns true if this state is Empty.
     */
    val isEmpty: Boolean get() = this is Empty
    
    /**
     * Returns the data if this is a Success state, null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data
    
    /**
     * Returns the data if this is a Success state, or the default value otherwise.
     */
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default
    
    /**
     * Returns the error message if this is an Error state, null otherwise.
     */
    fun errorOrNull(): String? = (this as? Error)?.message
}

/**
 * Maps the data in a Success state using the provided transform function.
 * Other states are passed through unchanged.
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> UiState.Error(message, retry)
    is UiState.Empty -> UiState.Empty
}

/**
 * Executes the given block if this is a Success state.
 */
inline fun <T> UiState<T>.onSuccess(block: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) block(data)
    return this
}

/**
 * Executes the given block if this is an Error state.
 */
inline fun <T> UiState<T>.onError(block: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) block(message)
    return this
}

/**
 * Executes the given block if this is a Loading state.
 */
inline fun <T> UiState<T>.onLoading(block: () -> Unit): UiState<T> {
    if (this is UiState.Loading) block()
    return this
}

/**
 * Executes the given block if this is an Empty state.
 */
inline fun <T> UiState<T>.onEmpty(block: () -> Unit): UiState<T> {
    if (this is UiState.Empty) block()
    return this
}

/**
 * Converts a nullable value to a UiState.
 * Returns Success if value is not null, Empty otherwise.
 */
fun <T> T?.toUiState(): UiState<T> = if (this != null) UiState.Success(this) else UiState.Empty

/**
 * Converts a Result to a UiState.
 */
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

/**
 * Converts a list to a UiState, treating empty lists as Empty state.
 */
fun <T> List<T>.toUiState(): UiState<List<T>> = 
    if (isEmpty()) UiState.Empty else UiState.Success(this)
