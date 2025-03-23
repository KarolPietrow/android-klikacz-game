package pl.karolpietrow.klikacz.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.R

@Composable
fun NotificationRequest(navController: NavController, clickViewModel: ClickViewModel) {
    val context = LocalContext.current
    val notificationsEnabled = clickViewModel.notificationsEnabled.collectAsState()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate("home") {
                popUpTo("notification") { inclusive = true }
            }
        } else {
//            Toast.makeText(context, "Brak uprawnień do wyświetlania powiadomień.", Toast.LENGTH_SHORT).show()
            openNotifySettings(context)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Icon(Icons.Default.Notifications, "Notification Icon", Modifier.size(75.dp))
                Image(
                    modifier = Modifier
                        .size(75.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "App logo",
                )
            }
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = stringResource(id = R.string.notify_enable_notifications_question),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp
            )
            Text(
                modifier = Modifier.padding(20.dp),
                text = stringResource(id = R.string.notify_description),
                fontSize = 17.sp,
                textAlign = TextAlign.Justify
            )
            Button(
                modifier = Modifier
                    .padding(10.dp)
//                    .width(150.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,  // Kolor tła przycisku
                    contentColor = Color.Black     // Kolor tekstu na przycisku
                ),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (!notificationsEnabled.value) {
                        clickViewModel.enableNotifications()
                    }
                },
            ) {
                Text(
                    text = stringResource(id = R.string.enable).uppercase(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                modifier = Modifier
                    .padding(10.dp)
//                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,  // Kolor tła przycisku
                    contentColor = Color.Black     // Kolor tekstu na przycisku
                ),
                onClick = { // Disable notifications, navigate to GameScreen
                    clickViewModel.disableNotifications()
                    navController.navigate("home") {
                        popUpTo("notification") { inclusive = true }
                    }
                },
            ) {
                Text(
                    text = stringResource(id = R.string.not_now),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun openNotifySettings(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Wymagane uprawnienia")
        .setMessage("Aby kontynuować, przejdź do ustawień i udziel zgody na powiadomienia.")
        .setPositiveButton("Ustawienia") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Anuluj", null)
        .show()
}