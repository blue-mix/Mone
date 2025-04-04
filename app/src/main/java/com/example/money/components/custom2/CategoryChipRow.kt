package com.example.money.components.custom2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.money.data.models.CategorySpending

@Composable
fun CategoryChipsRow(categories: List<CategorySpending>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
            AssistChip(
                onClick = {},
                label = {
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text("${cat.name} ${(cat.percentage * 100).toInt()}%")
                    }
                },
                colors = AssistChipDefaults.assistChipColors(containerColor = cat.color)
            )
        }
    }
}
