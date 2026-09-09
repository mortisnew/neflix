package com.ssag.movieapp.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssag.movieapp.ui.screens.home.components.MovieCard
import com.ssag.movieapp.ui.screens.home.components.SeriesCard
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface
import com.ssag.movieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    viewModel: PersonDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMovieDetail: (Int) -> Unit,
    onNavigateToSeriesDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.person?.full_name ?: "Person Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FlixBackground,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FlixBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = FlixPrimary)
            } else if (state.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadPersonDetail() }, colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary)) {
                        Text("Retry")
                    }
                }
            } else {
                state.person?.let { person ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = FlixSurface,
                                    modifier = Modifier.size(160.dp),
                                    shadowElevation = 8.dp
                                ) {
                                    AsyncImage(
                                        model = "${Constants.BASE_URL}people/people/${person.id}/avatar/",
                                        contentDescription = person.full_name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = person.full_name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Biography",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = person.bio.ifBlank { "No biography available." },
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.LightGray
                            )
                        }

                        if (state.movies.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Movies",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    items(state.movies) { movie ->
                                        MovieCard(movie = movie, onClick = onNavigateToMovieDetail)
                                    }
                                }
                            }
                        }

                        if (state.series.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "TV Series",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    items(state.series) { series ->
                                        SeriesCard(series = series, onClick = onNavigateToSeriesDetail)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
