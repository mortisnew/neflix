package com.ssag.movieapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StreamLinkDto(
    val id: Int,
    val quality: Int,
    val encoder: String,
    val size_mb: Int,
    val sub_chose: String,
    val sub_file: String? = null,
    val url: String
)
