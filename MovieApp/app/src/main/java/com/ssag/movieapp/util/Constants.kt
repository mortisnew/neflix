package com.ssag.movieapp.util

object Constants {
    // Android Emulator host address for localhost
    const val BASE_URL = "http://192.168.1.35:8000/"
    
    // For real devices, replace with your local IP or production domain
    // const val BASE_URL = "http://192.168.1.xxx:8000/"

    const val DATASTORE_NAME = "movie_app_prefs"
    const val ACCESS_TOKEN_KEY = "access_token"
    const val REFRESH_TOKEN_KEY = "refresh_token"
}
