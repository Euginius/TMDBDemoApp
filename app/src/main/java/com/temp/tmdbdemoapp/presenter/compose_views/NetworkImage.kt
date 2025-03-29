package com.temp.tmdbdemoapp.presenter.compose_views


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.size.Size
import com.temp.tmdbdemoapp.R

@Composable
fun NetworkImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .fallback(R.drawable.gallery)
            .error(R.drawable.gallery)
            .build()
    )

    val painterState by painter.state.collectAsState()

    // Adjust contentScale based on state
    val effectiveContentScale = when (painterState) {
        is AsyncImagePainter.State.Error,
        is AsyncImagePainter.State.Empty -> ContentScale.Fit // No cropping for error/fallback
        else -> contentScale // Use provided scale (Crop) for loading/success
    }

    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = effectiveContentScale,
            modifier = Modifier.matchParentSize()
        )

        // Show a Material Design loading indicator when the state is Loading
        if (painterState is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
