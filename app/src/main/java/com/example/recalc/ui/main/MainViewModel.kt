package com.example.recalc.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recalc.core.parser.CalcException
import com.example.recalc.core.parser.Calculator
import com.example.recalc.data.Formula
import com.example.recalc.data.FormulaRepository
import com.example.recalc.data.HistoryRecord
import com.example.recalc.data.InputValue
import com.example.recalc.data.SaveFormulaResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val formulas: List<Formula> = emptyList(),
    val selectedId: Long? = null,
    val inputs: Map<String, String> = emptyMap(),
    val result: String? = null,
    val error: String? = null,
) {
    val currentFormula: Formula?
        get() = formulas.firstOrNull { it.id == selectedId }
}

class MainViewModel(
    private val repository: FormulaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeFormulas().collect { formulas ->
                _uiState.update { state ->
                    val newSelectedId =
                        if (state.selectedId != null && formulas.any { it.id == state.selectedId }) {
                            state.selectedId
                        } else {
                            formulas.firstOrNull()?.id
                        }
                    if (newSelectedId == state.selectedId) {
                        state.copy(formulas = formulas)
                    } else {
                        val formula = formulas.firstOrNull { it.id == newSelectedId }
                        state.copy(
                            formulas = formulas,
                            selectedId = newSelectedId,
                            inputs = formula?.variables?.associateWith { "" } ?: emptyMap(),
                            result = null,
                            error = null,
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.preloadIfEmpty()
        }
    }

    fun onFormulaSelected(id: Long) {
        _uiState.update { state ->
            val formula = state.formulas.firstOrNull { it.id == id }
            state.copy(
                selectedId = id,
                inputs = formula?.variables?.associateWith { "" } ?: emptyMap(),
                result = null,
                error = null,
            )
        }
    }

    fun onInputChanged(name: String, text: String) {
        _uiState.update { state -> state.copy(inputs = state.inputs + (name to text)) }
    }

    fun calculate() {
        val state = _uiState.value
        val formula = state.currentFormula ?: return
        val variables = mutableMapOf<String, Double>()
        for (name in formula.variables) {
            val text = state.inputs[name].orEmpty().trim()
            val value = text.toDoubleOrNull()
            if (value == null) {
                _uiState.update { it.copy(result = null, error = "变量 \"$name\" 的值不是有效数字") }
                return
            }
            variables[name] = value
        }
        try {
            val result = Calculator.calculate(formula.expression, variables)
            _uiState.update { it.copy(result = result.formatted, error = null) }
            viewModelScope.launch {
                repository.addHistory(
                    HistoryRecord(
                        id = 0,
                        formulaName = formula.name,
                        expression = formula.expression,
                        inputs = variables.map { (k, v) -> InputValue(k, v) },
                        result = result.formatted,
                        timestamp = System.currentTimeMillis(),
                    )
                )
            }
        } catch (e: CalcException) {
            _uiState.update { it.copy(result = null, error = e.message) }
        }
    }

    fun addFormula(name: String, expression: String, variables: List<String>) {
        viewModelScope.launch {
            when (val r = repository.addFormula(name, expression, variables)) {
                is SaveFormulaResult.Success -> _uiState.update {
                    it.copy(
                        selectedId = r.id,
                        inputs = variables.associateWith { "" },
                        result = null,
                        error = null,
                    )
                }
                is SaveFormulaResult.Failure -> _uiState.update { it.copy(error = r.error.message) }
            }
        }
    }

    fun updateFormula(id: Long, name: String, expression: String, variables: List<String>) {
        viewModelScope.launch {
            when (val r = repository.updateFormula(id, name, expression, variables)) {
                is SaveFormulaResult.Success -> _uiState.update {
                    it.copy(
                        selectedId = id,
                        inputs = variables.associateWith { "" },
                        result = null,
                        error = null,
                    )
                }
                is SaveFormulaResult.Failure -> _uiState.update { it.copy(error = r.error.message) }
            }
        }
    }

    fun renameFormula(id: Long, newName: String) {
        viewModelScope.launch { repository.renameFormula(id, newName) }
    }

    fun deleteFormula(id: Long) {
        viewModelScope.launch { repository.deleteFormula(id) }
    }

    companion object {
        fun factory(repository: FormulaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MainViewModel(repository) as T
            }
    }
}
