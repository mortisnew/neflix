package com.ssag.movieapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssag.movieapp.ui.screens.home.components.FilterBar
import com.ssag.movieapp.ui.screens.home.components.SeriesCard
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesBrowseScreen(
    viewModel: SeriesBrowseViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSeriesDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyGridState()

    // Pagination trigger
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= state.series.size - 5) {
                    viewModel.loadSeries()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TV Series", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FlixBackground,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = FlixBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterBar(
                genres = state.genres,
                countries = state.countries,
                selectedGenre = state.selectedGenre,
                selectedCountry = state.selectedCountry,
                selectedSort = state.selectedSort,
                onGenreSelected = viewModel::onGenreSelected,
                onCountrySelected = viewModel::onCountrySelected,
                onSortSelected = viewModel::onSortSelected,
                onClearFilters = viewModel::clearFilters
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && state.series.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = FlixPrimary
                    )
                } else if (state.error != null && state.series.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.error!!, color = Color.Red)
                        Button(
                            onClick = { viewModel.loadSeries(reset = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary)
                        ) {
                            Text("Retry")
                        }
                    }
                } else if (state.series.isEmpty()) {
                    Text(
                        text = "No series found",
                        modifier = Modifier.align(Alignment.Center),
                        color = FlixTextSecondary
                    )
                } else {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(state.series) { index, series ->
                            SeriesCard(series = series, onClick = onNavigateToSeriesDetail)
                        }

                        if (state.isPaginationLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = FlixPrimary, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
