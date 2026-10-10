package com.example.recalc.core.parser

sealed interface Expr

data class NumberExpr(val value: Double) : Expr
data class ConstantExpr(val name: String, val value: Double) : Expr
data class VariableExpr(val name: String) : Expr
data class UnaryExpr(val op: UnaryOp, val operand: Expr) : Expr
data class BinaryExpr(val left: Expr, val op: BinaryOp, val right: Expr) : Expr
data class FunctionCallExpr(val name: String, val arguments: List<Expr>) : Expr

enum class BinaryOp { ADD, SUBTRACT, MULTIPLY, DIVIDE }
enum class UnaryOp { PLUS, MINUS }

object Constants {
    val values: Map<String, Double> = mapOf(
        "π" to Math.PI,
        "e" to Math.E,
    )

    fun isConstant(name: String): Boolean = values.containsKey(name)
}
