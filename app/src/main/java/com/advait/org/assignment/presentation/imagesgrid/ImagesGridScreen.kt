package com.advait.org.assignment.presentation.imagesgrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.advait.org.assignment.data.cache.Config
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.presentation.stateholders.MainViewModel
import com.advait.org.assignment.utils.Constants

@Composable
internal fun ImageGridScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMediaCoveragesData()
    }

    ImagesGridContent(
        modifier = modifier,
        uiState = uiState,
        onImageClick = { image ->
            viewModel.setSelectedArticle(image)
            navController.navigate(Constants.IMAGE_SCREEN_ROUTE)
        },
        onRetryClick = { viewModel.fetchMediaCoveragesData() }
    )
}