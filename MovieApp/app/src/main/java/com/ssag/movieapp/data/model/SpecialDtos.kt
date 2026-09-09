package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    val id: Int? = null,
    val user: Int? = null,
    val movie: Int? = null,
    val series: Int? = null
)

@Serializable
data class WatchlistDto(
    val id: Int? = null,
    val user: Int? = null,
    val movie: Int? = null,
    val series: Int? = null
)

@Serializable
data class RecentlyWatchedDto(
    val id: Int? = null,
    val user: Int? = null,
    val movie: Int? = null,
    val series: Int? = null,
    val watched_at: String? = null
)

@Serializable
data class RecentlyEpisodeDto(
    val id: Int? = null,
    val user: Int? = null,
    val episode: Int,
    val watched_at: String? = null
)

@Serializable
data class FolderDto(
    val id: Int? = null,
    val title: String
)

@Serializable
data class FolderItemDto(
    val id: Int? = null,
    val folder: Int,
    val movie: Int? = null,
    val series: Int? = null
)
