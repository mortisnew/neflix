package com.ssag.movieapp.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    object SeriesDetail : Screen("series_detail/{seriesId}") {
        fun createRoute(seriesId: Int) = "series_detail/$seriesId"
    }
    object Player : Screen("player/{streamUrl}") {
        fun createRoute(streamUrl: String) = "player/$streamUrl"
    }
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Watchlist : Screen("watchlist")
    object History : Screen("history")
    object Profile : Screen("profile")
    object PersonDetail : Screen("person_detail/{personId}") {
        fun createRoute(personId: Int) = "person_detail/$personId"
    }
    object Genres : Screen("genres")
    object Countries : Screen("countries")
    object People : Screen("people")
    
    object MoviesBrowse : Screen("movies_browse?sort={sort}") {
        fun createRoute(sort: String? = null) = if (sort != null) "movies_browse?sort=$sort" else "movies_browse"
    }
    
    object SeriesBrowse : Screen("series_browse?sort={sort}") {
        fun createRoute(sort: String? = null) = if (sort != null) "series_browse?sort=$sort" else "series_browse"
    }

    object FolderDetail : Screen("folder_detail/{folderId}/{title}") {
        fun createRoute(folderId: Int, title: String) = "folder_detail/$folderId/$title"
    }
}
