package com.example.valguard.app.onboarding

import com.example.valguard.app.onboarding.presentation.steps.getSelectionCountText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectionCountPropertyTest {
    
    @Test
    fun `zero selection shows error message`() {
        val text = getSelectionCountText(0)
        assertEquals(
            "Select at least one coin to continue",
            text,
            "Zero selection should show error message"
        )
    }
    
    @Test
    fun `single selection shows singular form`() {
        val text = getSelectionCountText(1)
        assertEquals(
            "1 coin selected",
            text,
            "Single selection should show singular form"
        )
    }
    
    @Test
    fun `two selections shows plural form`() {
        val text = getSelectionCountText(2)
        assertEquals(
            "2 coins selected",
            text,
            "Two selections should show plural form"
        )
    }
    
    @Test
    fun `multiple selections show correct count`() {
        for (count in 2..6) {
            val text = getSelectionCountText(count)
            assertEquals(
                "$count coins selected",
                text,
                "Count $count should show '$count coins selected'"
            )
        }
    }
    
    @Test
    fun `selection count text contains the count`() {
        for (count in 1..10) {
            val text = getSelectionCountText(count)
            assertTrue(
                text.contains(count.toString()),
                "Text should contain the count $count"
            )
        }
    }
    
    @Test
    fun `plural form used for counts greater than 1`() {
        for (count in 2..10) {
            val text = getSelectionCountText(count)
            assertTrue(
                text.contains("coins"),
                "Count $count should use plural 'coins'"
            )
        }
    }
    
    @Test
    fun `singular form used only for count 1`() {
        val text = getSelectionCountText(1)
        assertTrue(
            text.contains("coin") && !text.contains("coins"),
            "Count 1 should use singular 'coin'"
        )
    }
}
