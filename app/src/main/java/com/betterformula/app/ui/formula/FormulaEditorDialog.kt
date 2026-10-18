package com.betterformula.app.ui.formula

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.betterformula.app.core.parser.FormulaValidator
import com.betterformula.app.data.Formula

@Composable
fun FormulaEditorDialog(
    initial: Formula?,
    onDismiss: () -> Unit,
    onSave: (name: String, expression: String, variables: List<String>) -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var expression by remember { mutableStateOf(initial?.expression ?: "") }
    var variableNames by remember { mutableStateOf(initial?.variables ?: List(3) { "" }) }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    if (initial == null) "新增公式" else "编辑公式",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("公式名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = expression,
                    onValueChange = { expression = it },
                    label = { Text("表达式(如 长*宽*高)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("变量个数", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        enabled = variableNames.size > 2,
                        onClick = { variableNames = variableNames.dropLast(1) },
                    ) { Text("−") }
                    Text("${variableNames.size}", style = MaterialTheme.typography.titleMedium)
                    TextButton(
                        enabled = variableNames.size < 10,
                        onClick = { variableNames = variableNames + "" },
                    ) { Text("+") }
                }
                Spacer(Modifier.height(8.dp))
                variableNames.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { new ->
                            variableNames = variableNames.toMutableList().also { it[index] = new }
                        },
                        label = { Text("变量 ${index + 1}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                }
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val trimmedName = name.trim()
                        if (trimmedName.isEmpty()) {
                            error = "公式名称不能为空"
                        } else {
                            val trimmedVariables = variableNames.map { it.trim() }
                            val validation = FormulaValidator.check(expression, trimmedVariables)
                            if (validation != null) {
                                error = validation.message
                            } else {
                                onSave(trimmedName, expression, trimmedVariables)
                            }
                        }
                    }) { Text("保存") }
                }
            }
        }
    }
}
