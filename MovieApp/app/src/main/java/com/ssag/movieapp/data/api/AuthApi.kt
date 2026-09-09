package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.model.TokenRefreshRequestDto
import com.ssag.movieapp.data.model.TokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/token/")
    suspend fun login(@Body body: Map<String, String>): TokenResponseDto

    @POST("accounts/register/")
    suspend fun register(@Body body: Map<String, String>): Map<String, String>

    @POST("api/token/refresh/")
    fun refresh(@Body body: TokenRefreshRequestDto): Call<TokenResponseDto>
}
