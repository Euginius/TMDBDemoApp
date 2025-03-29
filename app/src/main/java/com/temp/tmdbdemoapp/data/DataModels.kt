package com.temp.tmdbdemoapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GenreResponse(
    @SerialName("genres")
    val genres: List<Genre>
)

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Serializable
data class MovieResponse(
    @SerialName("results")
    val results: List<Movie>,
    @SerialName("page")
    val page: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class Movie(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String
)


@Serializable
data class MoviesPage(var movies: List<Movie> = arrayListOf(), var pageNum: Int = 1)

@Serializable
data class ConfigurationResponse(
    @SerialName("images")
    val images: ImageConfiguration
)

@Serializable
data class ImageConfiguration(
    @SerialName("base_url")
    val baseUrl: String,
    @SerialName("poster_sizes")
    val posterSizes: List<String>
)


@Serializable
data class MovieDetails(
    @SerialName("adult")
    val adult: Boolean,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("belongs_to_collection")
    val belongsToCollection: Collection? = null,

    @SerialName("budget")
    val budget: Long,

    @SerialName("genres")
    val genres: List<Genre>,

    @SerialName("homepage")
    val homepage: String? = null,

    @SerialName("id")
    val id: Int,

    @SerialName("imdb_id")
    val imdbId: String? = null,

    @SerialName("origin_country")
    val originCountry: List<String>,

    @SerialName("original_language")
    val originalLanguage: String,

    @SerialName("original_title")
    val originalTitle: String,

    @SerialName("overview")
    val overview: String? = null,

    @SerialName("popularity")
    val popularity: Float,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany>,

    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry>,

    @SerialName("release_date")
    val releaseDate: String,

    @SerialName("revenue")
    val revenue: Long,

    @SerialName("runtime")
    val runtime: Int? = null,

    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>,

    @SerialName("status")
    val status: String,

    @SerialName("tagline")
    val tagline: String? = null,

    @SerialName("title")
    val title: String,

    @SerialName("video")
    val video: Boolean,

    @SerialName("vote_average")
    val voteAverage: Float,

    @SerialName("vote_count")
    val voteCount: Int
)

// Nested data classes for complex fields
@Serializable
data class Collection(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null
)

@Serializable
data class ProductionCompany(
    @SerialName("id")
    val id: Int,

    @SerialName("logo_path")
    val logoPath: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("origin_country")
    val originCountry: String
)

@Serializable
data class ProductionCountry(
    @SerialName("name")
    val name: String
)

@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val englishName: String,

    @SerialName("name")
    val name: String
)