package pl.karolpietrow.klikacz.ui.start

import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.AuthState
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.R

@Composable
fun LoginScreen(navController: NavController, clickViewModel: ClickViewModel, authViewModel: AuthViewModel,) {
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val scrollState = rememberScrollState()
    val scale = remember { Animatable(0f) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showContent by remember { mutableStateOf(false) }
    var forgotPasswordAlert by remember { mutableStateOf(false) }

    val credentialManager = remember { CredentialManager.create(context) }

    if (forgotPasswordAlert) {
        var forgotEmail by remember {mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { forgotPasswordAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.resetPassword(context, forgotEmail)
                        forgotPasswordAlert = false
                    },
                ) {
                    Text(stringResource(id = R.string.send_email))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        forgotPasswordAlert = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.password_reset_dialog)) },
            text = {
                Column{
                    Text(stringResource(id = R.string.password_reset_description))
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = forgotEmail,
                        onValueChange = { newText ->
                            forgotEmail = newText.replace(" ", "")
                        },
                        label = { Text(stringResource(id = R.string.email)) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                }
            }
        )
    }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("load_screen") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.GoogleRegisterRequired -> navController.navigate("register_google")
            else -> {
                authViewModel.deleteUserDataLocal()
                clickViewModel.deletePointDataLocal()
                showContent = true
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1000,
                        delayMillis = 500,
                        easing = {
                            OvershootInterpolator(1f).getInterpolation(it)
                        }
                    )
                )
            }
        }
    }

    Scaffold { innerPadding ->
        if (!showContent || authState.value == AuthState.Loading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
            }
        }
        if (showContent && authState.value != AuthState.Loading) {
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
                        .padding(10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .scale(scale.value),
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "App logo",
                )
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Klikacz",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = stringResource(id = R.string.login_greeting),
                    fontSize = 17.sp,
                    textAlign = TextAlign.Justify
                )
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
                    ),
                )
                val message = stringResource(id = R.string.login_enter_email_password)
                Button(
                    modifier = Modifier.padding(10.dp),
                    enabled = authState.value != AuthState.Loading,
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            authViewModel.login(context, email, password)
                        }
                    },
                ) {
                    Text(stringResource(id = R.string.login))
                }
                Text("LUB")
                Image(
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp)
                        .padding(10.dp)
                        .clickable(onClick = {
                            authViewModel.signInWithGoogle(credentialManager, context)
                        }),
                    painter = painterResource(R.drawable.continue_with_google_en),
                    contentDescription = "Continue with Google Button"
                )
                TextButton(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        forgotPasswordAlert = true
                    },
                ) {
                    Text(stringResource(id = R.string.forgot_password))
                }
                TextButton(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        navController.navigate("register")
                    },
                ) {
                    Text(stringResource(id = R.string.register_request_button))
                }
            }
        }
    }
}