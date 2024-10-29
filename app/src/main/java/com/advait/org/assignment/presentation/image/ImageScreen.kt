package com.advait.org.assignment.presentation.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.advait.org.assignment.presentation.stateholders.MainViewModel


@Composable
internal fun ImageScreen(
    viewModel: MainViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    ImageContent(uiState)
}