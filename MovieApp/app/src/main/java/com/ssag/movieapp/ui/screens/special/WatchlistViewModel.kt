package com.ssag.movieapp.ui.screens.special

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.ContentRepository
import com.ssag.movieapp.domain.repository.SpecialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WatchlistState(
    val isLoading: Boolean = false,
    val watchlistMovies: List<MovieDto> = emptyList(),
    val watchlistSeries: List<SeriesDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val specialRepository: SpecialRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state.asStateFlow()

    init {
        loadWatchlist()
    }

    fun loadWatchlist() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = specialRepository.getWatchlist()
            
            result.onSuccess { paginated ->
                val items = paginated.results
                
                val movieDefs = items.filter { it.movie != null }.map { 
                    async { contentRepository.getMovieDetail(it.movie!!) }
                }
                val seriesDefs = items.filter { it.series != null }.map { 
                    async { contentRepository.getSeriesDetail(it.series!!) }
                }

                val movies = movieDefs.awaitAll().mapNotNull { it.getOrNull() }
                val series = seriesDefs.awaitAll().mapNotNull { it.getOrNull() }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    watchlistMovies = movies,
                    watchlistSeries = series
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
