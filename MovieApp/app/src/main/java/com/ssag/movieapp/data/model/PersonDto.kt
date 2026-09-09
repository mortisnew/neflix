package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val id: Int,
    val full_name: String,
    val avatar_url: String,
    val bio: String
)

@Serializable
data class RoleDto(
    val id: Int,
    val person: Int,
    val role_type: String, // actor, director, writer
    val movie: Int? = null,
    val series: Int? = null
)
