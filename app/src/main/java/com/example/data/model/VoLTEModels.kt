package com.example.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceItem(
    val id: String,
    val manufacturer: String,
    val model: String,
    val modelNumbers: String,
    val chipset: String = "غير متوفر في المصدر",
    val chipsetGuess: String = "غير متوفر في المصدر",
    val category: String, // "auto1", "auto2", "app", "tools"
    val sourceTag: String = "pdf" // "pdf", "extra"
)

@Immutable
data class SecretCodeItem(
    val code: String,
    val manufacturer: String,
    val deviceType: String,
    val chipset: String,
    val codeType: String,
    val description: String,
    val warning: String = ""
)

@Immutable
data class ProblemItem(
    val n: Int,
    val title: String,
    val desc: String,
    val solution: List<String>,
    val manufacturer: String,
    val model: String,
    val warning: String = ""
)

@Immutable
data class ActivationRule(
    val company: String,
    val models: String,
    val method: String,
    val note: String
)

@Immutable
data class ApnItem(
    val name: String,
    val apn: String,
    val type: String,
    val source: String = "pdf"
)

@Immutable
data class GuideContent(
    val title: String,
    val intro: String,
    val steps: List<String>
)
