package pl.karolpietrow.klikacz.ui.start

import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.AuthState
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.R
import androidx.core.net.toUri

@Composable
fun GoogleRegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val authState = authViewModel.authState.observeAsState()

    var username by remember { mutableStateOf("") }

    var available by remember { mutableStateOf("") }
    var usernameOk by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate("register")
            else -> Unit
        }
    }


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(innerPadding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App logo",
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Dokończ rejestrację",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = stringResource(id = R.string.register_description),
                fontSize = 17.sp,
                textAlign = TextAlign.Justify
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Uzupełnij dane, aby zarejestrować się z Google!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            val message1 = stringResource(id = R.string.username_available)
            val message2 = stringResource(id = R.string.username_unavailable)
            OutlinedTextField(
                modifier = Modifier.padding(5.dp),
                value = username,
                onValueChange = { newText ->
                    if (newText.length <= 15 && newText.matches(Regex("^[a-zA-Z0-9]*$"))) {
                        username = newText
                        authViewModel.isUsernameAvailable(username) { isAvailable ->
                            if (isAvailable) {
                                if (username.isNotEmpty()) {
                                    usernameOk = true
                                    available = "✅ $message1"
                                } else {
                                    usernameOk = false
                                    available = ""
                                }
                            } else if (username.isNotEmpty()) {
                                usernameOk = false
                                available = "❌ $message2"
                            }
                        }
                    }
                },
                label = { Text(stringResource(id = R.string.username)) },
                maxLines = 1,
            )
            Text(available)
            Row(
                modifier = Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                val url = "https://www.karolpietrow.pl/klikacz/tos"
                                val customTabsIntent = CustomTabsIntent.Builder()
                                    .setShowTitle(true)
                                    .setInstantAppsEnabled(true)
                                    .build()
                                customTabsIntent.launchUrl(context, url.toUri())
                            }
                        ),
                    text = stringResource(id = R.string.register_tos),
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 15.sp,
                )
            }
            val enterAllFieldsMessage = stringResource(id = R.string.register_enter_all_fields)
            val acceptTOSRequiredMessage = stringResource(id = R.string.register_tos_request)
            Button(
                modifier = Modifier.padding(10.dp),
                enabled = usernameOk && (authState.value != AuthState.Loading),
                onClick = {
                    if (username.isNotEmpty()) {
                        if (checked) {
                            authViewModel.registerWithGoogle(context, username)
                        } else {
                            Toast.makeText(context, acceptTOSRequiredMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, enterAllFieldsMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(stringResource(id = R.string.register))
            }
            if (authState.value == AuthState.Loading) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "${stringResource(id = R.string.loading)}...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                LinearProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            TextButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    navController.navigate("login") {
                        popUpTo("register_google") { inclusive = true }
                    }
                },
            ) {
                Text("Anuluj rejestrację")
            }
        }
    }
}