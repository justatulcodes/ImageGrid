package com.advait.org.assignment.presentation.image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advait.org.assignment.R
import com.advait.org.assignment.presentation.component.CachedImage
import com.advait.org.assignment.presentation.component.ConnectivityBottomBar
import com.advait.org.assignment.presentation.stateholders.ImageScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    uiState: ImageScreenState,
    onForwardClick: () -> Unit,
    onBackwardsClick: () -> Unit,
    onShareArticleClick: () -> Unit,
    onViewArticleClick: () -> Unit
) {
    val selectedArticle = uiState.articleUrls.getOrNull(uiState.selectedArticleIndex)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        sheetContent = {
            Column(
                modifier = Modifier.navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedArticle?.title ?: "Title Not Found",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Published On : ${selectedArticle?.publishedOn ?: "No date found"}",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Published By : ${selectedArticle?.publishedBy ?: "Not found"}",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(onClick = onShareArticleClick) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Share",
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share article",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Text(
                    text = selectedArticle?.description ?: "No description found",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = Color.Black
                )

                OutlinedButton(
                    onClick = onViewArticleClick,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Article",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painterResource(id = R.drawable.ic_open_link),
                            contentDescription = "open link",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        sheetSwipeEnabled = true,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 320.dp,
        sheetDragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp)) {
                HorizontalDivider(
                    thickness = 3.dp,
                    color = Color.LightGray,
                    modifier = Modifier.width(56.dp)
                )
            }
        },
        containerColor = Color.White,
        sheetContainerColor = Color.White,
        sheetShadowElevation = 12.dp,
        sheetTonalElevation = 12.dp
    ) {
        Column(modifier.padding(it)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                CachedImage(
                    imageUrl = uiState.articleUrls.getOrNull(uiState.selectedArticleIndex)
                        ?.imageUrl ?: "",
                    errorResId = R.drawable.img_loading_failure,
                    placeholderResId = R.drawable.img_loading,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = onBackwardsClick,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = IconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Icon(
                        painterResource(id = R.drawable.chevron_left),
                        contentDescription = "previous image",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onForwardClick,
                    colors = IconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.LightGray
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        painterResource(id = R.drawable.chevron_right),
                        contentDescription = "next image",
                        tint = Color.Black
                    )
                }
            }
            ConnectivityBottomBar(uiState.isInternetAvailable)
        }
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
