package com.ssag.movieapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.ssag.movieapp.ui.navigation.Screen
import com.ssag.movieapp.ui.screens.auth.AuthViewModel
import com.ssag.movieapp.ui.screens.auth.LoginScreen
import com.ssag.movieapp.ui.screens.auth.RegisterScreen
import com.ssag.movieapp.ui.screens.detail.MovieDetailScreen
import com.ssag.movieapp.ui.screens.detail.MovieDetailViewModel
import com.ssag.movieapp.ui.screens.detail.PersonDetailScreen
import com.ssag.movieapp.ui.screens.detail.PersonDetailViewModel
import com.ssag.movieapp.ui.screens.detail.SeriesDetailScreen
import com.ssag.movieapp.ui.screens.detail.SeriesDetailViewModel
import com.ssag.movieapp.ui.screens.home.HomeScreen
import com.ssag.movieapp.ui.screens.home.HomeViewModel
import com.ssag.movieapp.ui.screens.home.GenresScreen
import com.ssag.movieapp.ui.screens.home.CountriesScreen
import com.ssag.movieapp.ui.screens.home.PeopleScreen
import com.ssag.movieapp.ui.screens.home.MoviesBrowseScreen
import com.ssag.movieapp.ui.screens.home.MoviesBrowseViewModel
import com.ssag.movieapp.ui.screens.home.SeriesBrowseScreen
import com.ssag.movieapp.ui.screens.home.SeriesBrowseViewModel
import com.ssag.movieapp.ui.screens.player.PlayerActivity
import com.ssag.movieapp.ui.screens.profile.ProfileScreen
import com.ssag.movieapp.ui.screens.profile.ProfileViewModel
import com.ssag.movieapp.ui.screens.profile.FolderDetailScreen
import com.ssag.movieapp.ui.screens.profile.FolderDetailViewModel
import com.ssag.movieapp.ui.screens.search.SearchScreen
import com.ssag.movieapp.ui.screens.search.SearchViewModel
import com.ssag.movieapp.ui.screens.special.FavoritesScreen
import com.ssag.movieapp.ui.screens.special.FavoritesViewModel
import com.ssag.movieapp.ui.screens.special.HistoryScreen
import com.ssag.movieapp.ui.screens.special.HistoryViewModel
import com.ssag.movieapp.ui.screens.special.WatchlistScreen
import com.ssag.movieapp.ui.screens.special.WatchlistViewModel
import com.ssag.movieapp.ui.screens.splash.SplashScreen
import com.ssag.movieapp.ui.theme.MovieAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MovieAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.MoviesBrowse.route,
        Screen.SeriesBrowse.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {

            if (showBottomBar) {

                NavigationBar {

                    // Home
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Home")
                        },
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == Screen.Home.route } == true,
                        onClick = {

                            navController.navigate(Screen.Home.route) {

                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Movies
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Movie,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Movies")
                        },
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route?.startsWith("movies_browse") == true } == true,
                        onClick = {

                            navController.navigate(Screen.MoviesBrowse.createRoute()) {

                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Series
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Tv,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Series")
                        },
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route?.startsWith("series_browse") == true } == true,
                        onClick = {

                            navController.navigate(Screen.SeriesBrowse.createRoute()) {

                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Profile
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Profile")
                        },
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == Screen.Profile.route } == true,
                        onClick = {

                            navController.navigate(Screen.Profile.route) {

                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {

            // Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Login
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            // Register
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Home
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToMovieDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateToSeriesDetail = { seriesId ->
                        navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                    },
                    onNavigateToPersonDetail = { personId ->
                        navController.navigate(Screen.PersonDetail.createRoute(personId))
                    },
                    onNavigateToGenres = {
                        navController.navigate(Screen.Genres.route)
                    },
                    onNavigateToCountries = {
                        navController.navigate(Screen.Countries.route)
                    },
                    onNavigateToPeople = {
                        navController.navigate(Screen.People.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToMoviesBrowse = { sort ->
                        navController.navigate(Screen.MoviesBrowse.createRoute(sort))
                    },
                    onNavigateToSeriesBrowse = { sort ->
                        navController.navigate(Screen.SeriesBrowse.createRoute(sort))
                    }
                )
            }

            // Movies Browse
            composable(
                route = Screen.MoviesBrowse.route,
                arguments = listOf(navArgument("sort") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) {
                val browseViewModel: MoviesBrowseViewModel = hiltViewModel()
                MoviesBrowseScreen(
                    viewModel = browseViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovieDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    }
                )
            }

            // Series Browse
            composable(
                route = Screen.SeriesBrowse.route,
                arguments = listOf(navArgument("sort") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) {
                val browseViewModel: SeriesBrowseViewModel = hiltViewModel()
                SeriesBrowseScreen(
                    viewModel = browseViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSeriesDetail = { seriesId ->
                        navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                    }
                )
            }

            // Genres
            composable(Screen.Genres.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                GenresScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Countries
            composable(Screen.Countries.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                CountriesScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // People
            composable(Screen.People.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                PeopleScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPersonDetail = { personId ->
                        navController.navigate(Screen.PersonDetail.createRoute(personId))
                    }
                )
            }

            // Search
            composable(Screen.Search.route) {
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(
                    viewModel = searchViewModel,
                    onNavigateToMovieDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateToSeriesDetail = { seriesId ->
                        navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                    }
                )
            }

            // Favorites
            composable(Screen.Favorites.route) {
                val favoritesViewModel: FavoritesViewModel = hiltViewModel()
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovieDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateToSeriesDetail = { seriesId ->
                        navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                    }
                )
            }

            // Watchlist
            composable(Screen.Watchlist.route) {
                val watchlistViewModel: WatchlistViewModel = hiltViewModel()
                WatchlistScreen(
                    viewModel = watchlistViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovieDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateToSeriesDetail = { seriesId ->
                        navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                    }
                )
            }

            // History
            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovieDetail = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onNavigateToSeriesDetail = { seriesId ->
                        navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                    }
                )
            }

            // Profile
            composable(Screen.Profile.route) {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    onNavigateToWatchlist = { navController.navigate(Screen.Watchlist.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToFolder = { folderId, title ->
                        navController.navigate(Screen.FolderDetail.createRoute(folderId, title))
                    }
                )
            }

            // Folder Detail
            composable(
                route = Screen.FolderDetail.route,
                arguments = listOf(
                    navArgument("folderId") { type = NavType.IntType },
                    navArgument("title") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val folderDetailViewModel: FolderDetailViewModel = hiltViewModel()
                val title = backStackEntry.arguments?.getString("title") ?: "Folder"
                FolderDetailScreen(
                    viewModel = folderDetailViewModel,
                    folderTitle = title,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovieDetail = { id -> navController.navigate(Screen.MovieDetail.createRoute(id)) },
                    onNavigateToSeriesDetail = { id -> navController.navigate(Screen.SeriesDetail.createRoute(id)) }
                )
            }

            // Movie Detail
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) {
                val detailViewModel: MovieDetailViewModel = hiltViewModel()
                MovieDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPlayMovie = { url, subtitleUrl ->
                        val intent = Intent(context, PlayerActivity::class.java).apply {
                            putExtra("STREAM_URL", url)
                            putExtra("SUBTITLE_URL", subtitleUrl)
                        }
                        context.startActivity(intent)
                    },
                    onNavigateToMovieDetail = { id ->
                        navController.navigate(Screen.MovieDetail.createRoute(id))
                    }
                )
            }

            // Series Detail
            composable(
                route = Screen.SeriesDetail.route,
                arguments = listOf(navArgument("seriesId") { type = NavType.IntType })
            ) {
                val detailViewModel: SeriesDetailViewModel = hiltViewModel()
                SeriesDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPlayEpisode = { url, subtitleUrl ->
                        val intent = Intent(context, PlayerActivity::class.java).apply {
                            putExtra("STREAM_URL", url)
                            putExtra("SUBTITLE_URL", subtitleUrl)
                        }
                        context.startActivity(intent)
                    },
                    onNavigateToSeriesDetail = { id ->
                        navController.navigate(Screen.SeriesDetail.createRoute(id))
                    }
                )
            }

            // Person Detail
            composable(
                route = Screen.PersonDetail.route,
                arguments = listOf(navArgument("personId") { type = NavType.IntType })
            ) {
                val detailViewModel: PersonDetailViewModel = hiltViewModel()
                PersonDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMovieDetail = { id -> navController.navigate(Screen.MovieDetail.createRoute(id)) },
                    onNavigateToSeriesDetail = { id -> navController.navigate(Screen.SeriesDetail.createRoute(id)) }
                )
            }
        }
    }
}
