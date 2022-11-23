package com.iskorsukov.aniwatcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.iskorsukov.aniwatcher.service.NotificationService
import com.iskorsukov.aniwatcher.ui.Screen
import com.iskorsukov.aniwatcher.ui.airing.AiringScreen
import com.iskorsukov.aniwatcher.ui.airing.AiringViewModel
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorSurfaceContent
import com.iskorsukov.aniwatcher.ui.base.topbar.SearchField
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import com.iskorsukov.aniwatcher.ui.following.FollowingScreen
import com.iskorsukov.aniwatcher.ui.following.FollowingViewModel
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaScreen
import com.iskorsukov.aniwatcher.ui.media.MediaViewModel
import com.iskorsukov.aniwatcher.ui.notification.NotificationActivity
import com.iskorsukov.aniwatcher.ui.settings.SettingsCompatActivity
import com.iskorsukov.aniwatcher.ui.sorting.SelectSortingOptionDialog
import com.iskorsukov.aniwatcher.ui.theme.*
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

            var shouldShowSortingOptionsDialog by remember {
                mutableStateOf(false)
            }
            var shouldShowErrorDialog by remember(uiState.errorItem) {
                mutableStateOf(uiState.errorItem != null)
            }

            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        TopBar(
                            navController = navController,
                            onSelectSortingOptionClicked = { shouldShowSortingOptionsDialog = true },
                            onSettingsClicked = { startSettingsActivity() },
                            onNotificationsClicked = { startNotificationsActivity() }
                        )
                    },
                    bottomBar = { BottomNavigationBar(navController = navController) },
                    scaffoldState = scaffoldState,
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        if (shouldShowSortingOptionsDialog) {
                            SelectSortingOptionDialog(
                                onSortingOptionSelected = mainActivityViewModel::onSortingOptionSelected,
                                onDismissRequest = { shouldShowSortingOptionsDialog = false },
                                selectedOption = uiState.sortingOption
                            )
                        }

                        NavHost(
                            navController = navController,
                            startDestination = "airing"
                        ) {
                            composable("media") {
                                MediaScreen(
                                    mainActivityViewModel,
                                    mediaViewModel,
                                    timeInMinutesFlow
                                ) { startDetailsActivity(it.id) }
                            }
                            composable("airing") {
                                AiringScreen(
                                    mainActivityViewModel,
                                    airingViewModel,
                                    timeInMinutesFlow
                                ) { startDetailsActivity(it.id) }
                            }
                            composable("following") {
                                FollowingScreen(
                                    mainActivityViewModel,
                                    followingViewModel,
                                    timeInMinutesFlow
                                ) { startDetailsActivity(it.id) }
                            }
                        }

                        AnimatedVisibility(
                            visible = shouldShowErrorDialog && uiState.errorItem != null,
                            enter = slideInVertically(initialOffsetY = { it * 2 }),
                            exit = slideOutVertically(targetOffsetY = { it * 2 }),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(8.dp)
                        ) {
                            val errorItem = uiState.errorItem!!
                            ErrorSurfaceContent(
                                errorItem = errorItem,
                                onActionClicked = {
                                    when (errorItem.action) {
                                        ErrorItem.Action.REFRESH -> mainActivityViewModel.loadAiringData()
                                        ErrorItem.Action.DISMISS -> {}
                                    }
                                },
                                onDismissRequest = { shouldShowErrorDialog = false }
                            )
                        }
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

    private fun startSettingsActivity() {
        startActivity(
            Intent(this, SettingsCompatActivity::class.java)
        )
    }

    private fun startNotificationsActivity() {
        startActivity(
            Intent(this, NotificationActivity::class.java)
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
    BottomNavigation(backgroundColor = LocalColors.current.primary) {
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
                label = {
                    Text(
                        text = stringResource(screen.labelStringId)
                    )
                },
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
                },
                selectedContentColor = LocalColors.current.secondary,
                unselectedContentColor = LocalColors.current.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun TopBar(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    navController: NavHostController,
    onSelectSortingOptionClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onNotificationsClicked: () -> Unit
) {
    val uiState by mainActivityViewModel.uiState
        .collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val screen = Screen.ofRoute(currentDestination?.route.orEmpty())

    TopAppBar(backgroundColor = LocalColors.current.primary) {
        if (screen?.hasSearchBar == true) {
            AnimatedVisibility(
                visible = uiState.searchFieldOpen,
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                SearchField(
                    searchText = uiState.searchText,
                    onSearchTextChanged = mainActivityViewModel::onSearchTextInput,
                    onSearchCancelled = {
                        mainActivityViewModel.onSearchFieldOpenChange(false)
                    },
                    focusRequester = focusRequester
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }

            if (!uiState.searchFieldOpen) {
                IconButton(
                    onClick = {
                        mainActivityViewModel.onSearchFieldOpenChange(true)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = LocalColors.current.onPrimary
                    )
                }
            }
        }
        if (screen?.hasSortingOptions == true) {
            IconButton(onClick = onSelectSortingOptionClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_sort_24),
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
            }
        }
        Spacer(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        IconButton(onClick = onNotificationsClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
        IconButton(onClick = onSettingsClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
    }
}


