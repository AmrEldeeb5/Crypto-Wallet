package com.example.cryptowallet.app.realtime.domain

import com.example.cryptowallet.app.realtime.TestGenerators
import com.example.cryptowallet.app.realtime.data.SubscriptionManagerImpl
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Property-based tests for SubscriptionManager.
 */
class SubscriptionManagerTest {

    /**
     * **Feature: realtime-price-updates, Property 3: Screen lifecycle subscription management**
     *
     * For any screen that subscribes to coin updates, when that screen is hidden,
     * all its subscriptions SHALL be removed from the active subscription set.
     *
     * Validates: Requirements 1.3, 2.3, 5.3
     */
    @Test
    fun `Property 3 - screen lifecycle subscription management`() = runTest {
        checkAll(100, TestGenerators.screenIdArb, TestGenerators.coinIdListArb()) { screenId, coinIds ->
            val manager = SubscriptionManagerImpl()

            // Subscribe the screen
            manager.subscribe(screenId, coinIds)

            // Verify subscriptions are active
            manager.getSubscriptionsForScreen(screenId) shouldBe coinIds.toSet()

            // Unsubscribe (screen hidden)
            manager.unsubscribe(screenId)

            // Verify all subscriptions are removed
            manager.getSubscriptionsForScreen(screenId).shouldBeEmpty()
            manager.getActiveSubscriptions().intersect(coinIds.toSet()).shouldBeEmpty()
        }
    }

    /**
     * **Feature: realtime-price-updates, Property 8: Subscription aggregation**
     *
     * For any set of screens with overlapping coin subscriptions, the active subscription set
     * SHALL contain the union of all subscribed coins without duplicates.
     *
     * Validates: Requirements 1.1, 2.1, 5.1
     */
    @Test
    fun `Property 8 - subscription aggregation`() = runTest {
        val screenSubscriptionsArb = Arb.list(
            Arb.pair(TestGenerators.screenIdArb, TestGenerators.coinIdListArb()),
            1..5
        )

        checkAll(100, screenSubscriptionsArb) { screenSubscriptions ->
            val manager = SubscriptionManagerImpl()

            // Subscribe all screens
            screenSubscriptions.forEach { (screenId, coinIds) ->
                manager.subscribe(screenId, coinIds)
            }

            // Calculate expected union
            val expectedUnion = screenSubscriptions
                .flatMap { it.second }
                .toSet()

            // Verify aggregation equals union without duplicates
            manager.getActiveSubscriptions() shouldBe expectedUnion
        }
    }

    /**
     * Test that subscribing the same screen twice replaces the previous subscription.
     */
    @Test
    fun `subscribe replaces previous subscription for same screen`() = runTest {
        checkAll(100, TestGenerators.screenIdArb, TestGenerators.coinIdListArb(), TestGenerators.coinIdListArb()) { screenId, firstCoinIds, secondCoinIds ->
            val manager = SubscriptionManagerImpl()

            // First subscription
            manager.subscribe(screenId, firstCoinIds)
            manager.getSubscriptionsForScreen(screenId) shouldBe firstCoinIds.toSet()

            // Second subscription replaces first
            manager.subscribe(screenId, secondCoinIds)
            manager.getSubscriptionsForScreen(screenId) shouldBe secondCoinIds.toSet()
        }
    }

    /**
     * Test that unsubscribing one screen doesn't affect other screens.
     */
    @Test
    fun `unsubscribe only affects the specified screen`() = runTest {
        checkAll(100, TestGenerators.screenIdArb, TestGenerators.screenIdArb, TestGenerators.coinIdListArb(), TestGenerators.coinIdListArb()) { screenId1, screenId2, coinIds1, coinIds2 ->
            // Skip if screen IDs are the same
            if (screenId1 == screenId2) return@checkAll

            val manager = SubscriptionManagerImpl()

            // Subscribe both screens
            manager.subscribe(screenId1, coinIds1)
            manager.subscribe(screenId2, coinIds2)

            // Unsubscribe first screen
            manager.unsubscribe(screenId1)

            // Second screen should still have its subscriptions
            manager.getSubscriptionsForScreen(screenId2) shouldBe coinIds2.toSet()
            manager.getActiveSubscriptions() shouldContainAll coinIds2.toSet()
        }
    }

    /**
     * Test that unsubscribing a non-existent screen is a no-op.
     */
    @Test
    fun `unsubscribe non-existent screen is no-op`() = runTest {
        checkAll(100, TestGenerators.screenIdArb) { screenId ->
            val manager = SubscriptionManagerImpl()

            // Unsubscribe without subscribing first - should not throw
            manager.unsubscribe(screenId)

            manager.getActiveSubscriptions().shouldBeEmpty()
        }
    }
}
