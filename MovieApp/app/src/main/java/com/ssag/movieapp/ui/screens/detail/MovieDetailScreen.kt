package com.ssag.movieapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssag.movieapp.data.model.CommentDto
import com.ssag.movieapp.data.model.MovieDto
import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.data.model.StreamLinkDto
import com.ssag.movieapp.ui.screens.detail.components.CastSection
import com.ssag.movieapp.ui.screens.detail.components.PlayerSelectionDialog
import com.ssag.movieapp.ui.screens.detail.components.StreamSelectionDialog
import com.ssag.movieapp.ui.screens.home.components.MovieCard
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface
import com.ssag.movieapp.ui.theme.FlixTextSecondary
import com.ssag.movieapp.util.Constants
import com.ssag.movieapp.utils.PlayerUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onNavigateBack: () -> Unit,
    onPlayMovie: (String, String?) -> Unit,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showPlayerDialog by remember { mutableStateOf(false) }
    var showStreamDialog by remember { mutableStateOf(false) }
    var selectedStream by remember { mutableStateOf<StreamLinkDto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.movie?.title ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = FlixBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = FlixPrimary)
            } else if (state.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.error!!, color = Color.Red)
                    Button(onClick = { viewModel.loadMovieDetail() }, colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary)) {
                        Text("Retry")
                    }
                }
            } else {
                state.movie?.let { movie ->
                    MovieDetailContent(
                        movie = movie,
                        cast = state.cast,
                        recommendations = state.recommendations,
                        comments = state.comments,
                        isFavorited = state.isFavorited,
                        isInWatchlist = state.isInWatchlist,
                        userRating = state.userRating,
                        onFavoriteToggle = { viewModel.toggleFavorite() },
                        onWatchlistToggle = { viewModel.toggleWatchlist() },
                        onPlayClick = {
                            if (movie.movie_link.size > 1) {
                                showStreamDialog = true
                            } else {
                                selectedStream = movie.movie_link.firstOrNull()
                                showPlayerDialog = true
                            }
                        },
                        onMovieClick = onNavigateToMovieDetail,
                        onAddComment = { viewModel.submitComment(it) },
                        onRate = { viewModel.submitRating(it) }
                    )

                    if (showStreamDialog) {
                        StreamSelectionDialog(
                            streams = movie.movie_link,
                            qualities = state.qualities,
                            onDismiss = { showStreamDialog = false },
                            onStreamSelected = {
                                selectedStream = it
                                showPlayerDialog = true
                            }
                        )
                    }

                    if (showPlayerDialog && selectedStream != null) {
                        PlayerSelectionDialog(
                            onDismiss = { showPlayerDialog = false },
                            onSelectInternal = {
                                viewModel.markAsWatched()
                                onPlayMovie(selectedStream!!.url, selectedStream!!.sub_file)
                            },
                            onSelectVLC = {
                                viewModel.markAsWatched()
                                PlayerUtils.openWithVLC(context, selectedStream!!.url, movie.title)
                            },
                            onSelectKMPlayer = {
                                viewModel.markAsWatched()
                                PlayerUtils.openWithKMPlayer(context, selectedStream!!.url)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: MovieDto,
    cast: List<PersonDto>,
    recommendations: List<MovieDto>,
    comments: List<CommentDto>,
    isFavorited: Boolean,
    isInWatchlist: Boolean,
    userRating: Int?,
    onFavoriteToggle: () -> Unit,
    onWatchlistToggle: () -> Unit,
    onPlayClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onAddComment: (String) -> Unit,
    onRate: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            MovieHeader(movie)
        }

        item {
            MovieActionButtons(
                isFavorited = isFavorited,
                isInWatchlist = isInWatchlist,
                onPlayClick = onPlayClick,
                onFavoriteToggle = onFavoriteToggle,
                onWatchlistToggle = onWatchlistToggle
            )
        }

        item {
            com.ssag.movieapp.ui.screens.detail.components.RatingSection(imdbRating = movie.imdb_rating, userRating = userRating, onRate = onRate)
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = movie.description ?: "No description available.", style = MaterialTheme.typography.bodyLarge, color = Color.LightGray)
            }
        }

        item { CastSection(cast = cast) }

        if (recommendations.isNotEmpty()) {
            item {
                Text(text = "Recommendations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(16.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recommendations) { rec ->
                        MovieCard(movie = rec, onClick = onMovieClick)
                    }
                }
            }
        }

        item {
            com.ssag.movieapp.ui.screens.detail.components.CommentsSection(comments = comments, onAddComment = onAddComment)
        }
        
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun MovieHeader(movie: MovieDto) {
    val posterUrl = "${Constants.BASE_URL}content/movies/${movie.id}/poster_url/"
    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
        AsyncImage(model = posterUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().blur(20.dp))
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, FlixBackground), startY = 300f)))
        Row(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Surface(shape = RoundedCornerShape(8.dp), modifier = Modifier.width(140.dp).height(210.dp), shadowElevation = 8.dp) {
                AsyncImage(model = posterUrl, contentDescription = movie.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.height(210.dp), verticalArrangement = Arrangement.Bottom) {
                Text(text = movie.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Year: ${movie.release_date.take(4)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                movie.duration?.let { Text(text = "Duration: $it min", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) }
            }
        }
    }
}

@Composable
fun MovieActionButtons(
    isFavorited: Boolean,
    isInWatchlist: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onWatchlistToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPlayClick,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Play Movie", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Surface(
            modifier = Modifier
                .size(54.dp)
                .clickable { onFavoriteToggle() },
            color = FlixSurface,
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorited) FlixPrimary else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(54.dp)
                .clickable { onWatchlistToggle() },
            color = FlixSurface,
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Watchlist",
                    tint = if (isInWatchlist) FlixPrimary else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
