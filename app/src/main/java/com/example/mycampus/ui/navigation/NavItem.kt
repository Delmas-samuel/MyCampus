package com.example.mycampus.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Actualites : NavItem(
        "actualites",
        "Actualités",
        Icons.AutoMirrored.Outlined.Article,
        Icons.AutoMirrored.Filled.Article
    )
    object Annuaire : NavItem(
        "annuaire",
        "Annuaire",
        Icons.Outlined.Contacts,
        Icons.Filled.Contacts
    )
    object Carte : NavItem(
        "carte",
        "Carte",
        Icons.Outlined.Map,
        Icons.Filled.Map
    )
    object Signalement : NavItem(
        "signalement",
        "Signalement",
        Icons.Outlined.Report,
        Icons.Filled.Report
    )
}