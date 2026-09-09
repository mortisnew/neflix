package com.ssag.movieapp.data.repository

import com.ssag.movieapp.data.api.SpecialApi
import com.ssag.movieapp.data.model.*
import com.ssag.movieapp.domain.repository.SpecialRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpecialRepositoryImpl @Inject constructor(
    private val specialApi: SpecialApi
) : SpecialRepository {

    override suspend fun getFavorites(): Result<PaginatedResponse<FavoriteDto>> = try {
        Result.success(specialApi.getFavorites())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addFavorite(favorite: FavoriteDto): Result<FavoriteDto> = try {
        Result.success(specialApi.addFavorite(favorite))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun removeFavorite(id: Int): Result<Unit> = try {
        specialApi.removeFavorite(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getWatchlist(): Result<PaginatedResponse<WatchlistDto>> = try {
        Result.success(specialApi.getWatchlist())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addWatchlist(watchlist: WatchlistDto): Result<WatchlistDto> = try {
        Result.success(specialApi.addWatchlist(watchlist))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun removeWatchlist(id: Int): Result<Unit> = try {
        specialApi.removeWatchlist(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentlyWatched(): Result<List<RecentlyWatchedDto>> = try {
        Result.success(specialApi.getRecentlyWatched().results)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addRecentlyWatched(movieId: Int?, seriesId: Int?): Result<RecentlyWatchedDto> = try {
        Result.success(specialApi.addRecentlyWatched(RecentlyWatchedDto(movie = movieId, series = seriesId)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentlyEpisodes(): Result<List<RecentlyEpisodeDto>> = try {
        Result.success(specialApi.getRecentlyEpisodes().results)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addRecentlyEpisode(episodeId: Int): Result<RecentlyEpisodeDto> = try {
        Result.success(specialApi.addRecentlyEpisode(RecentlyEpisodeDto(episode = episodeId)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFolders(): Result<List<FolderDto>> = try {
        Result.success(specialApi.getFolders())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFolderItems(folderId: Int): Result<List<FolderItemDto>> = try {
        Result.success(specialApi.getFolderItems(folderId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecommendations(movieId: Int?, seriesId: Int?): Result<List<MovieDto>> = try {
        Result.success(specialApi.getRecommendations(movieId, seriesId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSeriesRecommendations(seriesId: Int): Result<List<SeriesDto>> = try {
        Result.success(specialApi.getSeriesRecommendations(seriesId))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
