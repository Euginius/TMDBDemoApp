package com.temp.tmdbdemoapp.data.mock

import com.temp.tmdbdemoapp.data.Genre
import com.temp.tmdbdemoapp.data.Movie
import com.temp.tmdbdemoapp.data.MovieDetails
import com.temp.tmdbdemoapp.data.MoviesPage
import com.temp.tmdbdemoapp.data.TmdbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockTmdbRepository : TmdbRepository {
    override suspend fun getMovieGenres(): Flow<List<Genre>> = flow {
        // First, emit cached genres
        val sampleGenres = listOf(
            Genre(
                id = 1,
                name = "Action"
            ),
            Genre(
                id = 2,
                name = "Comedy"
            ),
            Genre(
                id = 3,
                name = "Drama"
            ),
            Genre(
                id = 4,
                name = "Horror"
            ),
            Genre(
                id = 5,
                name = "Romance"
            ),
            Genre(
                id = 6,
                name = "Science Fiction"
            ),
            Genre(
                id = 7,
                name = "Thriller"
            )
        )
        emit(sampleGenres)
    }

    override suspend fun getMoviesByGenre(genreId: Int, page: Int): Flow<MoviesPage> = flow {

        val sampleMovies = listOf(
            Movie(
                id = 1,
                title = "The Shawshank Redemption",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/8/81/ShawshankRedemptionMoviePoster.jpg",
                releaseDate = "1994-09-22"
            ),
            Movie(
                id = 2,
                title = "The Godfather",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/1/1c/Godfather_ver1.jpg",
                releaseDate = "1972-03-24"
            ),
            Movie(
                id = 3,
                title = "The Dark Knight",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/8/8a/Dark_Knight.jpg",
                releaseDate = "2008-07-18"
            ),
            Movie(
                id = 4,
                title = "Pulp Fiction",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/8/82/Pulp_Fiction_cover.jpg",
                releaseDate = "1994-10-14"
            ),
            Movie(
                id = 5,
                title = "Forrest Gump",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/6/67/Forrest_Gump_poster.jpg",
                releaseDate = "1994-07-06"
            ),
            Movie(
                id = 6,
                title = "Fight Club",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/f/fc/Fight_Club_poster.jpg",
                releaseDate = "1999-10-15"
            ),
            Movie(
                id = 7,
                title = "Inception",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/7/7f/Inception_ver3.jpg",
                releaseDate = "2010-07-16"
            ),
            Movie(
                id = 8,
                title = "The Matrix",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/c/c1/The_Matrix_Poster.jpg",
                releaseDate = "1999-03-31"
            ),
            Movie(
                id = 9,
                title = "Goodfellas",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/7/7b/Goodfellas.jpg",
                releaseDate = "1990-09-19"
            ),
            Movie(
                id = 10,
                title = "The Silence of the Lambs",
                posterPath = "https://upload.wikimedia.org/wikipedia/en/8/86/The_Silence_of_the_Lambs_poster.jpg",
                releaseDate = "1991-02-14"
            )
        )
        emit(MoviesPage(sampleMovies,1))

    }

    override suspend fun getImageBaseUrl(): Flow<String> = flow {
        emit("")
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails?   {
       return null
    }
}