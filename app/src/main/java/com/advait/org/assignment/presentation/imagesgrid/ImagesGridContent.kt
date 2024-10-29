package com.advait.org.assignment.presentation.imagesgrid

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.advait.org.assignment.data.network.downloadBitmap
import com.advait.org.assignment.presentation.stateholders.ImageScreenState
import com.advait.org.assignment.ui.theme.ImageGridTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesGridContent(
    uiState: ImageScreenState,
    onImageClick: (String) -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Image Grid")
                }
            )
        }
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(1.dp)
        ) {

            items(uiState.imageUrls, key = { imageUrl -> imageUrl }) { imageUrl ->
                ImageItem(imageUrl = imageUrl, onImageClick = { onImageClick(imageUrl) })
            }

        }
    }

}

@Composable
fun ImageItem(imageUrl: String, onImageClick: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        coroutineScope.launch(Dispatchers.IO) {
            bitmap = downloadBitmap(imageUrl)
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
            onImageClick = {},
            uiState = ImageScreenState(
                imageUrls = emptyList()
            )
        )
    }

}