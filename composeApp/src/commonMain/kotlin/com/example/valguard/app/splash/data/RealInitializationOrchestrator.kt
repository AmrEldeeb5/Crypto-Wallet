package com.example.valguard.app.splash.data

import com.example.valguard.app.splash.domain.InitPhase
import com.example.valguard.app.splash.domain.InitializationOrchestrator
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

/**
 * Real implementation of initialization orchestrator.
 * Executes tasks in sequence with timeout and retry logic.
 */
class RealInitializationOrchestrator(
    private val secureStorageInitializer: SecureStorageInitializer,
    private val databaseInitializer: DatabaseInitializer,
    private val networkWarmer: NetworkWarmer,
    private val configLoader: ConfigLoader,
    private val uiReadinessChecker: UIReadinessChecker
) : InitializationOrchestrator {
    
    override suspend fun initialize(
        onProgressUpdate: (progress: Float, phase: InitPhase) -> Unit
    ): Result<Unit> = runCatching {
        // Phase 1: Secure Storage (0-30%)
        executeWithRetry(
            phase = InitPhase.SecureStorage,
            onProgressUpdate = onProgressUpdate
        ) {
            secureStorageInitializer.initialize()
        }
        
        // Phase 2: Database (30-55%)
        executeWithRetry(
            phase = InitPhase.Database,
            onProgressUpdate = onProgressUpdate
        ) {
            databaseInitializer.initialize()
        }
        
        // Phase 3: Network (55-75%)
        executeWithRetry(
            phase = InitPhase.Network,
            onProgressUpdate = onProgressUpdate
        ) {
            networkWarmer.warmUp()
        }
        
        // Phase 4: Config (75-90%)
        executeWithRetry(
            phase = InitPhase.Config,
            onProgressUpdate = onProgressUpdate
        ) {
            configLoader.load()
        }
        
        // Phase 5: UI Ready (90-100%)
        executeWithRetry(
            phase = InitPhase.UIReady,
            onProgressUpdate = onProgressUpdate
        ) {
            uiReadinessChecker.check()
        }
        
        // Final progress update
        onProgressUpdate(1.0f, InitPhase.UIReady)
    }
    
    private suspend fun executeWithRetry(
        phase: InitPhase,
        onProgressUpdate: (Float, InitPhase) -> Unit,
        maxRetries: Int = 1,
        task: suspend () -> Unit
    ) {
        // Update progress to start of phase
        onProgressUpdate(phase.progressRange.start, phase)
        
        var lastException: Exception? = null
        
        repeat(maxRetries + 1) { attempt ->
            try {
                // Execute with 10-second timeout
                withTimeout(10.seconds) {
                    task()
                }
                
                // Update progress to end of phase on success
                onProgressUpdate(phase.progressRange.endInclusive, phase)
                return
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries) {
                    // Wait before retry
                    kotlinx.coroutines.delay(1000)
                }
            }
        }
        
        // If we get here, all retries failed
        throw lastException ?: Exception("Task failed after $maxRetries retries")
    }
}

/**
 * Base interface for initialization tasks.
 */
interface InitializationTask {
    suspend fun initialize()
}

/**
 * Secure storage initialization task.
 */
interface SecureStorageInitializer : InitializationTask

/**
 * Database initialization task.
 */
interface DatabaseInitializer : InitializationTask

/**
 * Network warm-up task.
 */
interface NetworkWarmer {
    suspend fun warmUp()
}

/**
 * Configuration loader task.
 */
interface ConfigLoader {
    suspend fun load()
}

/**
 * UI readiness checker task.
 */
interface UIReadinessChecker {
    suspend fun check()
}
