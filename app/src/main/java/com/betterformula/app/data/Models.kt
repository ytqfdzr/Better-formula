package com.betterformula.app.data

import com.betterformula.app.core.parser.CalcException
import com.betterformula.app.data.entity.FormulaEntity
import com.betterformula.app.data.entity.HistoryRecordEntity

data class Formula(
    val id: Long,
    val name: String,
    val expression: String,
    val variables: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
)

data class HistoryRecord(
    val id: Long,
    val formulaName: String,
    val expression: String,
    val inputs: List<InputValue>,
    val result: String,
    val timestamp: Long,
)

sealed interface SaveFormulaResult {
    data class Success(val id: Long) : SaveFormulaResult
    data class Failure(val error: CalcException) : SaveFormulaResult
}

fun FormulaEntity.toDomain() = Formula(
    id = id,
    name = name,
    expression = expression,
    variables = VariablesCodec.decode(variables),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun HistoryRecordEntity.toDomain() = HistoryRecord(
    id = id,
    formulaName = formulaName,
    expression = expression,
    inputs = InputCodec.decode(inputsJson),
    result = result,
    timestamp = timestamp,
)

fun HistoryRecord.toEntity() = HistoryRecordEntity(
    id = id,
    formulaName = formulaName,
    expression = expression,
    inputsJson = InputCodec.encode(inputs),
    result = result,
    timestamp = timestamp,
)
