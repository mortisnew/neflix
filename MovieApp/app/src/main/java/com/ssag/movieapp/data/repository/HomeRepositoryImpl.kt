package com.ssag.movieapp.data.repository

import com.ssag.movieapp.data.api.HomeApi
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi
) : HomeRepository {

    override suspend fun getPopularMovies(): Result<PaginatedResponse<MovieDto>> {
        return try {
            Result.success(homeApi.getPopularMovies())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPopularSeries(): Result<PaginatedResponse<SeriesDto>> {
        return try {
            Result.success(homeApi.getPopularSeries())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopRatedMovies(): Result<PaginatedResponse<MovieDto>> {
        return try {
            Result.success(homeApi.getTopRatedMovies())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopRatedSeries(): Result<PaginatedResponse<SeriesDto>> {
        return try {
            Result.success(homeApi.getTopRatedSeries())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestMovies(): Result<PaginatedResponse<MovieDto>> {
        return try {
            Result.success(homeApi.getLatestMovies())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestSeries(): Result<PaginatedResponse<SeriesDto>> {
        return try {
            Result.success(homeApi.getLatestSeries())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGenres(): Result<List<GenreDto>> = try {
        Result.success(homeApi.getGenres().results)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCountries(): Result<List<CountryDto>> = try {
        Result.success(homeApi.getCountries().results)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getLanguages(): Result<List<LanguageDto>> {
        return try {
            Result.success(homeApi.getLanguages().results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
