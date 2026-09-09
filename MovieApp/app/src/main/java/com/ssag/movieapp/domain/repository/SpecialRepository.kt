package com.ssag.movieapp.domain.repository

import com.ssag.movieapp.data.model.*

interface SpecialRepository {
    suspend fun getFavorites(): Result<PaginatedResponse<FavoriteDto>>
    suspend fun addFavorite(favorite: FavoriteDto): Result<FavoriteDto>
    suspend fun removeFavorite(id: Int): Result<Unit>
    suspend fun getWatchlist(): Result<PaginatedResponse<WatchlistDto>>
    suspend fun addWatchlist(watchlist: WatchlistDto): Result<WatchlistDto>
    suspend fun removeWatchlist(id: Int): Result<Unit>
    suspend fun getRecentlyWatched(): Result<List<RecentlyWatchedDto>>
    suspend fun addRecentlyWatched(movieId: Int? = null, seriesId: Int? = null): Result<RecentlyWatchedDto>
    suspend fun getRecentlyEpisodes(): Result<List<RecentlyEpisodeDto>>
    suspend fun addRecentlyEpisode(episodeId: Int): Result<RecentlyEpisodeDto>
    suspend fun getFolders(): Result<List<FolderDto>>
    suspend fun getFolderItems(folderId: Int): Result<List<FolderItemDto>>
    suspend fun getRecommendations(movieId: Int? = null, seriesId: Int? = null): Result<List<MovieDto>>
    suspend fun getSeriesRecommendations(seriesId: Int): Result<List<SeriesDto>>
}
