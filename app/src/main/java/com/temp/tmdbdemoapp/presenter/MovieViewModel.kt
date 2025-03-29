package com.temp.tmdbdemoapp.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temp.tmdbdemoapp.data.Genre
import com.temp.tmdbdemoapp.data.Movie
import com.temp.tmdbdemoapp.data.MovieDetails
import com.temp.tmdbdemoapp.data.TmdbRepository
import com.temp.tmdbdemoapp.data.api.TmdbApi.Companion.FALLBACK_BASE_IMAGE_URL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TmdbRepository) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _currentGenre = MutableStateFlow<Genre?>(null)
    val currentGenre: StateFlow<Genre?> = _currentGenre.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _imageBaseUrl = MutableStateFlow(FALLBACK_BASE_IMAGE_URL)
    val imageBaseUrl: StateFlow<String> = _imageBaseUrl.asStateFlow()

    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails: StateFlow<MovieDetails?> = _movieDetails.asStateFlow()

    private var currentPageInView: Int = 1

    init {
        fetchConfig()
        fetchGenres()
    }

    private fun fetchConfig() {
        viewModelScope.launch {
            try {
                repository.getImageBaseUrl().collect { url ->
                    _imageBaseUrl.value = url
                }
            } catch (e: Exception) { }
        }
    }


    private fun fetchGenres() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getMovieGenres().collect { fetchedGenres ->
                    _genres.value = fetchedGenres
                    // Automatically select the first genre if no genre is selected
                    if (_currentGenre.value == null && fetchedGenres.isNotEmpty()) {
                        selectGenre(fetchedGenres.first())
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectGenre(genre: Genre) {
        viewModelScope.launch {
            _currentGenre.value = genre
            _isLoading.value = true
            try {
                repository.getMoviesByGenre(genre.id).collect { fetchedMovies ->
                    currentPageInView = fetchedMovies.pageNum
                    _movies.value = fetchedMovies.movies
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun loadMovieDetails(movieId: Int) : MovieDetails?{
       return repository.getMovieDetails(movieId)
    }

    fun loadMoreMovies() {
        viewModelScope.launch {
            currentGenre.value?.let {
                try {
                    val nextPage = currentPageInView + 1
                    repository.getMoviesByGenre(it.id, nextPage).collect { fetchedMovies ->
                        _movies.value = _movies.value.plus(fetchedMovies.movies).distinctBy { movie-> movie.id }
                        currentPageInView = fetchedMovies.pageNum
                    }
                } catch (e: Exception) {}
            }
        }
    }
}