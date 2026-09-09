package com.ssag.movieapp.domain.repository

import com.ssag.movieapp.data.model.*

interface ContentRepository {
    suspend fun getMovies(
        genreId: Int? = null,
        countryId: Int? = null,
        languageId: Int? = null,
        sort: String? = null,
        search: String? = null,
        page: Int? = null
    ): Result<PaginatedResponse<MovieDto>>

    suspend fun getMovieDetail(id: Int): Result<MovieDto>

    suspend fun getSeries(
        genreId: Int? = null,
        countryId: Int? = null,
        languageId: Int? = null,
        sort: String? = null,
        search: String? = null,
        page: Int? = null
    ): Result<PaginatedResponse<SeriesDto>>

    suspend fun getSeriesDetail(id: Int): Result<SeriesDto>

    suspend fun getSeasons(seriesId: Int): Result<List<SeasonDto>>

    suspend fun getEpisodes(seasonId: Int): Result<List<EpisodeDto>>

    suspend fun getRoles(movieId: Int? = null, seriesId: Int? = null): Result<List<RoleDto>>

    suspend fun addComment(movieId: Int? = null, seriesId: Int? = null, comment: String): Result<CommentDto>

    suspend fun getComments(movieId: Int? = null, seriesId: Int? = null): Result<List<CommentDto>>

    suspend fun addRating(rating: Int, movieId: Int? = null, seriesId: Int? = null): Result<RatingDto>
}
