package com.advait.org.assignment.presentation.component

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.advait.org.assignment.data.cache.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException


@Composable
fun CachedImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderResId: Int? = null,
    errorResId: Int? = null,
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    DisposableEffect(imageUrl) {
        isVisible = true
        onDispose {
            isVisible = false
            downloadJob?.cancel()
        }
    }

    LaunchedEffect(imageUrl) {
        if (isVisible) {
            downloadJob = coroutineScope.launch {
                isLoading = true
                isError = false

                try {
                    val loadedBitmap = withContext(Dispatchers.IO) {
                        imageLoader.loadFullSizedImage(imageUrl, imageUrl + "_full")
                    }

                    if (loadedBitmap != null) {
                        bitmap = loadedBitmap
                        isError = false
                        isLoading = false
                    } else {
                        isError = true
                        isLoading = false
                    }
                } catch (e: CancellationException) {
                    bitmap = null
                    isError = true
                    isLoading = false
                } catch (e: Exception) {
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

    Box(modifier = modifier) {
        when {
            isLoading && placeholderResId != null -> {
                Image(
                    painter = painterResource(id = placeholderResId),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = contentScale
                )
            }
            isError && errorResId != null -> {
                Image(
                    painter = painterResource(id = errorResId),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = contentScale
                )
            }
            bitmap != null -> Image(
                painter = BitmapPainter(bitmap!!.asImageBitmap()),
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize(),
                contentScale = contentScale
            )
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            )
        }
    }
}