package com.example.recalc.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormulaTest {

    @Test
    fun acceptsValidVariableNames() {
        assertNull(FormulaValidator.validateVariableNames(listOf("长", "宽", "高")))
    }

    @Test
    fun rejectsTooFewVariables() {
        val e = FormulaValidator.validateVariableNames(listOf("A"))!!
        assertEquals(ErrorKind.INVALID_VARIABLE_COUNT, e.kind)
    }

    @Test
    fun rejectsTooManyVariables() {
        val names = (1..11).map { "v$it" }
        val e = FormulaValidator.validateVariableNames(names)!!
        assertEquals(ErrorKind.INVALID_VARIABLE_COUNT, e.kind)
    }

    @Test
    fun rejectsDuplicateVariable() {
        val e = FormulaValidator.validateVariableNames(listOf("A", "A"))!!
        assertEquals(ErrorKind.DUPLICATE_VARIABLE, e.kind)
    }

    @Test
    fun rejectsReservedConstantName() {
        val e = FormulaValidator.validateVariableNames(listOf("π", "r"))!!
        assertEquals(ErrorKind.RESERVED_VARIABLE_NAME, e.kind)
    }

    @Test
    fun rejectsInvalidVariableName() {
        val e = FormulaValidator.validateVariableNames(listOf("1x", "y"))!!
        assertEquals(ErrorKind.INVALID_VARIABLE_NAME, e.kind)
    }

    @Test
    fun formulaCheckOk() {
        assertNull(FormulaValidator.check("长*宽*高", listOf("长", "宽", "高")))
    }

    @Test
    fun formulaCheckMissingVariable() {
        val e = FormulaValidator.check("长*宽", listOf("长", "宽", "高"))!!
        assertEquals(ErrorKind.MISSING_VARIABLE, e.kind)
    }

    @Test
    fun formulaCheckUndefinedVariable() {
        val e = FormulaValidator.check("长*宽*高", listOf("长", "宽"))!!
        assertEquals(ErrorKind.UNDEFINED_VARIABLE, e.kind)
    }

    @Test
    fun constantNotCountedAsVariable() {
        assertNull(FormulaValidator.check("长*宽*π", listOf("长", "宽")))
    }
}
