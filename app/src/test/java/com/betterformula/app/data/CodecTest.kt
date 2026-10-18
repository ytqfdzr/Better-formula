package com.betterformula.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class CodecTest {

    @Test
    fun variablesRoundTrip() {
        val variables = listOf("长", "宽", "高")
        val encoded = VariablesCodec.encode(variables)
        assertEquals("长,宽,高", encoded)
        assertEquals(variables, VariablesCodec.decode(encoded))
    }

    @Test
    fun variablesDecodeEmpty() {
        assertEquals(emptyList<String>(), VariablesCodec.decode(""))
    }

    @Test
    fun inputsRoundTrip() {
        val inputs = listOf(InputValue("长", 2.0), InputValue("宽", 3.0))
        val encoded = InputCodec.encode(inputs)
        assertEquals(inputs, InputCodec.decode(encoded))
    }

    @Test
    fun inputsDecodeEmpty() {
        assertEquals(emptyList<InputValue>(), InputCodec.decode(""))
    }
}
