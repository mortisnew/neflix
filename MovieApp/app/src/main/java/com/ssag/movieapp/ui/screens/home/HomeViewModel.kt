package com.ssag.movieapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.HomeRepository
import com.ssag.movieapp.domain.repository.PeopleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val popularMovies: List<MovieDto> = emptyList(),
    val popularSeries: List<SeriesDto> = emptyList(),
    val topRatedMovies: List<MovieDto> = emptyList(),
    val topRatedSeries: List<SeriesDto> = emptyList(),
    val latestMovies: List<MovieDto> = emptyList(),
    val latestSeries: List<SeriesDto> = emptyList(),
    val genres: List<GenreDto> = emptyList(),
    val countries: List<CountryDto> = emptyList(),
    val people: List<PersonDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val peopleRepository: PeopleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val popularMoviesDef = async { homeRepository.getPopularMovies() }
                val popularSeriesDef = async { homeRepository.getPopularSeries() }
                val topRatedMoviesDef = async { homeRepository.getTopRatedMovies() }
                val topRatedSeriesDef = async { homeRepository.getTopRatedSeries() }
                val latestMoviesDef = async { homeRepository.getLatestMovies() }
                val latestSeriesDef = async { homeRepository.getLatestSeries() }
                val genresDef = async { homeRepository.getGenres() }
                val countriesDef = async { homeRepository.getCountries() }
                val peopleDef = async { peopleRepository.getPeople() }

                val popularMoviesRes = popularMoviesDef.await()
                val popularSeriesRes = popularSeriesDef.await()
                val topRatedMoviesRes = topRatedMoviesDef.await()
                val topRatedSeriesRes = topRatedSeriesDef.await()
                val latestMoviesRes = latestMoviesDef.await()
                val latestSeriesRes = latestSeriesDef.await()
                val genresRes = genresDef.await()
                val countriesRes = countriesDef.await()
                val peopleRes = peopleDef.await()

                if (popularMoviesRes.isFailure || popularSeriesRes.isFailure) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to load some content. Please check your connection."
                    )
                    return@launch
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    popularMovies = popularMoviesRes.getOrNull()?.results ?: emptyList(),
                    popularSeries = popularSeriesRes.getOrNull()?.results ?: emptyList(),
                    topRatedMovies = topRatedMoviesRes.getOrNull()?.results ?: emptyList(),
                    topRatedSeries = topRatedSeriesRes.getOrNull()?.results ?: emptyList(),
                    latestMovies = latestMoviesRes.getOrNull()?.results ?: emptyList(),
                    latestSeries = latestSeriesRes.getOrNull()?.results ?: emptyList(),
                    genres = genresRes.getOrDefault(emptyList()),
                    countries = countriesRes.getOrDefault(emptyList()),
                    people = peopleRes.getOrNull()?.results ?: emptyList()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
