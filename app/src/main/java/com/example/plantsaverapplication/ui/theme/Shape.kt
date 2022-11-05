package com.example.plantsaverapplication.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

/** Bottom navigation */
val bottomNavigationShape = RoundedCornerShape(
    topStart = 15.dp,
    topEnd =  15.dp
)