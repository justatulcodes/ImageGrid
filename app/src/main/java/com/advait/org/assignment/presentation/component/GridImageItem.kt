package com.advait.org.assignment.presentation.component

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advait.org.assignment.data.cache.ImageLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Composable function to display a grid image item with optional placeholders and error images
 * and built in caching of image in Memory and Disk Cache. It downloads the image in smaller
 * size to be displayed in a scroll view
 *
 * @param imageUrl The URL of the image to be displayed.
 * @param imageKey A unique key associated with the image. Used as cached image key.
 * @param modifier Modifier for styling and layout.
 * @param onImageClick Callback to be invoked when the image is clicked.
 * @param placeholderResId Resource ID of the placeholder image to be displayed while loading.
 * @param errorResId Resource ID of the error image to be displayed in case of loading failure.
 *
 */
@Composable
fun GridImageItem(
    imageUrl: String,
    imageKey: String,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit = {},
    placeholderResId: Int? = null,
    errorResId: Int? = null
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(imageUrl) {
        isVisible = true
        onDispose {
            isVisible = false
            downloadJob?.cancel()
        }
    }

    LaunchedEffect(imageUrl, isVisible) {
        isLoading = true
        isError = false

        if (isVisible) {
            downloadJob = coroutineScope.launch {
                try {

                    ensureActive()

                    val result = imageLoader.loadImage(
                        imageUrl = imageUrl,
                        imageKey = imageKey
                    )

                    when (result) {
                        is ImageLoader.ImageLoadResult.Success -> {
                            bitmap = result.bitmap
                            isError = false
                            isLoading = false
                        }
                        is ImageLoader.ImageLoadResult.Error -> {
                            bitmap = null
                            isError = true
                            isLoading = false
                        }
                    }
                } catch (e: CancellationException) {
                    bitmap = null
                    isError = true
                    isLoading = false
                }
            }
        } else {
            downloadJob = null
            bitmap = null
            isLoading = false
            isError = false
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            bitmap != null -> Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onImageClick),
                contentScale = ContentScale.Crop,
            )
            isLoading && placeholderResId != null -> Image(
                painter = painterResource(id = placeholderResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            isError && errorResId != null -> Image(
                painter = painterResource(id = errorResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            )
        }
    }
}