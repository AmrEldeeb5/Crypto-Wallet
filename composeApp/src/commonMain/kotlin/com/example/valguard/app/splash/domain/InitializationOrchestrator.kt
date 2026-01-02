package com.example.valguard.app.splash.domain

/**
 * Orchestrates real app initialization tasks.
 * Executes tasks in sequence and reports progress.
 */
interface InitializationOrchestrator {
    /**
     * Executes all initialization tasks in sequence.
     * 
     * @param onProgressUpdate Callback invoked when progress changes
     * @return Result indicating success or failure
     */
    suspend fun initialize(
        onProgressUpdate: (progress: Float, phase: InitPhase) -> Unit
    ): Result<Unit>
}
