package com.example.recalc.core.parser

object FormulaValidator {

    fun check(expression: String, variableNames: List<String>): CalcException? {
        validateVariableNames(variableNames)?.let { return it }
        val expr = try {
            Parser(Lexer.tokenize(expression)).parse()
        } catch (e: CalcException) {
            return e
        }
        val used = Evaluator.collectVariables(expr)
        val declared = variableNames.toSet()
        val missing = declared - used
        if (missing.isNotEmpty()) {
            return CalcException(ErrorKind.MISSING_VARIABLE, "缺少变量 ${missing.joinToString("、")}")
        }
        val extra = used - declared
        if (extra.isNotEmpty()) {
            return CalcException(ErrorKind.UNDEFINED_VARIABLE, "未定义变量 ${extra.joinToString("、")}")
        }
        return null
    }

    fun validateVariableNames(names: List<String>): CalcException? {
        if (names.size < 2 || names.size > 10) {
            return CalcException(
                ErrorKind.INVALID_VARIABLE_COUNT,
                "变量个数需在 2 到 10 之间(当前 ${names.size} 个)"
            )
        }
        val seen = mutableSetOf<String>()
        for (raw in names) {
            val name = raw.trim()
            if (name.isEmpty() || !isValidIdentifier(name)) {
                return CalcException(ErrorKind.INVALID_VARIABLE_NAME, "非法的变量名 \"$raw\"")
            }
            if (Constants.isConstant(name)) {
                return CalcException(ErrorKind.RESERVED_VARIABLE_NAME, "变量名不能与常量同名 \"$raw\"")
            }
            if (!seen.add(name)) {
                return CalcException(ErrorKind.DUPLICATE_VARIABLE, "变量名重复 \"$raw\"")
            }
        }
        return null
    }

    private fun isValidIdentifier(name: String): Boolean {
        val first = name[0]
        if (first != '_' && !first.isLetter()) return false
        for (i in 1 until name.length) {
            val c = name[i]
            if (c != '_' && !c.isLetterOrDigit()) return false
        }
        return true
    }
}
