
package com.ssag.movieapp.di

import com.ssag.movieapp.data.api.*
import com.ssag.movieapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("AuthClient")
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        jwtInterceptor: JwtInterceptor,
        jwtAuthenticator: JwtAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(jwtInterceptor)
            .authenticator(jwtAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("AuthClient") okHttpClient: OkHttpClient,
        json: Json
    ): AuthApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(contentType)
            )
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(contentType)
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideHomeApi(
        retrofit: Retrofit
    ): HomeApi = retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideContentApi(
        retrofit: Retrofit
    ): ContentApi = retrofit.create(ContentApi::class.java)

    @Provides
    @Singleton
    fun provideCatalogApi(
        retrofit: Retrofit
    ): CatalogApi = retrofit.create(CatalogApi::class.java)

    @Provides
    @Singleton
    fun provideSpecialApi(
        retrofit: Retrofit
    ): SpecialApi = retrofit.create(SpecialApi::class.java)

    @Provides
    @Singleton
    fun providePeopleApi(
        retrofit: Retrofit
    ): PeopleApi = retrofit.create(PeopleApi::class.java)
}

