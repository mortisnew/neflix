package com.ssag.movieapp.domain.repository

import com.ssag.movieapp.data.model.*

interface HomeRepository {
    suspend fun getPopularMovies(): Result<PaginatedResponse<MovieDto>>
    suspend fun getPopularSeries(): Result<PaginatedResponse<SeriesDto>>
    suspend fun getTopRatedMovies(): Result<PaginatedResponse<MovieDto>>
    suspend fun getTopRatedSeries(): Result<PaginatedResponse<SeriesDto>>
    suspend fun getLatestMovies(): Result<PaginatedResponse<MovieDto>>
    suspend fun getLatestSeries(): Result<PaginatedResponse<SeriesDto>>
    suspend fun getGenres(): Result<List<GenreDto>>
    suspend fun getCountries(): Result<List<CountryDto>>
    suspend fun getLanguages(): Result<List<LanguageDto>>
}
