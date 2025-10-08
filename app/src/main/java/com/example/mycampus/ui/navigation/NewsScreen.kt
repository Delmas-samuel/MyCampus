package com.example.mycampus.ui.navigation


sealed class NewsScreens(val route: String) {
    object NewsList : NewsScreens("news_list")
    object NewsDetail : NewsScreens("news_detail")


    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}