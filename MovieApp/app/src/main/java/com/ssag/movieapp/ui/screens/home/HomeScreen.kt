package com.ssag.movieapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssag.movieapp.ui.screens.home.components.MovieCard
import com.ssag.movieapp.ui.screens.home.components.PersonCard
import com.ssag.movieapp.ui.screens.home.components.SeriesCard
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToMovieDetail: (Int) -> Unit,
    onNavigateToSeriesDetail: (Int) -> Unit,
    onNavigateToPersonDetail: (Int) -> Unit,
    onNavigateToGenres: () -> Unit,
    onNavigateToCountries: () -> Unit,
    onNavigateToPeople: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMoviesBrowse: (String?) -> Unit,
    onNavigateToSeriesBrowse: (String?) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "AndroidFlix",
                        fontWeight = FontWeight.ExtraBold,
                        color = FlixPrimary,
                        letterSpacing = 1.sp
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FlixBackground
                )
            )
        },
        containerColor = FlixBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = FlixPrimary
                )
            } else if (state.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Button(
                        onClick = { viewModel.loadHomeData() },
                        colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary)
                    ) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Latest Movies
                    if (state.latestMovies.isNotEmpty()) {
                        item { HomeSectionHeader("Latest Movies", onSeeAllClick = { onNavigateToMoviesBrowse("newest") }) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.latestMovies) { movie ->
                                    MovieCard(movie = movie, onClick = onNavigateToMovieDetail)
                                }
                            }
                        }
                    }

                    // Popular Movies
                    if (state.popularMovies.isNotEmpty()) {
                        item { HomeSectionHeader("Popular Movies", onSeeAllClick = { onNavigateToMoviesBrowse("popular") }) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.popularMovies) { movie ->
                                    MovieCard(movie = movie, onClick = onNavigateToMovieDetail)
                                }
                            }
                        }
                    }

                    // Latest Series
                    if (state.latestSeries.isNotEmpty()) {
                        item { HomeSectionHeader("Latest Series", onSeeAllClick = { onNavigateToSeriesBrowse("newest") }) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.latestSeries) { series ->
                                    SeriesCard(series = series, onClick = onNavigateToSeriesDetail)
                                }
                            }
                        }
                    }

                    // Popular Series
                    if (state.popularSeries.isNotEmpty()) {
                        item { HomeSectionHeader("Popular Series", onSeeAllClick = { onNavigateToSeriesBrowse("popular") }) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.popularSeries) { series ->
                                    SeriesCard(series = series, onClick = onNavigateToSeriesDetail)
                                }
                            }
                        }
                    }

                    // Top Rated Movies
                    if (state.topRatedMovies.isNotEmpty()) {
                        item { HomeSectionHeader("Top Rated Movies", onSeeAllClick = { onNavigateToMoviesBrowse("rating") }) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.topRatedMovies) { movie ->
                                    MovieCard(movie = movie, onClick = onNavigateToMovieDetail)
                                }
                            }
                        }
                    }

                    // Top Rated Series
                    if (state.topRatedSeries.isNotEmpty()) {
                        item { HomeSectionHeader("Top Rated Series", onSeeAllClick = { onNavigateToSeriesBrowse("rating") }) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.topRatedSeries) { series ->
                                    SeriesCard(series = series, onClick = onNavigateToSeriesDetail)
                                }
                            }
                        }
                    }

                    // Popular People
                    if (state.people.isNotEmpty()) {
                        item { HomeSectionHeader("Popular People", onSeeAllClick = onNavigateToPeople) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.people) { person ->
                                    PersonCard(person = person, onClick = onNavigateToPersonDetail)
                                }
                            }
                        }
                    }

                    // Genres
                    if (state.genres.isNotEmpty()) {
                        item { HomeSectionHeader("Genres", onSeeAllClick = onNavigateToGenres) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.genres.take(12)) { genre ->
                                    CategoryChip(genre.name)
                                }
                            }
                        }
                    }

                    // Countries
                    if (state.countries.isNotEmpty()) {
                        item { HomeSectionHeader("Countries", onSeeAllClick = onNavigateToCountries) }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.countries.take(12)) { country ->
                                    CategoryChip(country.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeSectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        )
        if (onSeeAllClick != null) {
            Text(
                text = "See All ›",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = FlixPrimary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
    }
}

@Composable
fun CategoryChip(name: String) {
    Surface(
        color = FlixSurface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}
