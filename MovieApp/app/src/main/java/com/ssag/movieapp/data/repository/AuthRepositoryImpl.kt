package com.ssag.movieapp.data.repository

import com.ssag.movieapp.data.api.AuthApi
import com.ssag.movieapp.data.local.TokenManager
import com.ssag.movieapp.data.model.TokenResponseDto
import com.ssag.movieapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<TokenResponseDto> {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val response = authApi.login(body)
            tokenManager.saveTokens(response.access, response.refresh)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            val body = mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "password2" to password
            )
            authApi.register(body)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        return !tokenManager.accessToken.first().isNullOrEmpty()
    }
}
