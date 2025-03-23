package pl.karolpietrow.klikacz.ui.game

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import pl.karolpietrow.klikacz.R
import pl.karolpietrow.klikacz.ui.checkNotificationPermission
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import pl.karolpietrow.klikacz.ImageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    context: Context,
    navController: NavController,
    clickViewModel: ClickViewModel,
    authViewModel: AuthViewModel,
    personalisationViewModel: PersonalisationViewModel,
    imageViewModel: ImageViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val localContext = LocalContext.current
    val scrollState = rememberScrollState()
    val notificationPermissionStatus by remember { mutableStateOf(checkNotificationPermission(localContext)) }
    val notificationsEnabled = clickViewModel.notificationsEnabled.collectAsState()
    val uriHandler = LocalUriHandler.current

    var changeEmailAlert by remember { mutableStateOf(false) }
    var changePasswordAlert by remember { mutableStateOf(false) }
    var deleteDataAlert by remember { mutableStateOf(false) }
    var deleteDataAlert2 by remember { mutableStateOf(false) }
    var deleteAccountAlert by remember { mutableStateOf(false) }
    var deleteAccountAlert2 by remember { mutableStateOf(false) }

    val themeMode = clickViewModel.themeMode.collectAsState().value
    var isExpanded by remember { mutableStateOf(false) }
    val themeOptions = listOf("Automatycznie", "Jasny", "Ciemny")

    if (changeEmailAlert) {
        var email by remember {mutableStateOf("") }
        var password by remember {mutableStateOf("") }
        var newEmail by remember {mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { changeEmailAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.updateEmail(localContext, email, password, newEmail)
                        changeEmailAlert = false
                    },
                ) {
                    Text(stringResource(id = R.string.settings_change_email))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        changeEmailAlert = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.settings_change_email)) },
            text = {
                Column{
                    Text(stringResource(id = R.string.settings_confirm_identity))
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = email,
                        onValueChange = { newText ->
                            email = newText.replace(" ", "")
                        },
                        label = { Text("Aktualny adres e-mail") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(id = R.string.password)) },
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                        ),
                    )
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = newEmail,
                        onValueChange = { newText ->
                            newEmail = newText.replace(" ", "")
                        },
                        label = { Text("Nowy adres e-mail") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                    Text("Zostanie wysłana wiadomość weryfikacyjna na nowy adres e-mail. Kliknij w link we wiadomości, a następnie zaloguj się ponownie, aby dokończyć procedurę zmiany adresu e-mail.")
                }
            }
        )
    }

    if (changePasswordAlert) {
        var email by remember {mutableStateOf("") }
        var password by remember {mutableStateOf("") }
        var newPassword by remember {mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { changePasswordAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.updatePassword(localContext, email, password, newPassword)
                        changePasswordAlert = false
                    },
                ) {
                    Text(stringResource(id = R.string.settings_change_password))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        changePasswordAlert = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.settings_change_password)) },
            text = {
                Column{
                    Text(stringResource(id = R.string.settings_confirm_identity))
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = email,
                        onValueChange = { newText ->
                            email = newText.replace(" ", "")
                        },
                        label = { Text(stringResource(id = R.string.email)) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Aktualne hasło") },
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                        ),
                    )
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nowe hasło") },
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                        ),
                    )
//                    Text("Zostanie wysłana wiadomość weryfikacyjna na nowy adres e-mail. Kliknij w link we wiadomości, a następnie zaloguj się ponownie, aby dokończyć procedurę zmiany adresu e-mail.")
                }
            }
        )
    }

    if (deleteDataAlert) {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = "Warning icon", modifier = Modifier.size(50.dp), tint = Color.Red) },
            onDismissRequest = { deleteDataAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        deleteDataAlert = false
                        deleteDataAlert2 = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(stringResource(id = R.string.settings_yes_delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deleteDataAlert = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel).uppercase())
                }
            },
            title = { Text("${stringResource(id = R.string.are_you_sure)} 😭") },
            text = { Text("Wybrałeś/aś opcję \"Wyzeruj postęp\". Twój cały postęp w grze, w tym wynik kliknięć, mnożniki, zakupione ulepszenia i zdobyte osiągnięcia, zostanie utracony. TEJ AKCJI NIE MOŻNA COFNĄĆ! Czy chcesz kontynuować?")}
        )
    }

    if (deleteDataAlert2) {
        var email by remember {mutableStateOf("") }
        var password by remember {mutableStateOf("") }
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = "Warning icon", modifier = Modifier.size(50.dp), tint = Color.Red) },
            onDismissRequest = { deleteDataAlert2 = false},
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.verifyCredential(email, password) { success ->
                            if (success) {
                                val notificationManager = ContextCompat.getSystemService(
                                    context,
                                    NotificationManager::class.java
                                ) as NotificationManager
                                notificationManager.cancelAll()

                                coroutineScope.launch {
                                    clickViewModel.deletePointDataLocal()
                                    clickViewModel.savePointDataCloud()
                                    personalisationViewModel.deleteDataLocal()
                                    imageViewModel.deleteImageFromCloud()
                                }

                                deleteDataAlert2 = false
                                Toast.makeText(localContext, "Postęp gry został wyzerowany.", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            } else {
                                Toast.makeText(localContext, "Nieprawidłowy email lub hasło.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Tak, USUŃ")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deleteDataAlert2 = false
                    }
                ) {
                    Text("ANULUJ")
                }
            },
            title = { Text("Aby kontynuować operację usunięcia, potwierdź swoją tożsamość.", textAlign = TextAlign.Center) },
            text = {
                Column{
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = email,
                        onValueChange = { newText ->
                            email = newText.replace(" ", "")
                        },
                        label = { Text("Aktualny adres e-mail") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Hasło") },
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                        ),
                    )
                }
            }
        )
    }

    if (deleteAccountAlert) {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = "Warning icon", modifier = Modifier.size(50.dp), tint = Color.Red) },
            onDismissRequest = { deleteAccountAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        deleteAccountAlert = false
                        deleteAccountAlert2 = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Tak, USUŃ")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deleteAccountAlert = false
                    }
                ) {
                    Text("ANULUJ")
                }
            },
            title = { Text("Na pewno? 😭") },
            text = { Text("Wybrałeś/aś opcję \"Usuń konto\". Twój cały postęp w grze, w tym wynik kliknięć, mnożniki, zakupione ulepszenia i zdobyte osiągnięcia, oraz nazwa konta i inne dane profilowe, zostaną usunięte. TEJ AKCJI NIE MOŻNA COFNĄĆ!\n" +
                    "Sprawdź Politykę prywatności aplikacji Klikacz w celu uzyskania szczegółów nt. usuwania danych użytkowników z serwerów aplikacji Klikacz.\n" +
                    "Czy chcesz kontynuować?")}
        )
    }

    if (deleteAccountAlert2) {
        var email by remember {mutableStateOf("") }
        var password by remember {mutableStateOf("") }
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = "Warning icon", modifier = Modifier.size(50.dp), tint = Color.Red) },
            onDismissRequest = { deleteAccountAlert2 = false},
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.verifyCredential(email, password) { success ->
                            if (success) {
                                clickViewModel.deletePointDataLocal()
                                personalisationViewModel.deleteDataLocal()
                                deleteAccountAlert = false
                                authViewModel.deleteAccount { deleteSuccess ->
                                    if (deleteSuccess) {
                                        Toast.makeText(
                                            localContext,
                                            "Konto zostało usunięte.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("home") {
                                            popUpTo("settings") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(
                                            localContext,
                                            "Wystąpił błąd podczas usuwania.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(localContext, "Nieprawidłowy email lub hasło.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Tak, USUŃ")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deleteAccountAlert2 = false
                    }
                ) {
                    Text("ANULUJ")
                }
            },
            title = { Text("Aby kontynuować operację usunięcia, potwierdź swoją tożsamość.", textAlign = TextAlign.Center) },
            text = {
                Column{
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = email,
                        onValueChange = { newText ->
                            email = newText.replace(" ", "")
                        },
                        label = { Text("Aktualny adres e-mail") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Hasło") },
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                        ),
                    )
                }
            }
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        navController.popBackStack(route = "home", inclusive = false)
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Arrow")
                }
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings icon",
                        Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(
                        text = stringResource(id = R.string.settings),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.settings_enable_notifications),
                    fontSize = 20.sp
                )
                Switch(
                    checked = notificationsEnabled.value,
                    onCheckedChange = {
                        if (notificationsEnabled.value) {
                            clickViewModel.disableNotifications()
                        } else {
                            clickViewModel.enableNotifications()
                            if (!notificationPermissionStatus) {
                                navController.navigate("notification") {
                                    popUpTo("settings") { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        label = { Text("Tryb ciemny") },
                        value = themeOptions[themeMode],
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false}
                    ) {
                        themeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    val mode = when (option) {
                                        "Automatycznie" -> 0
                                        "Jasny" -> 1
                                        "Ciemny" -> 2
                                        else -> 0
                                    }
                                    clickViewModel.setThemeMode(mode)
                                    isExpanded = false
                                }
                            )
                        }
                    }

                }
            }
            Button(
                modifier = Modifier
                    .height(75.dp)
                    .width(250.dp)
                    .padding(10.dp),
                onClick = {
                    navController.navigate("tutorial")
                },
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    text = "Samouczek",
                    fontSize = 20.sp
                )
            }
            Button(
                modifier = Modifier
                    .height(75.dp)
                    .width(250.dp)
                    .padding(10.dp),
                onClick = {
                    changeEmailAlert = true
                },
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.settings_change_email),
                    fontSize = 20.sp
                )
            }
            Button(
                modifier = Modifier
                    .height(75.dp)
                    .width(250.dp)
                    .padding(10.dp),
                onClick = {
                    changePasswordAlert = true
                },
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.settings_change_password),
                    fontSize = 20.sp
                )
            }
            Button(
                modifier = Modifier
                    .height(75.dp)
                    .width(250.dp)
                    .padding(10.dp),
                onClick = {
                    deleteDataAlert = true
                },
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.settings_reset_progress),
                    fontSize = 20.sp
                )
            }
//            Button(
//                enabled = false,
//                modifier = Modifier
//                    .height(75.dp)
//                    .width(250.dp)
//                    .padding(10.dp),
//                onClick = {
//                    deleteAccountAlert = true
//                },
//                shape = RoundedCornerShape(15.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Red,
//                    contentColor = Color.White
//                ),
//            ) {
//                Text(
//                    text = "@Usuń konto",
//                    fontSize = 20.sp
//                )
//            }
            Spacer(Modifier.padding(20.dp))
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App logo",
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${stringResource(id = R.string.klikacz_app)} v.0.7\nby Karol Pietrów",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    val url = "https://www.karolpietrow.pl/klikacz/tos"
                    val customTabsIntent = CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .setInstantAppsEnabled(true)
                        .build()
                    customTabsIntent.launchUrl(localContext, url.toUri())
                }
            ) {
                Text(
                    text = stringResource(id = R.string.settings_tos),
                    fontSize = 16.sp
                )
            }
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:klikacz@karolpietrow.pl".toUri()
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(localContext, "Wystąpił problem", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(
                    text = "${stringResource(id = R.string.contact)}: klikacz@karolpietrow.pl",
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.padding(20.dp))
            Text(
                text = stringResource(id = R.string.settings_creative_commons),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    uriHandler.openUri("https://davooda.com/")
                }
            ) {
                Text(
                    text = "Ikona kursora w logo aplikacji: Icons by davooda.com",
                    textAlign = TextAlign.Center
                )
            }
            TextButton(
                onClick = {
                    uriHandler.openUri("https://davooda.com/")
                }
            ) {
                Text(
                    text = "Ikona sklepu w grafice osiągnięcia: Icons by davooda.com",
                    textAlign = TextAlign.Center
                )
            }
            TextButton(
                onClick = {
                    uriHandler.openUri("https://www.flaticon.com/free-icons/wheel-of-fortune")
                }
            ) {
//                Text("Wheel of Fortune icons created by Freepik - Flaticon")
                Text(
                    text = "Ikona koła fortuny w grafice osiągnięcia: Created by Freepik - Flaticon",
                    textAlign = TextAlign.Center
                )
            }
//         Text("© 2025 Wszelkie prawa zastrzeżone (xd)")
        }
    }

}