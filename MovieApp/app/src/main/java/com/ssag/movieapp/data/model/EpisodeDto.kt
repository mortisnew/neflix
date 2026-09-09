package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDto(
    val id: Int,
    val episode_number: Int,
    val episode_link: List<StreamLinkDto> = emptyList()
)
