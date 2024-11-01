package com.advait.org.assignment

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.advait.org.assignment.presentation.image.ImageScreen
import com.advait.org.assignment.presentation.imagesgrid.ImageGridScreen
import com.advait.org.assignment.presentation.stateholders.MainViewModel
import com.advait.org.assignment.utils.Constants
import com.advait.org.assignment.utils.Constants.APP_GRAPH
import kotlinx.coroutines.delay


@Composable
fun ImageGridApp(navController: NavHostController, modifier: Modifier) {

    SetupBackPressHandler()

    NavHost(navController = navController, startDestination = APP_GRAPH) {

       navigation(route = APP_GRAPH, startDestination = Constants.IMAGES_GRID_SCREEN_ROUTE) {

           composable(Constants.IMAGES_GRID_SCREEN_ROUTE) {

               val viewModel = it.sharedViewModel<MainViewModel>(navController = navController)

               ImageGridScreen(modifier = modifier, viewModel = viewModel, navController = navController)
           }

           composable(Constants.IMAGE_SCREEN_ROUTE) {

               val viewModel = it.sharedViewModel<MainViewModel>(navController = navController)

               ImageScreen(modifier = modifier.statusBarsPadding(), viewModel = viewModel, navController = navController)
           }

       }
   }

}

@Composable
inline fun < reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return  hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}


@Composable
private fun SetupBackPressHandler() {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    BackHandler {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context,
                context.getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(backPressedOnce) {
        delay(2000)
        backPressedOnce = false
    }
}
