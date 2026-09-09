package com.ssag.movieapp.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.MovieDto
import com.ssag.movieapp.data.model.SeriesDto
import com.ssag.movieapp.domain.repository.ContentRepository
import com.ssag.movieapp.domain.repository.SpecialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderDetailState(
    val isLoading: Boolean = false,
    val movies: List<MovieDto> = emptyList(),
    val series: List<SeriesDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FolderDetailViewModel @Inject constructor(
    private val specialRepository: SpecialRepository,
    private val contentRepository: ContentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(FolderDetailState())
    val state: StateFlow<FolderDetailState> = _state.asStateFlow()

    private val folderId: Int = checkNotNull(savedStateHandle["folderId"])

    init {
        loadFolderItems()
    }

    fun loadFolderItems() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = specialRepository.getFolderItems(folderId)
            
            result.onSuccess { items ->
                val movieDefs = items.filter { it.movie != null }.map { 
                    async { contentRepository.getMovieDetail(it.movie!!) }
                }
                val seriesDefs = items.filter { it.series != null }.map { 
                    async { contentRepository.getSeriesDetail(it.series!!) }
                }

                val movies = movieDefs.mapNotNull { it.await().getOrNull() }
                val series = seriesDefs.mapNotNull { it.await().getOrNull() }

                _state.value = _state.value.copy(
                    isLoading = false,
                    movies = movies,
                    series = series
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
