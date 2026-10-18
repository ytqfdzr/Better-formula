package com.betterformula.app.ui.history

import com.betterformula.app.core.parser.NumberFormat
import com.betterformula.app.data.HistoryRecord
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val HISTORY_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

internal fun formatHistoryTime(millis: Long): String =
    HISTORY_TIME_FORMATTER.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

internal fun historyInputsText(record: HistoryRecord): String =
    record.inputs.joinToString("、") { "${it.name}=${NumberFormat.format(it.value)}" }