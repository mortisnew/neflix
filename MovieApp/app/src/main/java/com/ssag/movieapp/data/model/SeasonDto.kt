package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SeasonDto(
    val id: Int,
    val season_number: Int,
    val episode_season: List<EpisodeDto> = emptyList()
)
