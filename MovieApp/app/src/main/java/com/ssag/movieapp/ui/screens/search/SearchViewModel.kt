package com.ssag.movieapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.MovieDto
import com.ssag.movieapp.data.model.SeriesDto
import com.ssag.movieapp.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val movies: List<MovieDto> = emptyList(),
    val series: List<SeriesDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _state.value = _state.value.copy(movies = emptyList(), series = emptyList(), isLoading = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            search()
        }
    }

    private suspend fun search() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val query = _state.value.query
        
        try {
            val moviesResult = contentRepository.getMovies(search = query)
            val seriesResult = contentRepository.getSeries(search = query)
            
            _state.value = _state.value.copy(
                isLoading = false,
                movies = moviesResult.getOrNull()?.results ?: emptyList(),
                series = seriesResult.getOrNull()?.results ?: emptyList(),
                error = if (moviesResult.isFailure && seriesResult.isFailure) "Search failed" else null
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(isLoading = false, error = e.message)
        }
    }
}
