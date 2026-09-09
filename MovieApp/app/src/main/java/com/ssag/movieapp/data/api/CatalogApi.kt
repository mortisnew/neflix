package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.model.CountryDto
import com.ssag.movieapp.data.model.GenreDto
import com.ssag.movieapp.data.model.LanguageDto
import com.ssag.movieapp.data.model.QualityDto
import retrofit2.http.GET

interface CatalogApi {
    @GET("catalog/languages/")
    suspend fun getLanguages(): List<LanguageDto>

    @GET("catalog/countries/")
    suspend fun getCountries(): List<CountryDto>

    @GET("catalog/genres/")
    suspend fun getGenres(): List<GenreDto>

    @GET("catalog/qualities/")
    suspend fun getQualities(): List<QualityDto>
}
