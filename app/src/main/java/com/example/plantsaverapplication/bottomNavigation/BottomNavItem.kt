package com.example.plantsaverapplication.bottomNavigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Krizbai Csaba - 2022.10.17
 * Bottom Navigation class
 * Tutorial source: @Phillip Lackner
 */

data class BottomNavItem(
    val name: String,
    val route: String,
    val image: ImageVector,
    val badgeNumber: Int = 0
)
