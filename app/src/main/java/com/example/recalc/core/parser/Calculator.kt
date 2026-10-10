package com.example.recalc.core.parser

data class CalculationResult(
    val value: Double,
    val formatted: String,
)

object Calculator {

    fun calculate(expression: String, variables: Map<String, Double>): CalculationResult {
        val expr = Parser(Lexer.tokenize(expression)).parse()
        val used = Evaluator.collectVariables(expr)
        val declared = variables.keys
        val missing = declared - used
        if (missing.isNotEmpty()) {
            throw CalcException(ErrorKind.MISSING_VARIABLE, "缺少变量 ${missing.joinToString("、")}")
        }
        val extra = used - declared
        if (extra.isNotEmpty()) {
            throw CalcException(ErrorKind.UNDEFINED_VARIABLE, "未定义变量 ${extra.joinToString("、")}")
        }
        val value = Evaluator.evaluate(expr, variables)
        return CalculationResult(value, NumberFormat.format(value))
    }
}
