package com.betterformula.app.ui.history

import com.betterformula.app.data.HistoryRecord
import com.betterformula.app.data.InputValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryFormattingTest {

    private fun record(vararg inputs: InputValue) = HistoryRecord(
        id = 1,
        formulaName = "体积",
        expression = "长*宽*高",
        inputs = inputs.toList(),
        result = "24",
        timestamp = 0L,
    )

    @Test
    fun inputsTextJoinsNameValuePairs() {
        val text = historyInputsText(
            record(InputValue("长", 2.0), InputValue("宽", 3.0), InputValue("高", 4.0))
        )
        assertEquals("长=2、宽=3、高=4", text)
    }

    @Test
    fun inputsTextEmptyWhenNoInputs() {
        assertEquals("", historyInputsText(record()))
    }

    @Test
    fun timeFormattedAsYyyyMmDdHms() {
        val text = formatHistoryTime(1_700_000_000_000L)
        assertTrue(text, Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""").matches(text))
    }
}