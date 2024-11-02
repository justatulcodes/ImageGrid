package com.advait.org.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.advait.org.assignment.ui.theme.ImageGridTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ImageGridTheme {

                val navController = rememberNavController()
                val globalModifier = Modifier.navigationBarsPadding().statusBarsPadding()
                ImageGridApp(navController = navController, modifier = globalModifier)

            }
        }
    }
}
