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
import com.ssag.movieapp.data.model.EpisodeDto
import com.ssag.movieapp.data.model.SeriesDto
import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.data.model.StreamLinkDto
import com.ssag.movieapp.ui.screens.detail.components.CastSection
import com.ssag.movieapp.ui.screens.detail.components.PlayerSelectionDialog
import com.ssag.movieapp.ui.screens.detail.components.StreamSelectionDialog
import com.ssag.movieapp.ui.screens.home.components.SeriesCard
import com.ssag.movieapp.ui.theme.FlixBackground
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface
import com.ssag.movieapp.util.Constants
import com.ssag.movieapp.utils.PlayerUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    viewModel: SeriesDetailViewModel,
    onNavigateBack: () -> Unit,
    onPlayEpisode: (String, String?) -> Unit,
    onNavigateToSeriesDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showPlayerDialog by remember { mutableStateOf(false) }
    var showStreamDialog by remember { mutableStateOf(false) }
    var selectedStream by remember { mutableStateOf<StreamLinkDto?>(null) }
    var selectedEpisodeId by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.series?.title ?: "Series Detail") },
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
                    Button(onClick = { viewModel.loadSeriesDetail() }, colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary)) {
                        Text("Retry")
                    }
                }
            } else {
                state.series?.let { series ->
                    SeriesDetailContent(
                        series = series,
                        cast = state.cast,
                        recommendations = state.recommendations,
                        comments = state.comments,
                        isFavorited = state.isFavorited,
                        isInWatchlist = state.isInWatchlist,
                        userRating = state.userRating,
                        onFavoriteToggle = { viewModel.toggleFavorite() },
                        onWatchlistToggle = { viewModel.toggleWatchlist() },
                        onPlayEpisode = { url, subtitleUrl, id, links ->
                            selectedEpisodeId = id
                            if (links.size > 1) {
                                showStreamDialog = true
                            } else {
                                selectedStream = links.firstOrNull()
                                showPlayerDialog = true
                            }
                        },
                        onSeriesClick = onNavigateToSeriesDetail,
                        onAddComment = { viewModel.submitComment(it) },
                        onRate = { viewModel.submitRating(it) }
                    )

                    if (showStreamDialog) {
                        val currentSeason = series.season_link.find { season -> 
                            season.episode_season.any { it.id == selectedEpisodeId }
                        }
                        val episode = currentSeason?.episode_season?.find { it.id == selectedEpisodeId }
                        
                        StreamSelectionDialog(
                            streams = episode?.episode_link ?: emptyList(),
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
                                viewModel.markEpisodeAsWatched(selectedEpisodeId)
                                onPlayEpisode(selectedStream!!.url, selectedStream!!.sub_file)
                            },
                            onSelectVLC = {
                                viewModel.markAsWatched()
                                viewModel.markEpisodeAsWatched(selectedEpisodeId)
                                PlayerUtils.openWithVLC(context, selectedStream!!.url, series.title)
                            },
                            onSelectKMPlayer = {
                                viewModel.markAsWatched()
                                viewModel.markEpisodeAsWatched(selectedEpisodeId)
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
fun SeriesDetailContent(
    series: SeriesDto,
    cast: List<PersonDto>,
    recommendations: List<SeriesDto>,
    comments: List<CommentDto>,
    isFavorited: Boolean,
    isInWatchlist: Boolean,
    userRating: Int?,
    onFavoriteToggle: () -> Unit,
    onWatchlistToggle: () -> Unit,
    onPlayEpisode: (String, String?, Int, List<StreamLinkDto>) -> Unit,
    onSeriesClick: (Int) -> Unit,
    onAddComment: (String) -> Unit,
    onRate: (Int) -> Unit
) {
    var selectedSeasonIndex by remember { mutableIntStateOf(0) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SeriesHeader(series) }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onFavoriteToggle, colors = IconButtonDefaults.iconButtonColors(contentColor = if (isFavorited) Color.Red else Color.White)) {
                    Icon(if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                }
                IconButton(onClick = onWatchlistToggle, colors = IconButtonDefaults.iconButtonColors(contentColor = if (isInWatchlist) FlixPrimary else Color.White)) {
                    Icon(if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = "Watchlist")
                }
            }
        }

        item {
            com.ssag.movieapp.ui.screens.detail.components.RatingSection(imdbRating = series.imdb_rating, userRating = userRating, onRate = onRate)
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = series.description ?: "No description available.", style = MaterialTheme.typography.bodyLarge, color = Color.LightGray)
            }
        }

        item { CastSection(cast = cast) }

        if (series.season_link.isNotEmpty()) {
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedSeasonIndex,
                    containerColor = Color.Transparent,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    series.season_link.forEachIndexed { index, season ->
                        Tab(
                            selected = selectedSeasonIndex == index,
                            onClick = { selectedSeasonIndex = index },
                            text = { Text("Season ${season.season_number}") },
                            selectedContentColor = FlixPrimary,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }

            val currentSeason = series.season_link.getOrNull(selectedSeasonIndex)
            if (currentSeason != null) {
                items(currentSeason.episode_season) { episode ->
                    EpisodeItem(
                        episode = episode,
                        onPlayClick = {
                            val stream = episode.episode_link.firstOrNull()
                            stream?.let {
                                onPlayEpisode(it.url, it.sub_file, episode.id, episode.episode_link)
                            }
                        }
                    )
                }
            }
        }

        if (recommendations.isNotEmpty()) {
            item {
                Text(text = "Recommendations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(16.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recommendations) { rec ->
                        SeriesCard(series = rec, onClick = onSeriesClick)
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
fun SeriesHeader(series: SeriesDto) {
    val posterUrl = "${Constants.BASE_URL}content/series/${series.id}/poster_url/"
    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
        AsyncImage(model = posterUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().blur(20.dp))
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, FlixBackground), startY = 250f)))
        Row(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Surface(shape = RoundedCornerShape(8.dp), modifier = Modifier.width(120.dp).height(180.dp), shadowElevation = 8.dp) {
                AsyncImage(model = posterUrl, contentDescription = series.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.height(180.dp), verticalArrangement = Arrangement.Bottom) {
                Text(text = series.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "First Aired: ${series.release_date.take(4)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeItem(episode: EpisodeDto, onPlayClick: () -> Unit) {
    Surface(
        onClick = onPlayClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = FlixSurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(FlixPrimary, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Episode ${episode.episode_number}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
