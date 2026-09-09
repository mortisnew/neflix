package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(
    val access: String,
    val refresh: String
)

@Serializable
data class TokenRefreshRequestDto(
    val refresh: String
)

@Serializable
data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)
