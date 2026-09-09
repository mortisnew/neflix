package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.local.TokenManager
import com.ssag.movieapp.data.model.TokenRefreshRequestDto
import com.ssag.movieapp.util.Constants
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class JwtAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApi> // Use Provider to avoid circular dependency
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // We only retry once for a 401
        if (response.countRetries() >= 1) {
            return null
        }

        synchronized(lock) {
            val currentToken = tokenManager.getAccessTokenBlocking()
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // If the token in tokenManager is already different from the one that failed,
            // it means another thread has already refreshed it.
            if (currentToken != failedToken && !currentToken.isNullOrEmpty()) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Otherwise, we need to refresh
            val refreshToken = tokenManager.getRefreshTokenBlocking()
            if (refreshToken.isNullOrEmpty()) {
                return null
            }

            return try {
                val refreshResponse = authApiProvider.get()
                    .refresh(TokenRefreshRequestDto(refreshToken))
                    .execute()

                if (refreshResponse.isSuccessful) {
                    val newTokens = refreshResponse.body()
                    if (newTokens != null) {
                        tokenManager.saveTokensBlocking(newTokens.access, newTokens.refresh)
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.access}")
                            .build()
                    } else {
                        null
                    }
                } else {
                    // Refresh failed, probably refresh token expired
                    tokenManager.clearTokensBlocking()
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun Response.countRetries(): Int {
        var result = 0
        var prev = priorResponse
        while (prev != null) {
            result++
            prev = prev.priorResponse
        }
        return result
    }
}
