package com.betterformula.app.data

object HistoryFifo {

    const val MAX_RECORDS = 100

    fun idsToDelete(idsOrderedOldestFirst: List<Long>): List<Long> {
        val overflow = idsOrderedOldestFirst.size - MAX_RECORDS
        if (overflow <= 0) return emptyList()
        return idsOrderedOldestFirst.take(overflow)
    }
}
