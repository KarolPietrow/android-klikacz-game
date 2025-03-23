package pl.karolpietrow.klikacz.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.ImageViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import pl.karolpietrow.klikacz.ui.start.*
import pl.karolpietrow.klikacz.ui.game.*

@Composable
fun Navigation(context: Context, openScreen: String? = null) {
    val clickViewModel: ClickViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val personalisationViewModel: PersonalisationViewModel = viewModel()
    val imageViewModel: ImageViewModel = viewModel()
    val navController = rememberNavController()

    val isLoggedIn = Firebase.auth.currentUser != null
    val firstPage = if (isLoggedIn) "load_screen" else "login"

    NavHost(
        navController = navController,
        startDestination = firstPage,
        builder = {
            composable("load_screen") {
                LoadScreen(navController, clickViewModel, authViewModel, personalisationViewModel)
            }
            composable("login") {
                LoginScreen(navController, clickViewModel, authViewModel)
            }
            composable("register") {
                RegisterScreen(navController, authViewModel)
            }
            composable("register_google") {
                GoogleRegisterScreen(navController, authViewModel)
            }
            composable("home") {
                GameScreen(context, navController, clickViewModel, authViewModel, personalisationViewModel, imageViewModel, openScreen)
            }
            composable("settings") {
                Settings(context, navController, clickViewModel, authViewModel, personalisationViewModel, imageViewModel)
            }
            composable("notification") {
                NotificationRequest(navController, clickViewModel) // dodac ciemny
            }
            composable("personalisation") {
                Personalisation(navController, clickViewModel, personalisationViewModel)
            }
            composable("wheel") {
                FortuneWheel(context, navController, clickViewModel)
            }
            composable("tutorial") {
                Tutorial(navController, clickViewModel)
            }
        }
    )
}