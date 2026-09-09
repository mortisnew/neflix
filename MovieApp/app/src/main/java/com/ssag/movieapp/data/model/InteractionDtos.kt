package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RatingDto(
    val id: Int? = null,
    val user: Int? = null,
    val rating: Int,
    val movie: Int? = null,
    val series: Int? = null
)

@Serializable
data class RatingRequest(
    val rating: Int
)

@Serializable
data class CommentDto(
    val id: Int? = null,
    val user: Int? = null,
    val username: String?,
    val movie: Int? = null,
    val series: Int? = null,
    val comment: String
)
