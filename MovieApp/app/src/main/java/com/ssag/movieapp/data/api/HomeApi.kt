package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.model.*
import retrofit2.http.GET

interface HomeApi {
    @GET("home/popular-movies/")
    suspend fun getPopularMovies(): PaginatedResponse<MovieDto>

    @GET("home/popular-series/")
    suspend fun getPopularSeries(): PaginatedResponse<SeriesDto>

    @GET("home/top-rated-movies/")
    suspend fun getTopRatedMovies(): PaginatedResponse<MovieDto>

    @GET("home/top-rated-series/")
    suspend fun getTopRatedSeries(): PaginatedResponse<SeriesDto>

    @GET("home/latest-movies/")
    suspend fun getLatestMovies(): PaginatedResponse<MovieDto>

    @GET("home/latest-series/")
    suspend fun getLatestSeries(): PaginatedResponse<SeriesDto>

    @GET("catalog/genres/")
    suspend fun getGenres(): PaginatedResponse<GenreDto>

    @GET("catalog/countries/")
    suspend fun getCountries(): PaginatedResponse<CountryDto>

    @GET("catalog/languages/")
    suspend fun getLanguages(): PaginatedResponse<LanguageDto>
}
