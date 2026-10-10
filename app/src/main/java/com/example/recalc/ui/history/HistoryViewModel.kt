package com.example.recalc.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recalc.data.FormulaRepository
import com.example.recalc.data.HistoryRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val records: List<HistoryRecord> = emptyList(),
)

class HistoryViewModel(
    private val repository: FormulaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeHistory().collect { records ->
                _uiState.update { it.copy(records = records) }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    companion object {
        fun factory(repository: FormulaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HistoryViewModel(repository) as T
            }
    }
}
