package com.ssag.movieapp.domain.repository

import com.ssag.movieapp.data.model.CountryDto
import com.ssag.movieapp.data.model.GenreDto
import com.ssag.movieapp.data.model.LanguageDto
import com.ssag.movieapp.data.model.QualityDto

interface CatalogRepository {
    suspend fun getLanguages(): Result<List<LanguageDto>>
    suspend fun getCountries(): Result<List<CountryDto>>
    suspend fun getGenres(): Result<List<GenreDto>>
    suspend fun getQualities(): Result<List<QualityDto>>
}
