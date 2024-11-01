package com.advait.org.assignment.presentation.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advait.org.assignment.R
import com.advait.org.assignment.domain.model.Article
import com.advait.org.assignment.presentation.component.CachedImage
import com.advait.org.assignment.presentation.component.ConnectivityBottomBar
import com.advait.org.assignment.presentation.stateholders.ImageScreenState

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    uiState: ImageScreenState,
    onForwardClick: () -> Unit,
    onBackwardsClick: () -> Unit,
    onShareArticleClick : () -> Unit,
    onViewArticleClick : () -> Unit
) {

    val selectedArticle = uiState.articleUrls.getOrNull(uiState.selectedArticleIndex)

    Column(modifier) {
        Box(modifier = Modifier
            .weight(1f)
            .background(Color.Black))
        {

            Column(Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier.weight(4f),
                    contentAlignment = Alignment.Center,
                ) {
                    CachedImage(
                        imageUrl = uiState.articleUrls.getOrNull(uiState.selectedArticleIndex)?.imageUrl ?: "",
                        errorResId = R.drawable.img_loading_failure,
                        placeholderResId = R.drawable.img_loading,
                        modifier = Modifier.fillMaxSize())
                }

                Column(modifier = Modifier
                    .weight(2f)
                    .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = selectedArticle?.title ?: "Title Not Found", color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 16.dp))

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        Column {

                            Text(text = "Published On : ${selectedArticle?.publishedOn?:"No date found"}", color = Color.White,
                                fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Published By : ${selectedArticle?.publishedBy?:"Not found"}", color = Color.White,
                                fontWeight = FontWeight.Medium, fontSize = 12.sp)

                        }

                        OutlinedButton(onClick = onShareArticleClick, ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Share", color = Color.White,
                                    fontWeight = FontWeight.Medium, fontSize = 12.sp)

                                Spacer(modifier = Modifier.width(4.dp))

                                Icon(imageVector = Icons.Default.Share,
                                    contentDescription = "Share article",
                                    tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                    }

                    Text(text = selectedArticle?.description?:"No description found",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = Color.White)

                    OutlinedButton(onClick = onViewArticleClick, modifier = Modifier.padding(vertical = 8.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "View Article", color = Color.White,
                                fontWeight = FontWeight.Medium, fontSize = 12.sp)

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(painterResource(id = R.drawable.ic_open_link),
                                contentDescription = "open link",
                                tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }



            }


            Row(modifier = Modifier
                .fillMaxSize()) {

                IconButton(onClick = onBackwardsClick,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = IconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Icon(
                        painterResource(id = R.drawable.chevron_left), contentDescription = "previous image",
                        tint = Color.Black)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onForwardClick,
                    colors = IconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.LightGray
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(
                        painterResource(id = R.drawable.chevron_right), contentDescription = "next image",
                        tint = Color.Black)
                }

            }

        }

        ConnectivityBottomBar(uiState.isInternetAvailable)

    }


}

@Preview(showBackground = true)
@Composable
fun Preview() {
    ImageContent(
        uiState = ImageScreenState(),
        onForwardClick = { },
        onBackwardsClick = { },
        onShareArticleClick = {},
        onViewArticleClick = {})
}
