package com.temp.tmdbdemoapp.presenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.temp.tmdbdemoapp.BuildConfig
import com.temp.tmdbdemoapp.data.Genre
import com.temp.tmdbdemoapp.data.GenreDataStore
import com.temp.tmdbdemoapp.data.Movie
import com.temp.tmdbdemoapp.data.TmdbRepositoryImpl
import com.temp.tmdbdemoapp.data.api.TmdbApiManagerImpl
import com.temp.tmdbdemoapp.data.mock.MockTmdbRepository
import com.temp.tmdbdemoapp.presenter.compose_views.MovieDetailsScreen
import com.temp.tmdbdemoapp.presenter.compose_views.NetworkImage
import com.temp.tmdbdemoapp.ui.theme.TMDBDemoAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tmdbRepository = TmdbRepositoryImpl(
            TmdbApiManagerImpl(this, BuildConfig.TMDB_API_KEY), GenreDataStore(this)
        )

        val factory = GenericViewModelFactory { MainViewModel(tmdbRepository) }
        setContent {
             TMDBDemoAppTheme {
                 MovieApp(viewModel(factory = factory))
               // MovieGridScreen(viewModel(factory = factory))
            }
        }
    }
}

@Composable
fun MovieApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "movieGrid") {
        composable("movieGrid") {
            MovieGridScreen(viewModel,navController)
        }
        composable("movieDetails/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            movieId?.let { MovieDetailsScreen(viewModel,navController,it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieGridScreen(viewModel: MainViewModel,navController: NavHostController) {
    val genres by viewModel.genres.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val currentGenre by viewModel.currentGenre.collectAsState()
    val imageBaseUrl by viewModel.imageBaseUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TMDB Explorer",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

        ) {
            GenreTabs(
                genres = genres,
                currentGenre = currentGenre,
                onGenreSelected = { genre ->
                    viewModel.selectGenre(genre)
                }
            )

            // Movies Grid
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                MoviesGrid(movies,imageBaseUrl,
                    loadMore =  {
                    viewModel.loadMoreMovies()
                }, onClick =  { id->
                    navController.navigate("movieDetails/${id}")
                })  
            }
        }
    }
}

@Composable
fun GenreTabs(
    genres: List<Genre>,
    currentGenre: Genre?,
    onGenreSelected: (Genre) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            items(genres.size) { position ->
                GenreTabItem(
                    genre = genres[position],
                    isSelected = genres[position] == currentGenre,
                    onClick = {
                        if (genres[position] != currentGenre) {
                            onGenreSelected(genres[position])
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GenreTabItem(
    genre: Genre,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = genre.name,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MoviesGrid(movies: List<Movie>, baseUrl: String,loadMore: () -> Unit, onClick: (id: Int) -> Unit) {
    val gridState = rememberLazyGridState()
    var isLoading by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies.size) { index ->
            MovieGridItem(
                movie = movies[index],
                baseUrl,
                onClick = onClick
            )
        }
    }

    LaunchedEffect(movies) {
        isLoading = false
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = layoutInfo.totalItemsCount

                if (!isLoading && lastVisibleItem >= totalItemsCount - 5) {
                    isLoading = true
                    loadMore()
                    delay(1000)
                    isLoading = false
                }
            }
    }
}


@Composable
fun MovieGridItem(
    movie: Movie,
    imageBaseUrl: String,
    onClick: (id: Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clickable {
                    onClick(movie.id)
                },
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                NetworkImage("${imageBaseUrl}${movie.posterPath}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentDescription = movie.title
                )
            }
        }

        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            text = "${movie.title} (${movie.releaseDate.take(4)})",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            minLines = 2,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val tmdbRepository = MockTmdbRepository()
    val factory = GenericViewModelFactory { MainViewModel(tmdbRepository) }

    TMDBDemoAppTheme {
        MovieApp(viewModel(factory = factory))
    }
}


class GenericViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(creator().javaClass)) {
            return creator() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}