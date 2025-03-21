package com.example.money.utils

import androidx.compose.ui.graphics.Color

fun generateColorForCategory(name: String): Color {
    return when (name.lowercase()) {
        "food" -> Color(0xFFE57373)
        "transport" -> Color(0xFF64B5F6)
        "shopping" -> Color(0xFFBA68C8)
        "income" -> Color(0xFF81C784)
        "bills" -> Color(0xFFFFB74D)
        "entertainment" -> Color(0xFF9575CD)
        "travel" -> Color(0xFFFF8A65)
        "transfers" -> Color(0xFF4DB6AC)
        "cash withdrawal" -> Color(0xFF90A4AE)
        "uncategorized" -> Color.Gray
        else -> Color(0xFFBDBDBD) // fallback grey
    }
}
