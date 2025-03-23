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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val authState = authViewModel.authState.observeAsState()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    var goBackAlert by remember { mutableStateOf(false) }
    var available by remember { mutableStateOf("") }
    var usernameOk by remember {mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
            else -> Unit
        }
    }

    if (goBackAlert) {
        AlertDialog(
            onDismissRequest = { goBackAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate("login")
                        goBackAlert = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        goBackAlert = false
                    }
                ) {
                    Text(stringResource(id = R.string.no_stay))
                }
            },
            title = { Text(stringResource(id = R.string.cancel_register)) },
            text = { Text(stringResource(id = R.string.cancel_register_description)) }
        )
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
                text = stringResource(id = R.string.register),
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
                text = stringResource(id = R.string.register_description2),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                modifier = Modifier.padding(5.dp),
                value = name,
                onValueChange = { newText ->
                    name = newText.replace("\n", "")
                },
                label = { Text(stringResource(id = R.string.name)) },
                maxLines = 1,
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
            OutlinedTextField(
                modifier = Modifier.padding(10.dp),
                value = email,
                onValueChange = { newText ->
                    email = newText.replace(" ", "")
                },
                label = { Text(stringResource(id = R.string.email)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )
            OutlinedTextField(
                modifier = Modifier.padding(5.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password)) },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                )
            )
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
                    if (!(name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty())) {
                        if (checked) {
                            authViewModel.register(context, name, username, email, password)
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
                    if (name.isEmpty() && username.isEmpty() && email.isEmpty() && password.isEmpty()) {
                        navController.navigate("login")
                    } else {
                        goBackAlert = true
                    }
                },
            ) {
                Text("Cofnij do logowania")
            }
        }
    }
}