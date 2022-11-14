package com.iskorsukov.aniwatcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iskorsukov.aniwatcher.service.NotificationService
import com.iskorsukov.aniwatcher.ui.Screen
import com.iskorsukov.aniwatcher.ui.airing.AiringScreen
import com.iskorsukov.aniwatcher.ui.airing.AiringViewModel
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import com.iskorsukov.aniwatcher.ui.following.FollowingScreen
import com.iskorsukov.aniwatcher.ui.following.FollowingViewModel
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaScreen
import com.iskorsukov.aniwatcher.ui.media.MediaViewModel
import com.iskorsukov.aniwatcher.ui.media.SearchField
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLifecycleComposeApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val airingViewModel: AiringViewModel by viewModels()
    private val followingViewModel: FollowingViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()

    private val timeInMinutesFlow = flow {
        while (true) {
            val timeInMillis = System.currentTimeMillis()
            emit(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }.distinctUntilChanged()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel.loadAiringData()
        startNotificationService()
        setContent {
            val navController = rememberNavController()

            val scaffoldState = rememberScaffoldState()
            val uiState by mainActivityViewModel.uiState.collectAsStateWithLifecycle()

            if (uiState.errorItem != null) {
                LaunchedEffect(scaffoldState.snackbarHostState) {
                    val snackbarData = scaffoldState.snackbarHostState.showSnackbar(
                        message = getString(uiState.errorItem!!.labelResId),
                        actionLabel = getString(R.string.try_again),
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarData == SnackbarResult.ActionPerformed) {
                        mainActivityViewModel.loadAiringData()
                    }
                }
            }

            Scaffold(
                topBar = { TopBar(navController = navController) },
                bottomBar = { BottomNavigationBar(navController = navController) },
                scaffoldState = scaffoldState
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "airing",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("media") {
                        MediaScreen(
                            mainActivityViewModel,
                            mediaViewModel,
                            timeInMinutesFlow,
                            this@MainActivity::startDetailsActivity
                        )
                    }
                    composable("airing") {
                        AiringScreen(
                            mainActivityViewModel,
                            airingViewModel,
                            timeInMinutesFlow,
                            this@MainActivity::startDetailsActivity
                        )
                    }
                    composable("following") {
                        FollowingScreen(
                            mainActivityViewModel,
                            followingViewModel,
                            timeInMinutesFlow,
                            this@MainActivity::startDetailsActivity
                        )
                    }
                }
            }
        }
    }

    private fun startNotificationService() {
        startService(Intent(this, NotificationService::class.java))
    }

    private fun startDetailsActivity(mediaItemId: Int) {
        startActivity(
            Intent(this, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.MEDIA_ITEM_ID_EXTRA, mediaItemId)
            }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.MediaScreen,
        Screen.AiringScreen,
        Screen.FollowingScreen
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconDrawableId),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(screen.labelStringId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun TopBar(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    navController: NavHostController
) {
    val searchFieldVisibleState = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val screen = Screen.ofRoute(currentDestination?.route.orEmpty())

    if (screen?.hasTopBar == true) {
        TopAppBar {
            if (searchFieldVisibleState.value) {
                AnimatedVisibility(
                    visible = searchFieldVisibleState.value,
                    enter = expandHorizontally(expandFrom = Alignment.Start),
                    exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
                ) {
                    SearchField(
                        onSearchTextChanged = mainActivityViewModel::onSearchTextInput,
                        searchFieldVisibleState = searchFieldVisibleState,
                        focusRequester = focusRequester
                    )
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            } else {
                IconButton(onClick = { searchFieldVisibleState.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}


