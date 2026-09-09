package com.ssag.movieapp.data.repository

import com.ssag.movieapp.data.api.CatalogApi
import com.ssag.movieapp.data.model.CountryDto
import com.ssag.movieapp.data.model.GenreDto
import com.ssag.movieapp.data.model.LanguageDto
import com.ssag.movieapp.data.model.QualityDto
import com.ssag.movieapp.domain.repository.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val catalogApi: CatalogApi
) : CatalogRepository {
    override suspend fun getLanguages(): Result<List<LanguageDto>> = try {
        Result.success(catalogApi.getLanguages())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCountries(): Result<List<CountryDto>> = try {
        Result.success(catalogApi.getCountries())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getGenres(): Result<List<GenreDto>> = try {
        Result.success(catalogApi.getGenres())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getQualities(): Result<List<QualityDto>> = try {
        Result.success(catalogApi.getQualities())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
