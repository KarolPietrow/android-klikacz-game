package pl.karolpietrow.klikacz.ui.game

import android.content.Context
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.karolpietrow.klikacz.AuthState
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.ImageViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import pl.karolpietrow.klikacz.R
import pl.karolpietrow.klikacz.ui.BannerAd
import pl.karolpietrow.klikacz.ui.checkNotificationPermission

@Composable
fun GameScreen(
    context: Context,
    navController: NavController,
    clickViewModel: ClickViewModel,
    authViewModel: AuthViewModel,
    personalisationViewModel: PersonalisationViewModel,
    imageViewModel: ImageViewModel,
    openScreen: String? = null
) {
    val localContext = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val autoFrequency = clickViewModel.autoFrequency.collectAsState()
    val notificationPermissionStatus by remember { mutableStateOf(checkNotificationPermission(localContext)) }
    val isPurchaseAvailable = clickViewModel.isPurchaseAvailable.collectAsState()
    val tutorialComplete = clickViewModel.tutorialComplete.collectAsState()
    val buttonText = personalisationViewModel.buttonText.collectAsState()
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "clicker"

    LaunchedEffect(openScreen) {
        if (openScreen == "profile") {
            bottomNavController.navigate("profile") {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> {
                Log.d("KLIKACZAPP", "UNAUTHENTICATED")
                authViewModel.deleteUserDataLocal()
                clickViewModel.deletePointDataLocal()
                personalisationViewModel.deleteDataLocal()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    authViewModel.checkAuthStatus()
                    if (!tutorialComplete.value) {
                        navController.navigate("tutorial") {
                            popUpTo("home") { inclusive = true }
                        }
                    } else if (clickViewModel.notificationsEnabled.value && !notificationPermissionStatus) {
                        navController.navigate("notification") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    authViewModel.checkAuthStatus()
                    coroutineScope.launch {
                        while (true) {
                            delay(autoFrequency.value)
                            if (buttonText.value != "KLIK--") {
                                clickViewModel.autoIncrement()
                            }
                        }
                    }
                    coroutineScope.launch {
                        while (true) {
                            delay(120000)
                            clickViewModel.savePointDataLocal()
                            clickViewModel.savePointDataCloud()
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    clickViewModel.savePointDataLocal()
                    clickViewModel.savePointDataCloud()
//                    Toast.makeText(context, "ON_PAUSE", Toast.LENGTH_SHORT).show()
                    coroutineScope.coroutineContext.cancelChildren()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    label = { Text("Klikacz")},
                    selected = currentRoute == "clicker",
                    onClick = {
                        if (currentRoute != "clicker") {
                            bottomNavController.navigate("clicker") {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(
                        painter = painterResource(id = R.drawable.click_icon),
                        contentDescription = "Game view"
                    )}
                )
                NavigationBarItem(
                    label = { Text(stringResource(id = R.string.shop))},
                    selected = currentRoute == "shop",
                    onClick = {
                        if (currentRoute != "shop") {
                            bottomNavController.navigate("shop") {
//                            popUpTo(bottomNavController.graph.startDestinationId) {
//                                saveState = true
//                            }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        BadgedBox(
                            modifier = Modifier.size(35.dp),
                            badge = {
                                if (isPurchaseAvailable.value) {
                                    Badge(modifier = Modifier.size(10.dp))
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.store_bottom_icon),
                                contentDescription = "Shop view"
                            )
                        }
                    },
                )
                NavigationBarItem(
                    label = { Text(stringResource(id = R.string.ranking))},
                    selected = currentRoute == "ranking",
                    onClick = {
                        if (currentRoute != "ranking") {
                            bottomNavController.navigate("ranking") {
//                            popUpTo(bottomNavController.graph.startDestinationId) {
//                                saveState = true
//                            }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(
                        painter = painterResource(id = R.drawable.leaderboard_icon),
                        contentDescription = "Ranking view"
                    )}
                )
                NavigationBarItem(
                    label = { Text(stringResource(id = R.string.profile))},
                    selected = currentRoute == "profile",
                    onClick = {
                        if (currentRoute != "profile") {
                            bottomNavController.navigate("profile") {
//                            popUpTo(bottomNavController.graph.startDestinationId) {
//                                saveState = true
//                            }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(
                        painter = painterResource(id = R.drawable.default_profile_icon),
                        contentDescription = "Profile view"
                    )}
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            val modifier  = Modifier.padding(bottom = 60.dp)
            NavHost(navController = bottomNavController, startDestination = "clicker") {
                animatedComposable("clicker", bottomNavController  ) { Clicker(context, modifier, clickViewModel, personalisationViewModel, navController) }
                animatedComposable("shop", bottomNavController) { Shop(modifier, clickViewModel) }
                animatedComposable("ranking", bottomNavController) { Ranking(context, modifier, clickViewModel, authViewModel) }
                animatedComposable("profile", bottomNavController) { Profile(modifier, clickViewModel, navController, authViewModel, personalisationViewModel, imageViewModel) }
            }
            BannerAd(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )
        }
    }
}

fun NavGraphBuilder.animatedComposable(
    route: String,
    bottomNavController: NavController,
    content: @Composable () -> Unit
) {
    val screenOrder = listOf("clicker", "shop", "ranking", "profile")
    composable(
        route = route,
        enterTransition = {
            val fromIndex = screenOrder.indexOf(bottomNavController.previousBackStackEntry?.destination?.route)
            val toIndex = screenOrder.indexOf(route)
//            Log.d("KLIKACZAPP", "ENTER - From: $fromIndex, To: $toIndex")

            if (fromIndex < toIndex) {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            } else {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            }
        },
        exitTransition = {
            val fromIndex = screenOrder.indexOf(route)
            val toIndex = screenOrder.indexOf(bottomNavController.currentBackStackEntry?.destination?.route)

//            Log.d("KLIKACZAPP", "EXIT - From: $fromIndex, To: $toIndex")

            if (fromIndex < toIndex) {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            } else {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        },
        popEnterTransition = {
            val fromIndex = screenOrder.indexOf(bottomNavController.previousBackStackEntry?.destination?.route)
            val toIndex = screenOrder.indexOf(route)

            if (fromIndex < toIndex) {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            } else {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            }
        },
        popExitTransition = {
            val fromIndex = screenOrder.indexOf(route)
            val toIndex = screenOrder.indexOf(bottomNavController.currentBackStackEntry?.destination?.route)

            if (fromIndex < toIndex) {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            } else {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            }
        }
    ) {
        content()
    }
}