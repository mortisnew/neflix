package com.ssag.movieapp.data.api

import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.data.model.PaginatedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PeopleApi {
    @GET("people/people/")
    suspend fun getPeople(@Query("page") page: Int? = null): PaginatedResponse<PersonDto>

    @GET("people/people/{id}/")
    suspend fun getPerson(@Path("id") id: Int): PersonDto
}
