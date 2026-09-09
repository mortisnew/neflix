package com.ssag.movieapp.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssag.movieapp.data.model.MovieDto
import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.data.model.SeriesDto
import com.ssag.movieapp.domain.repository.ContentRepository
import com.ssag.movieapp.domain.repository.PeopleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonDetailState(
    val isLoading: Boolean = false,
    val person: PersonDto? = null,
    val movies: List<MovieDto> = emptyList(),
    val series: List<SeriesDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val contentRepository: ContentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val personId: Int = checkNotNull(savedStateHandle["personId"])

    private val _state = MutableStateFlow(PersonDetailState())
    val state: StateFlow<PersonDetailState> = _state.asStateFlow()

    init {
        loadPersonDetail()
    }

    fun loadPersonDetail() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = peopleRepository.getPerson(personId)
            
            result.onSuccess { person ->
                _state.value = _state.value.copy(person = person)
                
                // Fetch related content using search as a fallback if no direct mapping exists
                // The backend likely supports searching for movies where the person is cast
                launch {
                    contentRepository.getMovies(search = person.full_name).onSuccess { response ->
                        _state.value = _state.value.copy(movies = response.results)
                    }
                }
                launch {
                    contentRepository.getSeries(search = person.full_name).onSuccess { response ->
                        _state.value = _state.value.copy(series = response.results)
                    }
                }
                
                _state.value = _state.value.copy(isLoading = false)
            }.onFailure { e ->
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
