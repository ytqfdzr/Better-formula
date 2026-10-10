package com.example.recalc.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LexerTest {

    @Test
    fun lexesNumbers() {
        val tokens = Lexer.tokenize("1 2.5 .5 1e3 1.2E-3")
            .filter { it.type != TokenType.EOF }
            .map { it.text }
        assertEquals(listOf("1", "2.5", ".5", "1e3", "1.2E-3"), tokens)
    }

    @Test
    fun lexesChineseIdentifiersAndConstants() {
        val tokens = Lexer.tokenize("长*π+e")
            .filter { it.type != TokenType.EOF }
        val kinds = tokens.map { it.type }
        assertEquals(
            listOf(
                TokenType.IDENTIFIER, TokenType.STAR,
                TokenType.IDENTIFIER, TokenType.PLUS, TokenType.IDENTIFIER
            ),
            kinds
        )
        assertEquals("长", tokens[0].text)
        assertEquals("π", tokens[2].text)
        assertEquals("e", tokens[4].text)
    }

    @Test
    fun lexesUnderscoreIdentifier() {
        val tokens = Lexer.tokenize("_x1").filter { it.type != TokenType.EOF }
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("_x1", tokens[0].text)
    }

    @Test
    fun rejectsIllegalCharacter() {
        val e = assertThrows(CalcException::class.java) { Lexer.tokenize("1+2@") }
        assertEquals(ErrorKind.ILLEGAL_CHARACTER, e.kind)
    }

    @Test
    fun rejectsFullWidthOperator() {
        assertThrows(CalcException::class.java) { Lexer.tokenize("1＋2") }
    }

    @Test
    fun rejectsIncompleteExponent() {
        val e = assertThrows(CalcException::class.java) { Lexer.tokenize("1e") }
        assertEquals(ErrorKind.INVALID_NUMBER, e.kind)
    }
}
