package com.example.recalc.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recalc.data.Formula
import com.example.recalc.ui.formula.FormulaEditorDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToHistory: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingFormula by remember { mutableStateOf<Formula?>(null) }
    var deletingFormula by remember { mutableStateOf<Formula?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                FormulaDrawerContent(
                    formulas = state.formulas,
                    selectedId = state.selectedId,
                    onSelect = { id ->
                        viewModel.onFormulaSelected(id)
                        scope.launch { drawerState.close() }
                    },
                    onAdd = {
                        showAddDialog = true
                        scope.launch { drawerState.close() }
                    },
                    onEdit = { editingFormula = it },
                    onDelete = { deletingFormula = it },
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(state.currentFormula?.name ?: "持续计算") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "菜单")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "历史")
                        }
                    },
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                val formula = state.currentFormula
                if (formula == null) {
                    Text("暂无公式,请点左上角菜单新增")
                } else {
                    Text("公式:${formula.expression}", fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    formula.variables.forEach { name ->
                        OutlinedTextField(
                            value = state.inputs[name] ?: "",
                            onValueChange = { viewModel.onInputChanged(name, it) },
                            label = { Text(name) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::calculate, modifier = Modifier.fillMaxWidth()) {
                        Text("计算")
                    }
                    Spacer(Modifier.height(12.dp))
                    state.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    state.result?.let {
                        Text(
                            "结果:$it",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        FormulaEditorDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, expr, vars ->
                viewModel.addFormula(name, expr, vars)
                showAddDialog = false
            },
        )
    }

    editingFormula?.let { formula ->
        FormulaEditorDialog(
            initial = formula,
            onDismiss = { editingFormula = null },
            onSave = { name, expr, vars ->
                viewModel.updateFormula(formula.id, name, expr, vars)
                editingFormula = null
            },
        )
    }

    deletingFormula?.let { formula ->
        DeleteConfirmDialog(
            formula = formula,
            onDismiss = { deletingFormula = null },
            onConfirm = {
                viewModel.deleteFormula(formula.id)
                deletingFormula = null
            },
        )
    }
}

@Composable
private fun FormulaDrawerContent(
    formulas: List<Formula>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onAdd: () -> Unit,
    onEdit: (Formula) -> Unit,
    onDelete: (Formula) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .padding(16.dp)
    ) {
        Text("公式列表", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        if (formulas.isEmpty()) {
            Text("暂无公式", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(formulas, key = { it.id }) { formula ->
                    val selected = formula.id == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(formula.id) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                formula.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                            Text(
                                formula.expression,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { onEdit(formula) }) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑")
                        }
                        IconButton(onClick = { onDelete(formula) }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("新增公式")
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    formula: Formula,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除公式") },
        text = { Text("确定删除 \"${formula.name}\" 吗?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("删除", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}
