package com.example.recalc.core.parser

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberFormatTest {

    @Test
    fun integer() {
        assertEquals("6", NumberFormat.format(6.0))
    }

    @Test
    fun trimsTrailingZeros() {
        assertEquals("2.5", NumberFormat.format(2.5))
    }

    @Test
    fun roundsHalfUp() {
        assertEquals("0.33333", NumberFormat.format(1.0 / 3.0))
        assertEquals("0.66667", NumberFormat.format(2.0 / 3.0))
    }

    @Test
    fun avoidsFloatArtifacts() {
        assertEquals("0.3", NumberFormat.format(0.1 + 0.2))
    }

    @Test
    fun negative() {
        assertEquals("-0.5", NumberFormat.format(-0.5))
    }

    @Test
    fun zero() {
        assertEquals("0", NumberFormat.format(0.0))
    }

    @Test
    fun largeBecomesScientific() {
        assertEquals("1.00000e+09", NumberFormat.format(1e9))
    }

    @Test
    fun smallBecomesScientific() {
        assertEquals("1.00000e-06", NumberFormat.format(1e-6))
    }
}
