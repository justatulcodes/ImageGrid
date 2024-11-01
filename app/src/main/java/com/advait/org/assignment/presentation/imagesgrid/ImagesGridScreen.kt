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
    val context = LocalContext.current
    val diskLruCache = remember {
        DiskLruCache(context, Config.diskCacheSize)
    }

    LaunchedEffect(Unit) {
        viewModel.setupDiskCache(diskLruCache)
        viewModel.fetchMediaCoveragesData()
    }

    ImagesGridContent(
        modifier = modifier,
        uiState = uiState,
        memoryCache = uiState.memoryCache,
        diskCache = uiState.diskCache,
        onImageClick = { image ->
            viewModel.setSelectedImageUrl(image)
            navController.navigate(Constants.IMAGE_SCREEN_ROUTE)
        }
    )
}