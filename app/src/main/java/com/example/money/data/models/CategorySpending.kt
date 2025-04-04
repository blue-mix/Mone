package com.example.money.data.models

import androidx.compose.ui.graphics.Color

data class CategorySpending(
    val name: String,
    val color: Color,
    val amount: Double,
    val percentage: Float,
    val transactions: Int,
)

