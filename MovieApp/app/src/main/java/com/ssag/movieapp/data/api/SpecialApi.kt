package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.model.*
import retrofit2.http.*

interface SpecialApi {
    @GET("special/favorites/")
    suspend fun getFavorites(): PaginatedResponse<FavoriteDto>

    @POST("special/favorites/")
    suspend fun addFavorite(@Body favorite: FavoriteDto): FavoriteDto

    @DELETE("special/favorites/{id}/")
    suspend fun removeFavorite(@Path("id") id: Int)

    @GET("special/watchlist/")
    suspend fun getWatchlist(): PaginatedResponse<WatchlistDto>

    @POST("special/watchlist/")
    suspend fun addWatchlist(@Body watchlist: WatchlistDto): WatchlistDto

    @DELETE("special/watchlist/{id}/")
    suspend fun removeWatchlist(@Path("id") id: Int)

    @GET("special/recently-watched/")
    suspend fun getRecentlyWatched(): PaginatedResponse<RecentlyWatchedDto>

    @POST("special/recently-watched/")
    suspend fun addRecentlyWatched(@Body recentlyWatched: RecentlyWatchedDto): RecentlyWatchedDto

    @GET("special/recently-episodes/")
    suspend fun getRecentlyEpisodes(): PaginatedResponse<RecentlyEpisodeDto>

    @POST("special/recently-episodes/")
    suspend fun addRecentlyEpisode(@Body recentlyEpisode: RecentlyEpisodeDto): RecentlyEpisodeDto

    @GET("special/folders/")
    suspend fun getFolders(): List<FolderDto>

    @GET("special/folder-item/")
    suspend fun getFolderItems(@Query("folder") folderId: Int): List<FolderItemDto>

    @GET("special/recommendation/")
    suspend fun getRecommendations(
        @Query("movie_id") movieId: Int? = null,
        @Query("series_id") seriesId: Int? = null
    ): List<MovieDto> // Note: Backend RecommendationViewSet returns either MovieSerializer or SeriesSerializer
    
    @GET("special/recommendation/")
    suspend fun getSeriesRecommendations(
        @Query("series_id") seriesId: Int
    ): List<SeriesDto>
}
