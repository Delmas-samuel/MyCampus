package com.example.mycampus.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycampus.domain.model.News
import com.example.mycampus.ui.screens.contacts.ContactsScreen
import com.example.mycampus.ui.screens.map.MapScreen
import com.example.mycampus.ui.screens.news.NewsDetailScreen
import com.example.mycampus.ui.screens.news.NewsScreen
import com.example.mycampus.ui.screens.report.ReportScreen
import com.example.mycampus.ui.viewmodel.MapViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        NavItem.Actualites,
        NavItem.Annuaire,
        NavItem.Carte,
        NavItem.Signalement
    )

    val bottomBarRoutes = items.map { it.route }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    val primaryColor = Color(0xFF25D366)
    val whatsappLightGreen = Color(0xFFDCF8C6)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                AnimatedContent(
                                    targetState = selected,
                                    transitionSpec = {
                                        fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) with
                                                fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    },
                                    label = "icon_anim"
                                ) { isSelected ->
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = primaryColor,
                                selectedTextColor = primaryColor,
                                indicatorColor = whatsappLightGreen,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Actualites.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Actualités
            composable(NavItem.Actualites.route) {
                NewsScreen(
                    onNewsClick = { news ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                        navController.navigate(NewsScreens.NewsDetail.route)
                    }
                )
            }

            // Détail actualité
            composable(NewsScreens.NewsDetail.route) {
                val news = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<News>("selectedNews")

                if (news != null) {
                    NewsDetailScreen(
                        navController = navController,
                        news = news
                    )
                } else {
                    Text("Actualité non trouvée. Erreur de navigation.")
                }
            }

            // Annuaire
            composable(NavItem.Annuaire.route) {
                ContactsScreen()
            }

            // Carte
            composable(NavItem.Carte.route) {
                // Obtain the Hilt-provided ViewModel scoped to this destination
                val mapViewModel: MapViewModel = hiltViewModel()
                MapScreen(viewModel = mapViewModel)
            }

            // Signalement
            composable(NavItem.Signalement.route) {
                ReportScreen()
            }
        }
    }
}
