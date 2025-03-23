package pl.karolpietrow.klikacz.ui.start

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pl.karolpietrow.klikacz.AuthState
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import pl.karolpietrow.klikacz.R

@Composable
fun LoadScreen(
    navController: NavHostController,
    clickViewModel: ClickViewModel,
    authViewModel: AuthViewModel,
    personalisationViewModel: PersonalisationViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val username = authViewModel.username.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var loadStatus by remember { mutableIntStateOf(0) } // 0 - Loading, 1 - Success, -1 - Fail


    LaunchedEffect(authState.value) {
        authViewModel.checkAuthStatus()
        when(authState.value) {
            is AuthState.Authenticated -> {
                coroutineScope.launch {
                    val cloudSuccess = clickViewModel.getPointDataCloud()
                    if (cloudSuccess) {
                        Log.d("KLIKACZAPP", "CLOUD-SUCCESS-1")
                        val cloudSuccess2 = clickViewModel.getUpgradesCloud()
                        if (cloudSuccess2) {
                            Log.d("KLIKACZAPP", "CLOUD-SUCCESS-2")
                            val cloudSuccess3 = authViewModel.getUserDataCloud()
                            if (cloudSuccess3) {
                                Log.d("KLIKACZAPP", "CLOUD-SUCCESS-3")
                                loadStatus = 1
                            } else {
                                Log.d("KLIKACZAPP", "CLOUD-FAIL-3")
                                loadStatus = -1
                            }
                        } else {
                            Log.d("KLIKACZAPP", "CLOUD-FAIL-2")
                            loadStatus = -1
                        }
                    } else {
                        Log.d("KLIKACZAPP", "CLOUD-FAIL-1")
                        loadStatus = -1
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                authViewModel.deleteUserDataLocal()
                clickViewModel.deletePointDataLocal()
                personalisationViewModel.deleteDataLocal()
                navController.navigate("login") {
                    popUpTo("load_screen") { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(loadStatus) {
        if (loadStatus == 1) {
            navController.navigate("home") {
                popUpTo("load_screen") { inclusive = true }
            }
        }
    }

    Scaffold { innerPadding ->
        when (loadStatus) {
            0 -> { // Loading
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "App logo",
                    )
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "${stringResource(id = R.string.loading)}...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
//            LinearProgressIndicator()
//            CircularProgressIndicator()
                    LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    if (username.value != "N/A") {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "Ładowanie trwa za długo?",
                            fontSize = 17.sp,
//                    fontWeight = FontWeight.Bold,
                        )
                        Button(
                            onClick = {
                                clickViewModel.getUpgradesLocal()
                            }
                        ) {
                            Text("Kontynuuj w trybie offline")
                        }
                    }
                }
            }

            1 -> { // Success

            }

            -1 -> { // Fail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "App logo",
                    )
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Błąd ładowania",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            loadStatus = 0
                            coroutineScope.launch {
                                val cloudSuccess = clickViewModel.getPointDataCloud()
                                if (cloudSuccess) {
                                    Log.d("KLIKACZAPP", "CLOUD-SUCCESS-1")
                                    val cloudSuccess2 = clickViewModel.getUpgradesCloud()
                                    if (cloudSuccess2) {
                                        Log.d("KLIKACZAPP", "CLOUD-SUCCESS-2")
                                        loadStatus = 1
                                    } else {
                                        Log.d("KLIKACZAPP", "CLOUD-FAIL-2")
                                        loadStatus = -1
                                    }
                                } else {
                                    Log.d("KLIKACZAPP", "CLOUD-FAIL-1")
                                    loadStatus = -1
                                }
                            }
                        }
                    ) {
                        Text("Spróbuj ponownie")
                    }
                    if (username.value != "N/A") {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    clickViewModel.getUpgradesLocal()
                                    navController.navigate("home") {
                                        popUpTo("load_screen") { inclusive = true }
                                    }
                                }
                            }
                        ) {
                            Text("Kontynuuj w trybie offline")
                        }
                    }
                }
            }
        }
    }

}