package com.example.recalc.core.parser

object Evaluator {

    fun evaluate(expr: Expr, variables: Map<String, Double>): Double {
        return when (expr) {
            is NumberExpr -> expr.value
            is ConstantExpr -> expr.value
            is VariableExpr -> variables[expr.name]
                ?: throw CalcException(ErrorKind.UNDEFINED_VARIABLE, "未定义变量 \"${expr.name}\"")
            is UnaryExpr -> {
                val v = evaluate(expr.operand, variables)
                val result = when (expr.op) {
                    UnaryOp.PLUS -> v
                    UnaryOp.MINUS -> -v
                }
                checkFinite(result)
                result
            }
            is BinaryExpr -> {
                val left = evaluate(expr.left, variables)
                val right = evaluate(expr.right, variables)
                val result = when (expr.op) {
                    BinaryOp.ADD -> left + right
                    BinaryOp.SUBTRACT -> left - right
                    BinaryOp.MULTIPLY -> left * right
                    BinaryOp.DIVIDE -> {
                        if (right == 0.0) throw CalcException(ErrorKind.DIVISION_BY_ZERO, "除数不能为 0")
                        left / right
                    }
                }
                checkFinite(result)
                result
            }
            is FunctionCallExpr -> throw CalcException(
                ErrorKind.UNDEFINED_FUNCTION,
                "未定义的函数 \"${expr.name}\""
            )
        }
    }

    fun collectVariables(expr: Expr): Set<String> {
        val result = mutableSetOf<String>()
        fun walk(e: Expr) {
            when (e) {
                is VariableExpr -> result.add(e.name)
                is UnaryExpr -> walk(e.operand)
                is BinaryExpr -> { walk(e.left); walk(e.right) }
                is FunctionCallExpr -> e.arguments.forEach { walk(it) }
                else -> {}
            }
        }
        walk(expr)
        return result
    }

    private fun checkFinite(value: Double) {
        if (!value.isFinite()) {
            throw CalcException(ErrorKind.OVERFLOW, "计算结果超出可表示范围")
        }
    }
}
