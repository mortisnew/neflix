package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)

@Serializable
data class CountryDto(
    val id: Int,
    val name: String
)

@Serializable
data class LanguageDto(
    val id: Int,
    val name: String
)

@Serializable
data class QualityDto(
    val id: Int,
    val name: String
)
