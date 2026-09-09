package com.ssag.movieapp.data.repository

import com.ssag.movieapp.data.api.ContentApi
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.ContentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val contentApi: ContentApi
) : ContentRepository {

    override suspend fun getMovies(
        genreId: Int?,
        countryId: Int?,
        languageId: Int?,
        sort: String?,
        search: String?,
        page: Int?
    ): Result<PaginatedResponse<MovieDto>> = try {
        Result.success(contentApi.getMovies(genreId, countryId, languageId, sort, search, page))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDto> = try {
        Result.success(contentApi.getMovieDetail(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSeries(
        genreId: Int?,
        countryId: Int?,
        languageId: Int?,
        sort: String?,
        search: String?,
        page: Int?
    ): Result<PaginatedResponse<SeriesDto>> = try {
        Result.success(contentApi.getSeries(genreId, countryId, languageId, sort, search, page))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSeriesDetail(id: Int): Result<SeriesDto> = try {
        Result.success(contentApi.getSeriesDetail(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSeasons(seriesId: Int): Result<List<SeasonDto>> = try {
        Result.success(contentApi.getSeasons(seriesId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getEpisodes(seasonId: Int): Result<List<EpisodeDto>> = try {
        Result.success(contentApi.getEpisodes(seasonId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRoles(movieId: Int?, seriesId: Int?): Result<List<RoleDto>> = try {
        Result.success(contentApi.getRoles(movieId, seriesId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addComment(movieId: Int?, seriesId: Int?, comment: String): Result<CommentDto> = try {
        Result.success(contentApi.addComment(movieId, seriesId, mapOf("comment" to comment)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getComments(
        movieId: Int?,
        seriesId: Int?
    ): Result<List<CommentDto>> = try {
        Result.success(
            contentApi.getComments(movieId, seriesId).results
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addRating(
        rating: Int,
        movieId: Int?,
        seriesId: Int?
    ): Result<RatingDto> = try {
        Result.success(
            contentApi.addRating(
                movieId = movieId,
                seriesId = seriesId,
                rating = RatingRequest(rating = rating)
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}
