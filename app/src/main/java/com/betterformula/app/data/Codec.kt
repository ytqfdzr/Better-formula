package com.betterformula.app.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class InputValue(
    val name: String,
    val value: Double,
)

object InputCodec {
    private val json = Json { ignoreUnknownKeys = true }

    fun encode(values: List<InputValue>): String = json.encodeToString(values)

    fun decode(text: String): List<InputValue> =
        if (text.isBlank()) emptyList() else json.decodeFromString(text)
}

object VariablesCodec {

    fun encode(variables: List<String>): String = variables.joinToString(",")

    fun decode(text: String): List<String> =
        if (text.isBlank()) emptyList()
        else text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
