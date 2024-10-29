package com.advait.org.assignment.presentation.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.advait.org.assignment.R
import com.advait.org.assignment.presentation.stateholders.ImageScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageContent(uiState: ImageScreenState) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Image Name")
                }
            )
        }
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .background(Color.Black)) {

            Row(modifier = Modifier
                .fillMaxSize()) {

                IconButton(onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(
                        painterResource(id = R.drawable.chevron_left), contentDescription = "previous image",
                        tint = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(
                        painterResource(id = R.drawable.chevron_right), contentDescription = "next image",
                        tint = Color.White)
                }

            }

            IconButton(onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.BottomCenter)) {
                Icon(imageVector = Icons.Default.Share,
                    contentDescription = "share", tint = Color.White)
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun Preview() {
    ImageContent(uiState = ImageScreenState())
}
