package com.temp.tmdbdemoapp.data

import com.temp.tmdbdemoapp.data.api.TmdbApi.Companion.FALLBACK_BASE_IMAGE_URL
import com.temp.tmdbdemoapp.data.api.TmdbApiManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface TmdbRepository {
    suspend fun getMovieGenres(): Flow<List<Genre>>
    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): Flow<MoviesPage>
    suspend fun getImageBaseUrl(): Flow<String>
    suspend fun getMovieDetails(movieId: Int): MovieDetails?
}

class TmdbRepositoryImpl (
    private val apiService: TmdbApiManager,
    private val genreDataStore: GenreDataStore
): TmdbRepository {
    override suspend fun getMovieGenres(): Flow<List<Genre>> = flow {
        // First, emit cached genres
        val cachedGenres = genreDataStore.getGenres().first()
        if (cachedGenres.isNotEmpty()) {
            emit(cachedGenres)
        }
        try {
            // Fetch fresh genres from API
            val remoteGenres = apiService.getMovieGenres()
            // Cache the new genres
            genreDataStore.saveGenres(remoteGenres)
            // Emit the fresh genres
            emit(remoteGenres)
        } catch (e: Exception) {
            // If API call fails, emit cached genres
            emit(cachedGenres)
        }
    }

    override suspend fun getMoviesByGenre(genreId: Int, page: Int): Flow<MoviesPage> = flow {
        try {
            val movies = apiService.getMoviesByGenre(genreId,page)
            emit(movies)
        } catch (e: Exception) {
            emit(MoviesPage())
        }
    }

    override suspend fun getImageBaseUrl(): Flow<String> = flow {
        try {
            val url = apiService.getImageBaseUrl()
            emit(url)
        } catch (e: Exception) {
            emit(FALLBACK_BASE_IMAGE_URL)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails? {
        return try {
            apiService.getMovieDetails(movieId)
        } catch (e: Exception) {
            null
        }
    }
}