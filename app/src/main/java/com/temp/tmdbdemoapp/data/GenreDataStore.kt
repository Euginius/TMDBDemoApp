package com.temp.tmdbdemoapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GenreDataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "genre_cache")

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val GENRES_KEY = stringPreferencesKey("cached_genres")
        private const val CACHE_EXPIRATION_HOURS = 24
    }

    suspend fun saveGenres(genres: List<Genre>) {
        val cachedGenresJson = json.encodeToString(
            CachedGenres(
                genres = genres,
                timestamp = System.currentTimeMillis()
            )
        )
        context.dataStore.edit { preferences ->
            preferences[GENRES_KEY] = cachedGenresJson
        }
    }

    fun getGenres(): Flow<List<Genre>> = context.dataStore.data.map { preferences ->
        val cachedGenresJson = preferences[GENRES_KEY]
        cachedGenresJson?.let {
            val cachedGenres = json.decodeFromString<CachedGenres>(it)

            // Check if cache is still valid (within 24 hours)
            if (System.currentTimeMillis() - cachedGenres.timestamp <= CACHE_EXPIRATION_HOURS * 60 * 60 * 1000) {
                cachedGenres.genres
            } else {
                emptyList()
            }
        } ?: emptyList()
    }

    // Wrapper class to store genres with timestamp
    @Serializable
    data class CachedGenres(
        val genres: List<Genre>,
        val timestamp: Long
    )
}