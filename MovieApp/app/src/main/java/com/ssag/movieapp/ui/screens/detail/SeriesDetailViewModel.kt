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

data class SeriesDetailState(
    val isLoading: Boolean = false,
    val series: SeriesDto? = null,
    val genres: List<GenreDto> = emptyList(),
    val cast: List<PersonDto> = emptyList(),
    val recommendations: List<SeriesDto> = emptyList(),
    val comments: List<CommentDto> = emptyList(),
    val isFavorited: Boolean = false,
    val isInWatchlist: Boolean = false,
    val userRating: Int? = null,
    val isActionLoading: Boolean = false,
    val qualities: Map<Int, String> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val homeRepository: HomeRepository,
    private val peopleRepository: PeopleRepository,
    private val specialRepository: SpecialRepository,
    private val catalogRepository: CatalogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val seriesId: Int = checkNotNull(savedStateHandle["seriesId"])

    private val _state = MutableStateFlow(SeriesDetailState())
    val state: StateFlow<SeriesDetailState> = _state.asStateFlow()

    init {
        loadSeriesDetail()
    }

    fun loadSeriesDetail() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val seriesResult = contentRepository.getSeriesDetail(seriesId)
            val series = seriesResult.getOrNull()

            if (series != null) {
                launch { fetchGenres() }
                launch { fetchRecommendations() }
                launch { fetchQualities() }
                launch { fetchRolesAndCast() }
                launch { fetchInteractions() }
                launch { fetchComments() }

                _state.value = _state.value.copy(
                    isLoading = false,
                    series = series
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = seriesResult.exceptionOrNull()?.message ?: "Failed to load series"
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
        specialRepository.getSeriesRecommendations(seriesId).onSuccess { recs ->
            _state.value = _state.value.copy(recommendations = recs)
        }
    }

    private suspend fun fetchQualities() {
        catalogRepository.getQualities().onSuccess { qualities ->
            _state.value = _state.value.copy(qualities = qualities.associate { it.id to it.name })
        }
    }

    private suspend fun fetchRolesAndCast() {
        contentRepository.getRoles(seriesId = seriesId).onSuccess { roles ->
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
        val isFavorited = favorites.any { it.series == seriesId }

        val watchlist = specialRepository.getWatchlist().getOrNull()?.results ?: emptyList()
        val isInWatchlist = watchlist.any { it.series == seriesId }

        _state.value = _state.value.copy(
            isFavorited = isFavorited,
            isInWatchlist = isInWatchlist
        )
    }

    private suspend fun fetchComments() {
        contentRepository.getComments(seriesId = seriesId).onSuccess { comments ->
            _state.value = _state.value.copy(comments = comments)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            if (_state.value.isFavorited) {
                val favorites = specialRepository.getFavorites().getOrNull()?.results ?: emptyList()
                val favId = favorites.find { it.series == seriesId }?.id
                if (favId != null) {
                    specialRepository.removeFavorite(favId).onSuccess {
                        _state.value = _state.value.copy(isFavorited = false, isActionLoading = false)
                    }
                }
            } else {
                specialRepository.addFavorite(FavoriteDto(series = seriesId)).onSuccess {
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
                val itemId = watchlist.find { it.series == seriesId }?.id
                if (itemId != null) {
                    specialRepository.removeWatchlist(itemId).onSuccess {
                        _state.value = _state.value.copy(isInWatchlist = false, isActionLoading = false)
                    }
                }
            } else {
                specialRepository.addWatchlist(WatchlistDto(series = seriesId)).onSuccess {
                    _state.value = _state.value.copy(isInWatchlist = true, isActionLoading = false)
                }
            }
        }
    }

    fun submitComment(commentText: String) {
        if (commentText.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            contentRepository.addComment(seriesId = seriesId, comment = commentText).onSuccess {
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
            contentRepository.addRating(rating = rating, seriesId = seriesId).onSuccess {
                _state.value = _state.value.copy(userRating = rating, isActionLoading = false)
            }.onFailure {
                 _state.value = _state.value.copy(isActionLoading = false)
            }
        }
    }

    fun markAsWatched() {
        viewModelScope.launch {
            specialRepository.addRecentlyWatched(seriesId = seriesId)
        }
    }

    fun markEpisodeAsWatched(episodeId: Int) {
        viewModelScope.launch {
            specialRepository.addRecentlyEpisode(episodeId)
        }
    }
}
