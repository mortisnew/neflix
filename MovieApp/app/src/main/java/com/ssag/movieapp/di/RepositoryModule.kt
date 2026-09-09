package com.ssag.movieapp.di

import com.ssag.movieapp.data.repository.*
import com.ssag.movieapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(
        catalogRepositoryImpl: CatalogRepositoryImpl
    ): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindContentRepository(
        contentRepositoryImpl: ContentRepositoryImpl
    ): ContentRepository

    @Binds
    @Singleton
    abstract fun bindSpecialRepository(
        specialRepositoryImpl: SpecialRepositoryImpl
    ): SpecialRepository

    @Binds
    @Singleton
    abstract fun bindPeopleRepository(
        peopleRepositoryImpl: PeopleRepositoryImpl
    ): PeopleRepository
}
