package com.example.mycampus.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.with
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycampus.ui.screens.contacts.ContactsScreen
import com.example.mycampus.ui.screens.map.MapScreen
import com.example.mycampus.ui.screens.news.NewsScreen
import com.example.mycampus.ui.screens.report.ReportScreen


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

    //Color
    val primaryColor = Color(0xFF25D366)
    val whatsappLightGreen = Color(0xFFDCF8C6)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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
                            // Animation into icon
                            AnimatedContent(
                                targetState = selected,
                                transitionSpec = {
                                    fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) with
                                            fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            ) { isSelected ->
                                if (isSelected) {
                                    Icon(
                                        imageVector = item.selectedIcon,
                                        contentDescription = item.title,
                                        tint = primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController = navController,
                startDestination = NavItem.Actualites.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(
                    NavItem.Actualites.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300, easing = EaseInOut)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        NewsScreen()
                    }
                }

                composable(
                    NavItem.Annuaire.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300, easing = EaseInOut)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ContactsScreen()
                    }
                }

                composable(
                    NavItem.Carte.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300, easing = EaseInOut)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MapScreen()
                    }
                }

                composable(
                    NavItem.Signalement.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300, easing = EaseInOut)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ReportScreen()
                    }
                }
            }
        }
    }
}