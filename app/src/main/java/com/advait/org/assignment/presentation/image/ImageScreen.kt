package com.advait.org.assignment.presentation.image

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.advait.org.assignment.data.network.downloadFullSizeImage
import com.advait.org.assignment.presentation.stateholders.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


@Composable
internal fun ImageScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        downloadJob = coroutineScope.launch {
            try {
                bitmap = downloadFullSizeImage(imageUrl = uiState.selectedImageUrl)
                bitmap.let { btm ->
                    if(btm != null){
                        viewModel.setSelectedBitmap(btm)
                    }else{
                        Toast.makeText(context, "Could not be downloaded", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: CancellationException) {
                bitmap = null
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            downloadJob?.cancel()
            viewModel.clearSelectedBitmap()
        }
    }

    ImageContent(
        modifier = modifier,
        uiState = uiState,
        onForwardClick = {index ->
            downloadJob = coroutineScope.launch {
                try {
                    bitmap = downloadFullSizeImage(imageUrl = uiState.imageUrls[index].imageUrl)
                    bitmap.let { btm ->
                        viewModel.setSelectedBitmap(btm!!)
                    }
                } catch (e: CancellationException) {
                    bitmap = null
                }
            }
        }, onBackwardsClick = {index ->
            downloadJob = coroutineScope.launch {
                try {
                    bitmap = downloadFullSizeImage(imageUrl = uiState.imageUrls[index].imageUrl)
                    bitmap.let { btm ->
                        viewModel.setSelectedBitmap(btm!!)
                    }
                } catch (e: CancellationException) {
                    bitmap = null
                }
            }
        })
}