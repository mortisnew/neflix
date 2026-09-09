package com.ssag.movieapp.domain.repository

import com.ssag.movieapp.data.model.TokenResponseDto

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<TokenResponseDto>
    suspend fun register(username: String, email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
