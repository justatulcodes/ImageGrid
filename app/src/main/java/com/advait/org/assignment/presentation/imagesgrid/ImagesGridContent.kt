package com.advait.org.assignment.presentation.imagesgrid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advait.org.assignment.R
import com.advait.org.assignment.domain.model.Article
import com.advait.org.assignment.presentation.component.ConnectivityBottomBar
import com.advait.org.assignment.presentation.component.GridImageItem
import com.advait.org.assignment.presentation.stateholders.ImageScreenErrors
import com.advait.org.assignment.presentation.stateholders.ImageScreenState
import com.advait.org.assignment.ui.theme.ImageGridTheme

@Composable
fun ImagesGridContent(
    modifier: Modifier,
    uiState: ImageScreenState,
    onImageClick: (Article) -> Unit,
    onRetryClick: () -> Unit,
) {

    Scaffold(
        topBar = {
            ImageGridTopBar(modifier)
        },
        modifier = modifier,
        containerColor = Color.White
    ) { paddingValues ->
        Column {

            LoadingIndicator(uiState.isLoading)

            if(uiState.errors != null) {

                when(uiState.errors) {
                    is ImageScreenErrors.GenericError -> {
                        Error(uiState.errors.message, R.drawable.ic_not_found, onRetryClick)
                    }
                    is ImageScreenErrors.NetworkFailure -> {
                        Error(uiState.errors.message, R.drawable.ic_no_internet, onRetryClick)
                    }
                }

            }else{
                GridImageContainer(
                    images = uiState.articleUrls,
                    onImageClick = onImageClick,
                    modifier = Modifier
                        .padding(paddingValues)
                        .weight(1f)
                )
            }

            ConnectivityBottomBar(uiState.isInternetAvailable)
        }
    }

}

@Composable
private fun Error(message: String, drawableInt : Int, onRetryClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = drawableInt),
                contentDescription = "not found icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
private fun LoadingIndicator(loading: Boolean) {

    if(loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Loading...")
            }
        }
    }
}

@Composable
private fun ImageGridTopBar(modifier: Modifier) {
    Row(
        modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_grid),
            tint = Color.Black, modifier = Modifier.size(20.dp),
            contentDescription = "grid_icon"
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(text = "Image Grid", color = Color.Black, fontSize = 20.sp)
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
            uiState = ImageScreenState(
                isLoading = false,
                errors = ImageScreenErrors.NetworkFailure(message = "Internet Failure")
            ),
            onImageClick = {},
            modifier = Modifier,
            onRetryClick = {}
        )
    }

}