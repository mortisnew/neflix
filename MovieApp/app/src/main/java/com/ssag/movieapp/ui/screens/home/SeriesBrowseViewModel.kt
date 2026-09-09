package com.ssag.movieapp.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.ContentRepository
import com.ssag.movieapp.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesBrowseState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val series: List<SeriesDto> = emptyList(),
    val genres: List<GenreDto> = emptyList(),
    val countries: List<CountryDto> = emptyList(),
    val selectedGenre: GenreDto? = null,
    val selectedCountry: CountryDto? = null,
    val selectedSort: String? = null,
    val page: Int = 1,
    val canLoadMore: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SeriesBrowseViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val homeRepository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SeriesBrowseState())
    val state: StateFlow<SeriesBrowseState> = _state.asStateFlow()

    init {
        val initialSort = savedStateHandle.get<String>("sort")
        _state.value = _state.value.copy(selectedSort = initialSort)
        
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val genresResult = homeRepository.getGenres()
            val countriesResult = homeRepository.getCountries()
            
            _state.value = _state.value.copy(
                genres = genresResult.getOrDefault(emptyList()),
                countries = countriesResult.getOrDefault(emptyList())
            )
            
            loadSeries(reset = true)
        }
    }

    fun loadSeries(reset: Boolean = false) {
        if (reset) {
            _state.value = _state.value.copy(page = 1, series = emptyList(), canLoadMore = true, isLoading = true)
        } else {
            if (!_state.value.canLoadMore || _state.value.isPaginationLoading) return
            _state.value = _state.value.copy(isPaginationLoading = true)
        }

        viewModelScope.launch {
            val currentState = _state.value
            val result = contentRepository.getSeries(
                genreId = currentState.selectedGenre?.id,
                countryId = currentState.selectedCountry?.id,
                sort = currentState.selectedSort,
                page = currentState.page
            )

            result.onSuccess { response ->
                val newSeries = if (reset) response.results else currentState.series + response.results
                _state.value = _state.value.copy(
                    series = newSeries,
                    isLoading = false,
                    isPaginationLoading = false,
                    page = currentState.page + 1,
                    canLoadMore = response.next != null,
                    error = null
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isPaginationLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onGenreSelected(genre: GenreDto?) {
        _state.value = _state.value.copy(selectedGenre = genre)
        loadSeries(reset = true)
    }

    fun onCountrySelected(country: CountryDto?) {
        _state.value = _state.value.copy(selectedCountry = country)
        loadSeries(reset = true)
    }

    fun onSortSelected(sort: String?) {
        _state.value = _state.value.copy(selectedSort = sort)
        loadSeries(reset = true)
    }

    fun clearFilters() {
        _state.value = _state.value.copy(
            selectedGenre = null,
            selectedCountry = null,
            selectedSort = null
        )
        loadSeries(reset = true)
    }
}
