package com.advait.org.assignment.presentation.imagesgrid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.advait.org.assignment.R
import com.advait.org.assignment.domain.model.Article
import com.advait.org.assignment.presentation.component.ConnectivityBottomBar
import com.advait.org.assignment.presentation.component.GridImageItem
import com.advait.org.assignment.presentation.stateholders.ImageScreenState
import com.advait.org.assignment.ui.theme.ImageGridTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesGridContent(
    modifier: Modifier,
    uiState: ImageScreenState,
    onImageClick: (Article) -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Image Grid") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column {
            GridImageContainer(
                images = uiState.articleUrls,
                onImageClick = onImageClick,
                modifier = Modifier
                    .padding(paddingValues)
                    .weight(1f)
            )

            ConnectivityBottomBar(uiState.isInternetAvailable)
        }
    }

}

@Composable
fun GridImageContainer(
    images: List<Article>,
    onImageClick: (Article) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
    ) {
        items(images, key = { it.imageKey }) { article ->
            GridImageItem(
                imageUrl = article.imageUrl,
                imageKey = article.imageKey,
                onImageClick = { onImageClick(article) },
                placeholderResId = R.drawable.img_loading,
                errorResId = R.drawable.img_loading_failure
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {

    ImageGridTheme {
        ImagesGridContent(
            uiState = ImageScreenState(),
            onImageClick = {},
            modifier = Modifier,
        )
    }

}