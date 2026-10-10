package com.example.recalc.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CalculatorTest {

    @Test
    fun respectsPrecedence() {
        assertEquals("14", calc("2+3*4", emptyMap()))
    }

    @Test
    fun respectsParentheses() {
        assertEquals("20", calc("(2+3)*4", emptyMap()))
    }

    @Test
    fun evaluatesNestedExpression() {
        assertEquals("1.5", calc("2*(A+B)/C-0.5", mapOf("A" to 1.0, "B" to 2.0, "C" to 3.0)))
    }

    @Test
    fun unaryMinus() {
        assertEquals("5", calc("-A + B", mapOf("A" to 5.0, "B" to 10.0)))
    }

    @Test
    fun unaryPlus() {
        assertEquals("7", calc("+A + B", mapOf("A" to 2.0, "B" to 5.0)))
    }

    @Test
    fun scientificNotationInput() {
        assertEquals("240", calc("1.2e2 * 2", emptyMap()))
    }

    @Test
    fun chineseVariables() {
        assertEquals("6", calc("长*宽", mapOf("长" to 2.0, "宽" to 3.0)))
    }

    @Test
    fun constants() {
        assertEquals("6.28319", calc("2*π", emptyMap()))
    }

    @Test
    fun divisionByZero() {
        val e = assertThrows(CalcException::class.java) { calc("1/0", emptyMap()) }
        assertEquals(ErrorKind.DIVISION_BY_ZERO, e.kind)
    }

    @Test
    fun overflow() {
        val e = assertThrows(CalcException::class.java) { calc("1e308*1e308", emptyMap()) }
        assertEquals(ErrorKind.OVERFLOW, e.kind)
    }

    @Test
    fun missingVariable() {
        val e = assertThrows(CalcException::class.java) {
            calc("A*B", mapOf("A" to 1.0, "B" to 2.0, "C" to 3.0))
        }
        assertEquals(ErrorKind.MISSING_VARIABLE, e.kind)
    }

    @Test
    fun undefinedVariable() {
        val e = assertThrows(CalcException::class.java) {
            calc("A*B", mapOf("A" to 1.0))
        }
        assertEquals(ErrorKind.UNDEFINED_VARIABLE, e.kind)
    }

    @Test
    fun unmatchedParen() {
        val e = assertThrows(CalcException::class.java) { calc("(1+2", emptyMap()) }
        assertEquals(ErrorKind.UNMATCHED_PAREN, e.kind)
    }

    @Test
    fun emptyExpression() {
        val e = assertThrows(CalcException::class.java) { calc("", emptyMap()) }
        assertEquals(ErrorKind.EMPTY_EXPRESSION, e.kind)
    }

    @Test
    fun consecutiveOperators() {
        val e = assertThrows(CalcException::class.java) { calc("A**B", mapOf("A" to 2.0, "B" to 3.0)) }
        assertEquals(ErrorKind.UNEXPECTED_TOKEN, e.kind)
    }

    @Test
    fun multipleDecimalPoints() {
        val e = assertThrows(CalcException::class.java) { calc("1.2.3", emptyMap()) }
        assertEquals(ErrorKind.UNEXPECTED_TOKEN, e.kind)
    }

    @Test
    fun undefinedFunction() {
        val e = assertThrows(CalcException::class.java) { calc("sqrt(4)", emptyMap()) }
        assertEquals(ErrorKind.UNDEFINED_FUNCTION, e.kind)
    }

    @Test
    fun roundsToFiveDecimals() {
        assertEquals("0.33333", calc("1/3", emptyMap()))
        assertEquals("0.66667", calc("2/3", emptyMap()))
    }

    @Test
    fun formatsScientificForLargeAndSmall() {
        assertEquals("1.00000e+09", calc("1000000000", emptyMap()))
        assertEquals("1.00000e-06", calc("0.000001", emptyMap()))
    }

    private fun calc(expression: String, variables: Map<String, Double>): String =
        Calculator.calculate(expression, variables).formatted
}
