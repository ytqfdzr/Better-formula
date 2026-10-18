package com.betterformula.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryFifoTest {

    @Test
    fun keepsAllWhenUnderLimit() {
        val ids = (1L..100L).toList()
        assertEquals(emptyList<Long>(), HistoryFifo.idsToDelete(ids))
    }

    @Test
    fun deletesOldestWhenOverLimit() {
        val ids = (1L..101L).toList()
        assertEquals(listOf(1L), HistoryFifo.idsToDelete(ids))
    }

    @Test
    fun deletesMultipleOldest() {
        val ids = (1L..105L).toList()
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), HistoryFifo.idsToDelete(ids))
    }

    @Test
    fun emptyListReturnsEmpty() {
        assertEquals(emptyList<Long>(), HistoryFifo.idsToDelete(emptyList()))
    }
}
