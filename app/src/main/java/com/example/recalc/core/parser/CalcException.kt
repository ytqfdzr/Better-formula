package com.example.recalc.core.parser

enum class ErrorKind {
    EMPTY_EXPRESSION,
    ILLEGAL_CHARACTER,
    INVALID_NUMBER,
    UNEXPECTED_TOKEN,
    UNEXPECTED_END,
    UNMATCHED_PAREN,
    DIVISION_BY_ZERO,
    OVERFLOW,
    UNDEFINED_FUNCTION,
    MISSING_VARIABLE,
    UNDEFINED_VARIABLE,
    INVALID_VARIABLE_COUNT,
    DUPLICATE_VARIABLE,
    INVALID_VARIABLE_NAME,
    RESERVED_VARIABLE_NAME,
    NOT_FOUND,
}

class CalcException(
    val kind: ErrorKind,
    override val message: String,
    val position: Int? = null
) : Exception(message)
