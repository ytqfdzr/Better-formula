package com.example.recalc.data

import androidx.room.withTransaction
import com.example.recalc.core.parser.CalcException
import com.example.recalc.core.parser.ErrorKind
import com.example.recalc.core.parser.FormulaValidator
import com.example.recalc.data.db.AppDatabase
import com.example.recalc.data.entity.FormulaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FormulaRepository(
    private val database: AppDatabase,
) {
    private val formulaDao = database.formulaDao()
    private val historyDao = database.historyDao()

    fun observeFormulas(): Flow<List<Formula>> =
        formulaDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeHistory(): Flow<List<HistoryRecord>> =
        historyDao.observeRecent(HistoryFifo.MAX_RECORDS).map { list -> list.map { it.toDomain() } }

    suspend fun getFormulas(): List<Formula> =
        formulaDao.getAll().map { it.toDomain() }

    suspend fun addFormula(
        name: String,
        expression: String,
        variables: List<String>,
    ): SaveFormulaResult {
        val error = FormulaValidator.check(expression, variables)
        if (error != null) return SaveFormulaResult.Failure(error)
        val now = System.currentTimeMillis()
        val id = formulaDao.insert(
            FormulaEntity(
                name = name,
                expression = expression,
                variables = VariablesCodec.encode(variables),
                createdAt = now,
                updatedAt = now,
            )
        )
        return SaveFormulaResult.Success(id)
    }

    suspend fun updateFormula(
        id: Long,
        name: String,
        expression: String,
        variables: List<String>,
    ): SaveFormulaResult {
        val error = FormulaValidator.check(expression, variables)
        if (error != null) return SaveFormulaResult.Failure(error)
        val existing = formulaDao.getById(id)
            ?: return SaveFormulaResult.Failure(CalcException(ErrorKind.NOT_FOUND, "公式不存在"))
        formulaDao.update(
            existing.copy(
                name = name,
                expression = expression,
                variables = VariablesCodec.encode(variables),
                updatedAt = System.currentTimeMillis(),
            )
        )
        return SaveFormulaResult.Success(id)
    }

    suspend fun renameFormula(id: Long, newName: String) {
        val existing = formulaDao.getById(id) ?: return
        formulaDao.update(existing.copy(name = newName, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteFormula(id: Long) {
        formulaDao.deleteById(id)
    }

    suspend fun addHistory(record: HistoryRecord) {
        database.withTransaction {
            historyDao.insert(record.toEntity())
            val ids = historyDao.getIdsOrderedByTimestampAsc()
            val toDelete = HistoryFifo.idsToDelete(ids)
            if (toDelete.isNotEmpty()) {
                historyDao.deleteByIds(toDelete)
            }
        }
    }

    suspend fun clearHistory() {
        historyDao.clear()
    }

    suspend fun preloadIfEmpty() {
        if (formulaDao.count() == 0) {
            addFormula("体积", "长*宽*高", listOf("长", "宽", "高"))
        }
    }
}
