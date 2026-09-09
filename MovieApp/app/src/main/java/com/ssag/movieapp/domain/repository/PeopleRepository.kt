package com.ssag.movieapp.domain.repository

import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.data.model.PaginatedResponse

interface PeopleRepository {
    suspend fun getPeople(page: Int? = null): Result<PaginatedResponse<PersonDto>>
    suspend fun getPerson(id: Int): Result<PersonDto>
}
