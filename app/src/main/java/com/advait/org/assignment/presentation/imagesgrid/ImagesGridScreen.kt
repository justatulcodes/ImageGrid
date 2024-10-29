package com.advait.org.assignment.presentation.imagesgrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.advait.org.assignment.ImageScreenRoute
import com.advait.org.assignment.presentation.stateholders.MainViewModel

@Composable
internal fun ImageGridScreen(
    viewModel: MainViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    viewModel.fetchMediaCoveragesData()

    ImagesGridContent(
        uiState = uiState,
        onImageClick = {
            navController.navigate(ImageScreenRoute)
        }
    )
}