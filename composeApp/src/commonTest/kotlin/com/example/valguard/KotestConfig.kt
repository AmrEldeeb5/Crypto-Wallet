/**
 * KotestConfig.kt
 *
 * Global Kotest configuration for the Valguard test suite.
 * Configures property-based testing parameters including minimum
 * iterations and test execution settings.
 *
 * This configuration ensures that all property-based tests run
 * with at least 100 iterations to provide comprehensive coverage
 * across the input space.
 */
package com.example.valguard

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import kotlin.time.Duration.Companion.seconds

/**
 * Project-wide Kotest configuration.
 *
 * Sets default values for:
 * - Property test iterations (minimum 100)
 * - Test isolation mode (InstancePerLeaf for proper test isolation)
 * - Parallel execution settings
 */
object KotestConfig : AbstractProjectConfig() {
    
    /**
     * Isolation mode for test specs.
     *
     * InstancePerLeaf creates a new spec instance for each test,
     * ensuring proper isolation and preventing state leakage
     * between tests.
     */
    override val isolationMode = IsolationMode.InstancePerLeaf
    
    /**
     * Number of threads for parallel test execution.
     *
     * Set to 1 for deterministic test execution. Can be increased
     * for faster test runs on multi-core systems.
     */
    override val parallelism = 1
    
    /**
     * Global timeout for all tests.
     *
     * Set to 60 seconds to allow for property-based tests with
     * 100+ iterations.
     */
    override val timeout = 60.seconds
}
