package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val poster_url: String,
    val genre: List<Int>,
    val country: List<Int>,
    val language: List<Int>,
    val description: String? = null,
    val imdb_rating: String,
    val duration: Int? = null,
    val release_date: String,
    val people: List<Int>,
    val movie_link: List<StreamLinkDto> = emptyList()
)
