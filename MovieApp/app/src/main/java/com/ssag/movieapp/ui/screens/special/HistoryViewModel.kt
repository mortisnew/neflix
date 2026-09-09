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

data class HistoryState(
    val isLoading: Boolean = false,
    val movieHistory: List<MovieDto> = emptyList(),
    val seriesHistory: List<SeriesDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val specialRepository: SpecialRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = specialRepository.getRecentlyWatched()
            
            result.onSuccess { historyItems ->
                val movieDefs = historyItems.filter { it.movie != null }.map { 
                    async { contentRepository.getMovieDetail(it.movie!!) }
                }
                val seriesDefs = historyItems.filter { it.series != null }.map { 
                    async { contentRepository.getSeriesDetail(it.series!!) }
                }

                val movies = movieDefs.awaitAll().mapNotNull { it.getOrNull() }
                val series = seriesDefs.awaitAll().mapNotNull { it.getOrNull() }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    movieHistory = movies,
                    seriesHistory = series
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
