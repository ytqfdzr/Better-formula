package com.example.recalc.core.parser

class Parser(private val tokens: List<Token>) {
    private var pos = 0

    fun parse(): Expr {
        if (peek().type == TokenType.EOF) {
            throw CalcException(ErrorKind.EMPTY_EXPRESSION, "表达式不能为空", peek().position)
        }
        val expr = parseExpression()
        if (peek().type != TokenType.EOF) {
            throw CalcException(
                ErrorKind.UNEXPECTED_TOKEN,
                "存在多余的内容 \"${peek().text}\"",
                peek().position
            )
        }
        return expr
    }

    private fun parseExpression(): Expr {
        var left = parseTerm()
        while (true) {
            left = when (peek().type) {
                TokenType.PLUS -> { advance(); BinaryExpr(left, BinaryOp.ADD, parseTerm()) }
                TokenType.MINUS -> { advance(); BinaryExpr(left, BinaryOp.SUBTRACT, parseTerm()) }
                else -> return left
            }
        }
    }

    private fun parseTerm(): Expr {
        var left = parseUnary()
        while (true) {
            left = when (peek().type) {
                TokenType.STAR -> { advance(); BinaryExpr(left, BinaryOp.MULTIPLY, parseUnary()) }
                TokenType.SLASH -> { advance(); BinaryExpr(left, BinaryOp.DIVIDE, parseUnary()) }
                else -> return left
            }
        }
    }

    private fun parseUnary(): Expr {
        return when (peek().type) {
            TokenType.PLUS -> { advance(); UnaryExpr(UnaryOp.PLUS, parseUnary()) }
            TokenType.MINUS -> { advance(); UnaryExpr(UnaryOp.MINUS, parseUnary()) }
            else -> parsePrimary()
        }
    }

    private fun parsePrimary(): Expr {
        val token = peek()
        return when (token.type) {
            TokenType.NUMBER -> {
                advance()
                NumberExpr(token.text.toDouble())
            }
            TokenType.IDENTIFIER -> {
                advance()
                val name = token.text
                when {
                    Constants.isConstant(name) -> ConstantExpr(name, Constants.values.getValue(name))
                    peek().type == TokenType.LPAREN -> parseFunctionCall(name)
                    else -> VariableExpr(name)
                }
            }
            TokenType.LPAREN -> {
                advance()
                val inner = parseExpression()
                if (peek().type != TokenType.RPAREN) {
                    throw CalcException(ErrorKind.UNMATCHED_PAREN, "缺少右括号 \")\"", peek().position)
                }
                advance()
                inner
            }
            else -> throw CalcException(
                ErrorKind.UNEXPECTED_TOKEN,
                "此处不期望出现 \"${token.text}\"",
                token.position
            )
        }
    }

    private fun parseFunctionCall(name: String): Expr {
        advance()
        val args = mutableListOf<Expr>()
        if (peek().type != TokenType.RPAREN) {
            args.add(parseExpression())
        }
        if (peek().type != TokenType.RPAREN) {
            throw CalcException(ErrorKind.UNMATCHED_PAREN, "函数缺少右括号 \")\"", peek().position)
        }
        advance()
        return FunctionCallExpr(name, args)
    }

    private fun peek(): Token = tokens[pos]
    private fun advance(): Token = tokens[pos++]
}
