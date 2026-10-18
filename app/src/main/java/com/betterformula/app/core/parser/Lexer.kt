package com.betterformula.app.core.parser

object Lexer {

    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c.isWhitespace() -> i++
                isDigit(c) || (c == '.' && i + 1 < input.length && isDigit(input[i + 1])) -> {
                    val start = i
                    i = scanNumber(input, i)
                    tokens.add(Token(TokenType.NUMBER, input.substring(start, i), start))
                }
                isIdentifierStart(c) -> {
                    val start = i
                    i++
                    while (i < input.length && isIdentifierPart(input[i])) i++
                    tokens.add(Token(TokenType.IDENTIFIER, input.substring(start, i), start))
                }
                c == '+' -> { tokens.add(Token(TokenType.PLUS, "+", i)); i++ }
                c == '-' -> { tokens.add(Token(TokenType.MINUS, "-", i)); i++ }
                c == '*' -> { tokens.add(Token(TokenType.STAR, "*", i)); i++ }
                c == '/' -> { tokens.add(Token(TokenType.SLASH, "/", i)); i++ }
                c == '(' -> { tokens.add(Token(TokenType.LPAREN, "(", i)); i++ }
                c == ')' -> { tokens.add(Token(TokenType.RPAREN, ")", i)); i++ }
                else -> throw CalcException(
                    ErrorKind.ILLEGAL_CHARACTER,
                    "存在无法识别的字符 \"$c\"",
                    i
                )
            }
        }
        tokens.add(Token(TokenType.EOF, "", input.length))
        return tokens
    }

    private fun scanNumber(input: String, start: Int): Int {
        var i = start
        var digitsBeforeDot = 0
        while (i < input.length && isDigit(input[i])) {
            i++
            digitsBeforeDot++
        }
        var digitsAfterDot = 0
        if (i < input.length && input[i] == '.') {
            i++
            while (i < input.length && isDigit(input[i])) {
                i++
                digitsAfterDot++
            }
        }
        if (digitsBeforeDot == 0 && digitsAfterDot == 0) {
            throw CalcException(ErrorKind.INVALID_NUMBER, "数字格式不正确", start)
        }
        if (i < input.length && (input[i] == 'e' || input[i] == 'E')) {
            val expStart = i
            i++
            if (i < input.length && (input[i] == '+' || input[i] == '-')) i++
            val expDigitsStart = i
            while (i < input.length && isDigit(input[i])) i++
            if (i == expDigitsStart) {
                throw CalcException(ErrorKind.INVALID_NUMBER, "科学计数法的指数部分缺少数字", expStart)
            }
        }
        return i
    }

    private fun isDigit(c: Char) = c in '0'..'9'
    private fun isIdentifierStart(c: Char) = c == '_' || c.isLetter()
    private fun isIdentifierPart(c: Char) = c == '_' || c.isLetterOrDigit()
}
