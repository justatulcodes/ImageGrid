package com.advait.org.assignment.presentation.imagesgrid

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.data.network.downloadBitmap
import com.advait.org.assignment.domain.Image
import com.advait.org.assignment.domain.LruImageCache
import com.advait.org.assignment.presentation.component.BnConnectivityStatusBar
import com.advait.org.assignment.presentation.stateholders.ImageScreenState
import com.advait.org.assignment.ui.theme.ImageGridTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesGridContent(
    modifier: Modifier,
    uiState: ImageScreenState,
    onImageClick: (Image) -> Unit,
    memoryCache: LruImageCache?,
    diskCache: DiskLruCache?
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Image Grid")
                }
            )
        },
        modifier = modifier
    ) {

        Column {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(it)
                    .weight(1f)
                    .padding(1.dp)
            ) {

                items(uiState.imageUrls, key = { imageUrl -> imageUrl.imageKey }) { image ->
                    ImageItem(image = image, onImageClick = { onImageClick(image) },
                        memoryCache = memoryCache, diskCache = diskCache)
                }

            }

            BnConnectivityStatusBar(uiState.isInternetAvailable)
        }


    }

}

@Composable
fun ImageItem(
    image : Image,
    onImageClick: () -> Unit,
    memoryCache: LruImageCache?,
    diskCache: DiskLruCache?
) {

    val coroutineScope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isVisible by remember { mutableStateOf(false) }

    var downloadJob by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(Unit) {
        isVisible = true
        onDispose {
            isVisible = false
            downloadJob?.cancel()
            Log.d("JOB", "Cancelling job with Id : ${downloadJob.toString()}")
        }
    }

    LaunchedEffect(image, isVisible) {
        // Only start download if the item is visible
        if (isVisible) {
            // Store the job so we can cancel it if needed
            downloadJob = coroutineScope.launch {
                Log.d("JOB", "Running job with Id : ${downloadJob.toString()}")
                try {
                    bitmap = downloadBitmap(image = image,
                        memoryCache = memoryCache, diskCache = diskCache)
                } catch (e: CancellationException) {
                    // Handle cancellation gracefully
                    bitmap = null
                }
            }

        }else{
            downloadJob = null
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp),
        contentAlignment = Alignment.Center,
    ) {
        bitmap?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick() },
                contentScale = ContentScale.Crop,
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {

    ImageGridTheme {
        ImagesGridContent(
            uiState = ImageScreenState(
                imageUrls = emptyList()
            ),
            onImageClick = {},
            modifier = Modifier,
            memoryCache = null,
            diskCache = null
        )
    }

}