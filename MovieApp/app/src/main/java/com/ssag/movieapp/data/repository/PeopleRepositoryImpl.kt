package com.ssag.movieapp.data.repository

import com.ssag.movieapp.data.api.PeopleApi
import com.ssag.movieapp.data.model.PaginatedResponse
import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.domain.repository.PeopleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeopleRepositoryImpl @Inject constructor(
    private val peopleApi: PeopleApi
) : PeopleRepository {
    override suspend fun getPeople(page: Int?): Result<PaginatedResponse<PersonDto>> = try {
        Result.success(peopleApi.getPeople(page))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPerson(id: Int): Result<PersonDto> = try {
        Result.success(peopleApi.getPerson(id))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
