package com.temp.tmdbdemoapp.data.api

import android.content.Context
import com.temp.tmdbdemoapp.data.ConfigurationResponse
import com.temp.tmdbdemoapp.data.Genre
import com.temp.tmdbdemoapp.data.MovieDetails
import com.temp.tmdbdemoapp.data.MoviesPage
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import kotlin.math.abs


interface TmdbApiManager {
     suspend fun getMovieGenres(): List<Genre>
     suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): MoviesPage
     suspend fun getMovieDetails(movieId: Int): MovieDetails
     suspend fun getImageBaseUrl(): String
}

class TmdbApiManagerImpl(context: Context,apiKey: String) :TmdbApiManager {

   private val retrofit =  Retrofit.Builder()
    .baseUrl(TmdbApi.URL)
    .client(provideOkHttpClient(apiKey,context))
    .addConverterFactory(provideJson().asConverterFactory("application/json".toMediaType()))
    .build()

    private val tmdbApi: TmdbApi by lazy {
        retrofit.create(TmdbApi::class.java)
    }

    override suspend fun getMovieGenres(): List<Genre> {
        return tmdbApi.getMovieGenres().genres
    }

    override suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviesPage {
        return MoviesPage(tmdbApi.getMoviesByGenre(genreId,page).results,page)
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return tmdbApi.getMovieDetails(movieId)
    }

    override suspend fun getImageBaseUrl(): String {
       val config =  tmdbApi.getApiConfiguration()
       return buildPosterUrl(config,500) ?:""
    }

    private fun buildPosterUrl(config: ConfigurationResponse, posterPreferdSize: Int): String? {
        val baseUrl = config.images.baseUrl.replace("http:","https:")
        val posterSizes = config.images.posterSizes

        if (posterSizes.isEmpty()) return null

        val closestSize = posterSizes.minByOrNull { size ->
            size.removePrefix("w").toIntOrNull()?.let { abs(it - posterPreferdSize) } ?: Int.MAX_VALUE
        } ?: return "woriginal"
        return "$baseUrl$closestSize"
    }

    private fun provideOkHttpClient(apiKey: String, context: Context): OkHttpClient {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(cacheDir, cacheSize.toLong())

        return OkHttpClient.Builder()
            .cache(cache) // Add cache to OkHttpClient
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            // Add API key to every request (replace with your TMDb API key)
            val urlWithApiKey = originalRequest.url.newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build()
            requestBuilder.url(urlWithApiKey)

            // Handle caching behavior
            val networkAvailable = isNetworkAvailable(context) // Implement this based on your app
            if (!networkAvailable) {
                // Force cache use when offline
                requestBuilder.header("Cache-Control", "public, only-if-cached, max-stale=2419200") // 28 days
            }

            chain.proceed(requestBuilder.build())
        }.build()
    }

    private fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Utility to check network availability (implement this)
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}