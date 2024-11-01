package com.advait.org.assignment.presentation.image

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.advait.org.assignment.presentation.stateholders.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
internal fun ImageScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedBitmap()
        }
    }

    ImageContent(
        modifier = modifier,
        uiState = uiState,
        onForwardClick = {
            val index = uiState.selectedArticleIndex
            val increment = if(index == uiState.articleUrls.size - 1) 0 else index + 1
            Log.d("ImageScreen", "onForwardClick: $increment")
            viewModel.clearSelectedBitmap()
            viewModel.setSelectedArticleIndex(increment)
        },
        onBackwardsClick = {
            val index = uiState.selectedArticleIndex
            val decrement = if(index == 0) uiState.articleUrls.size - 1 else index - 1
            viewModel.setSelectedArticleIndex(decrement)
            Log.d("ImageScreen", "onBackwardsClick: $decrement")
            viewModel.clearSelectedBitmap()
        },
        onShareArticleClick = {
            val article = uiState.articleUrls.getOrNull(uiState.selectedArticleIndex) ?: return@ImageContent
            val title = article.title
            val articleUrl = article.articleUrl

            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, title)
                            putExtra(Intent.EXTRA_TEXT, """
                        $title
                        
                        Read more at: $articleUrl
                    """.trimIndent())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to share article", Toast.LENGTH_SHORT).show()

                    }
                }
            }

        },
        onViewArticleClick = {
            val article = uiState.articleUrls.getOrNull(uiState.selectedArticleIndex)!!
            article.articleUrl.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        })
}