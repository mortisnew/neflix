package com.ssag.movieapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ssag.movieapp.ui.screens.home.components.MovieCard
import com.ssag.movieapp.ui.screens.home.components.SeriesCard
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToMovieDetail: (Int) -> Unit,
    onNavigateToSeriesDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(FlixBackground)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.onQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search movies or series...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlixPrimary,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = FlixPrimary
            ),
            shape = MaterialTheme.shapes.medium
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = FlixPrimary)
            } else if (state.movies.isEmpty() && state.series.isEmpty() && state.query.isNotEmpty()) {
                Text(
                    text = "No results found for \"${state.query}\"",
                    modifier = Modifier.align(Alignment.Center),
                    color = FlixTextSecondary
                )
            } else if (state.query.isEmpty()) {
                 Text(
                    text = "Type to search AndroidFlix",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.movies) { movie ->
                        MovieCard(movie = movie, onClick = onNavigateToMovieDetail)
                    }
                    items(state.series) { series ->
                        SeriesCard(series = series, onClick = onNavigateToSeriesDetail)
                    }
                }
            }
        }
    }
}
