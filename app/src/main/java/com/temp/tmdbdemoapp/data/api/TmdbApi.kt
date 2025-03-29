package com.temp.tmdbdemoapp.data.api

import com.temp.tmdbdemoapp.data.ConfigurationResponse
import com.temp.tmdbdemoapp.data.GenreResponse
import com.temp.tmdbdemoapp.data.MovieDetails
import com.temp.tmdbdemoapp.data.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    companion object {
        const val URL = "https://api.themoviedb.org/3/"
        const val FALLBACK_BASE_IMAGE_URL = "https://image.tmdb.org/t/p/"
    }

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "vote_average.desc"
    ): MovieResponse

    @GET("configuration")
    suspend fun getApiConfiguration(): ConfigurationResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): MovieDetails
}