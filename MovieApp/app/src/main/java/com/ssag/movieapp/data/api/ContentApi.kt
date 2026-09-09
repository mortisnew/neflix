package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.model.*
import retrofit2.http.*

interface ContentApi {
    @GET("content/movies/")
    suspend fun getMovies(
        @Query("genre") genreId: Int? = null,
        @Query("country") countryId: Int? = null,
        @Query("language") languageId: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null
    ): PaginatedResponse<MovieDto>

    @GET("content/movies/{id}/")
    suspend fun getMovieDetail(@Path("id") id: Int): MovieDto

    @GET("content/series/")
    suspend fun getSeries(
        @Query("genre") genreId: Int? = null,
        @Query("country") countryId: Int? = null,
        @Query("language") languageId: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null
    ): PaginatedResponse<SeriesDto>

    @GET("content/series/{id}/")
    suspend fun getSeriesDetail(@Path("id") id: Int): SeriesDto

    @GET("content/seasons/")
    suspend fun getSeasons(@Query("series") seriesId: Int): List<SeasonDto>

    @GET("content/episodes/")
    suspend fun getEpisodes(@Query("season") seasonId: Int): List<EpisodeDto>

    @GET("content/roles/")
    suspend fun getRoles(
        @Query("movie") movieId: Int? = null,
        @Query("series") seriesId: Int? = null
    ): List<RoleDto>

    @POST("content/comments/")
    suspend fun addComment(
        @Query("movie") movieId: Int? = null,
        @Query("series") seriesId: Int? = null,
        @Body comment: Map<String, String>
    ): CommentDto

    @GET("content/comments/")
    suspend fun getComments(
        @Query("movie") movieId: Int? = null,
        @Query("series") seriesId: Int? = null
    ): PaginatedResponse<CommentDto>

    @POST("content/ratings/")
    suspend fun addRating(
        @Query("movie") movieId: Int? = null,
        @Query("series") seriesId: Int? = null,
        @Body rating: RatingRequest
    ): RatingDto

}
