package com.example.plantsaverapplication.recyclerViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

/**
 * Krizbai Csaba - 2022.12.03
 * I have created the day selection recyclerview. (LazyColumn)
 *
 * Example code at the link below:
 * https://github.com/philipplackner/ComposeMultiSelect
 */

data class Day(
    val dayName: String,
    var isSelected: Boolean
)

@Composable
fun DaySelectorRecyclerView(fontFamily: FontFamily, days: List<Day>, onSelectedDay: (Int, Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(days.size) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectedDay(i, !days[i].isSelected)
                        }
                        .background(
                            if (days[i].isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                        .padding(horizontal = 25.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${i + 1}.${days[i].dayName}", fontFamily = fontFamily)

                    Checkbox(
                        checked = days[i].isSelected,
                        onCheckedChange = {
                            onSelectedDay(i, it)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.onSecondary)
                    )
                }
            }
        }
    }
}