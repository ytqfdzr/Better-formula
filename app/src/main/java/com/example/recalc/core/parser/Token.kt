package com.example.recalc.core.parser

enum class TokenType {
    NUMBER,
    IDENTIFIER,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    LPAREN,
    RPAREN,
    EOF,
}

data class Token(
    val type: TokenType,
    val text: String,
    val position: Int
)
