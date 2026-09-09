package com.ssag.movieapp.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailState(
    val isLoading: Boolean = false,
    val movie: MovieDto? = null,
    val genres: List<GenreDto> = emptyList(),
    val cast: List<PersonDto> = emptyList(),
    val recommendations: List<MovieDto> = emptyList(),
    val comments: List<CommentDto> = emptyList(),
    val isFavorited: Boolean = false,
    val isInWatchlist: Boolean = false,
    val userRating: Int? = null,
    val isActionLoading: Boolean = false,
    val qualities: Map<Int, String> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val homeRepository: HomeRepository,
    private val peopleRepository: PeopleRepository,
    private val specialRepository: SpecialRepository,
    private val catalogRepository: CatalogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    init {
        loadMovieDetail()
    }

    fun loadMovieDetail() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val movieResult = contentRepository.getMovieDetail(movieId)
            val movie = movieResult.getOrNull()
            
            if (movie != null) {
                // Concurrently fetch other data
                launch { fetchGenres() }
                launch { fetchRecommendations() }
                launch { fetchQualities() }
                launch { fetchRolesAndCast() }
                launch { fetchInteractions() }
                launch { fetchComments() }

                _state.value = _state.value.copy(
                    isLoading = false,
                    movie = movie
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = movieResult.exceptionOrNull()?.message ?: "Failed to load movie"
                )
            }
        }
    }

    private suspend fun fetchGenres() {
        homeRepository.getGenres().onSuccess { genres ->
            _state.value = _state.value.copy(genres = genres)
        }
    }

    private suspend fun fetchRecommendations() {
        specialRepository.getRecommendations(movieId = movieId).onSuccess { recs ->
            _state.value = _state.value.copy(recommendations = recs)
        }
    }

    private suspend fun fetchQualities() {
        catalogRepository.getQualities().onSuccess { qualities ->
            _state.value = _state.value.copy(qualities = qualities.associate { it.id to it.name })
        }
    }

    private suspend fun fetchRolesAndCast() {
        contentRepository.getRoles(movieId = movieId).onSuccess { roles ->
            val castList = mutableListOf<PersonDto>()
            roles.take(10).forEach { role ->
                peopleRepository.getPerson(role.person).onSuccess { person ->
                    castList.add(person)
                }
            }
            _state.value = _state.value.copy(cast = castList)
        }
    }

    private suspend fun fetchInteractions() {
        val favorites = specialRepository.getFavorites().getOrNull()?.results ?: emptyList()
        val isFavorited = favorites.any { it.movie == movieId }

        val watchlist = specialRepository.getWatchlist().getOrNull()?.results ?: emptyList()
        val isInWatchlist = watchlist.any { it.movie == movieId }

        _state.value = _state.value.copy(
            isFavorited = isFavorited,
            isInWatchlist = isInWatchlist
        )
    }

    private suspend fun fetchComments() {
        contentRepository.getComments(movieId = movieId).onSuccess { comments ->
            _state.value = _state.value.copy(comments = comments)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            if (_state.value.isFavorited) {
                val favorites = specialRepository.getFavorites().getOrNull()?.results ?: emptyList()
                val favId = favorites.find { it.movie == movieId }?.id
                if (favId != null) {
                    specialRepository.removeFavorite(favId).onSuccess {
                        _state.value = _state.value.copy(isFavorited = false, isActionLoading = false)
                    }
                }
            } else {
                specialRepository.addFavorite(FavoriteDto(movie = movieId)).onSuccess {
                    _state.value = _state.value.copy(isFavorited = true, isActionLoading = false)
                }
            }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            if (_state.value.isInWatchlist) {
                val watchlist = specialRepository.getWatchlist().getOrNull()?.results ?: emptyList()
                val itemId = watchlist.find { it.movie == movieId }?.id
                if (itemId != null) {
                    specialRepository.removeWatchlist(itemId).onSuccess {
                        _state.value = _state.value.copy(isInWatchlist = false, isActionLoading = false)
                    }
                }
            } else {
                specialRepository.addWatchlist(WatchlistDto(movie = movieId)).onSuccess {
                    _state.value = _state.value.copy(isInWatchlist = true, isActionLoading = false)
                }
            }
        }
    }

    fun submitComment(commentText: String) {
        if (commentText.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            contentRepository.addComment(movieId = movieId, comment = commentText).onSuccess {
                fetchComments()
                _state.value = _state.value.copy(isActionLoading = false)
            }.onFailure {
                 _state.value = _state.value.copy(isActionLoading = false)
            }
        }
    }

    fun submitRating(rating: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            contentRepository.addRating(rating = rating, movieId = movieId).onSuccess {
                _state.value = _state.value.copy(userRating = rating, isActionLoading = false)
            }.onFailure {
                 _state.value = _state.value.copy(isActionLoading = false)
            }
        }
    }

    fun markAsWatched() {
        viewModelScope.launch {
            specialRepository.addRecentlyWatched(movieId = movieId)
        }
    }
}
