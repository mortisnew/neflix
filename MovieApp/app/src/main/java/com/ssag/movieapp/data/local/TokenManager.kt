package com.ssag.movieapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssag.movieapp.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey(Constants.ACCESS_TOKEN_KEY)
        private val REFRESH_TOKEN = stringPreferencesKey(Constants.REFRESH_TOKEN_KEY)
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = access
            preferences[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    // Blocking methods for OkHttp Authenticator
    fun getAccessTokenBlocking(): String? = runBlocking { accessToken.first() }

    fun getRefreshTokenBlocking(): String? = runBlocking { refreshToken.first() }

    fun saveTokensBlocking(access: String, refresh: String) = runBlocking {
        saveTokens(access, refresh)
    }

    fun clearTokensBlocking() = runBlocking {
        clearTokens()
    }
}