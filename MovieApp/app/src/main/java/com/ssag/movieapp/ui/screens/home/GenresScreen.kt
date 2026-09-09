package com.ssag.movieapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Genres", fontWeight = FontWeight.Bold) },
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
            if (state.isLoading && state.genres.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = FlixPrimary)
            } else if (state.error != null && state.genres.isEmpty()) {
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
            } else if (state.genres.isEmpty()) {
                Text(
                    text = "No genres available.",
                    modifier = Modifier.align(Alignment.Center),
                    color = FlixTextSecondary
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.genres) { genre ->
                        GenreCard(genre.name)
                    }
                }
            }
        }
    }
}

@Composable
fun GenreCard(name: String) {
    Surface(
        color = FlixPrimary.copy(alpha = 0.1f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FlixPrimary.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
