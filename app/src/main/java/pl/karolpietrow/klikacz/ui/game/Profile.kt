package pl.karolpietrow.klikacz.ui.game

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.AuthState
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.ImageViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import pl.karolpietrow.klikacz.R
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import androidx.core.graphics.scale

@Composable
fun Profile(modifier: Modifier, clickViewModel: ClickViewModel, navController: NavController, authViewModel: AuthViewModel, personalisationViewModel: PersonalisationViewModel, imageViewModel: ImageViewModel) {
    val counter = clickViewModel.counter.collectAsState()
    val upgradeCount = clickViewModel.upgradeCount.collectAsState()
    val achievementCount = clickViewModel.achievementCount.collectAsState()
    val personalisationEnabled = clickViewModel.personalisationEnabled.collectAsState()
    val context = LocalContext.current
    val name = authViewModel.name.collectAsState()
    val username = authViewModel.username.collectAsState()
    val joinDate = authViewModel.joinDate.collectAsState()
    val email = authViewModel.email.collectAsState()
    val achievements = clickViewModel.achievements.collectAsState().value
    val wheelSpins = clickViewModel.wheelCount.collectAsState()
    val authState = authViewModel.authState.observeAsState()
    val image = imageViewModel.image.collectAsState()

    var signOutAlert by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }
    var changeNameAlert by remember { mutableStateOf(false) }

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var base64String by remember { mutableStateOf("") }
    var imageToDisplay by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val scaledBitmap = scaleBitmap(context, uri, 256, 256)
                imageBitmap = scaledBitmap
                base64String = bitmapToBase64(scaledBitmap)
                imageViewModel.saveBase64(base64String) {
                    imageViewModel.loadBase64()
                }
            }
        }
    )

    LaunchedEffect(true) {
        imageViewModel.loadBase64()
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }

    LaunchedEffect(image.value) {
        if (image.value.isNotEmpty()) {
            imageToDisplay = base64ToBitmap(image.value)
        } else {
            imageToDisplay = null
        }
    }

    LaunchedEffect(authState.value) {
        if (authState.value == AuthState.Unauthenticated) {
            imageViewModel.clearLocalData()
        }
    }

    if (signOutAlert) {
        AlertDialog(
            icon = { Icon(painter = painterResource(id = R.drawable.logout_icon), contentDescription = "Logout") },
            onDismissRequest = { signOutAlert = false},
            confirmButton = {
                Button(
                    onClick = {
//                        clickViewModel.savePointDataCloud()
                        val notificationManager = ContextCompat.getSystemService(
                            context,
                            NotificationManager::class.java
                        ) as NotificationManager
                        notificationManager.cancelAll()

                        authViewModel.signout()
                        clickViewModel.deletePointDataLocal()
                        personalisationViewModel.deleteDataLocal()
                        signOutAlert = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Tak, wyloguj")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        signOutAlert = false
                    }
                ) {
                    Text("Nie, zostań")
                }
            },
            title = { Text("Na pewno?") },
            text = { Text("Czy na pewno chcesz się wylogować?")}
        )
    }
    if (changeNameAlert) {
        val currentName = authViewModel.name.collectAsState()
        var newName by remember { mutableStateOf(currentName.value) }
        AlertDialog(
            icon = { Icon(Icons.Default.Edit, contentDescription = "Edit icon") },
            onDismissRequest = { changeNameAlert = false},
            confirmButton = {
                Button(
                    onClick = {
                        if (newName != currentName.value && newName.isNotEmpty()) {
                            authViewModel.changeName(newName)
                            changeNameAlert = false
                        }
                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Red
//                    )
                ) {
                    Text("Zmień nazwę")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        changeNameAlert = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text("Zmień nazwę") },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nazwa") }
                )
            }
        )
    }

    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top,
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if (imageToDisplay == null) {
                    Icon(
                        painter = painterResource(id = R.drawable.default_profile_icon),
                        contentDescription = "Profile icon",
                        Modifier
                            .size(100.dp)
                            .clickable(
                                onClick = {
                                    clicked = !clicked
                                }
                            )
                    )
                } else {
                    imageToDisplay?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Wybrany obraz",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    clicked = !clicked
                                }
                        )
                    }
                }
                Box {
                    Text(
                        text = name.value,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = {clicked = !clicked})
                    )
                    DropdownMenu(
                        expanded = clicked,
                        onDismissRequest = { clicked = false }
                    ) {
                        if (image.value.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Zmień zdjęcie profilowe") },
                                onClick = {
                                    launcher.launch("image/*")
                                    clicked = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Usuń zdjęcie profilowe") },
                                onClick = {
                                    imageViewModel.deleteImageFromCloud()
                                    clicked = false
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Ustaw zdjęcie profilowe") },
                                onClick = {
                                    launcher.launch("image/*")
                                    clicked = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Zmień nazwę użytkownika") },
                            onClick = {
                                changeNameAlert = true
                                clicked = false
                            }
                        )
                    }
                }
                Text(
                    text = "@${username.value}",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Email: ${email.value}",
                    fontSize = 20.sp
                )
                Text(
                    text = "${stringResource(id = R.string.join_date)}: ${joinDate.value}",
                    fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${stringResource(id = R.string.score)}: ${DecimalFormat("#,###").format(counter.value)}",
                    fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${stringResource(id = R.string.shop_purchased_upgrades)}: ${upgradeCount.value}",
                    fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${stringResource(id = R.string.unlocked_achievements)}: ${achievementCount.value}",
                    fontSize = 20.sp,
                )
                Text(
                    text = "${stringResource(id = R.string.fortune_wheel_spins)}: ${wheelSpins.value}",
                    fontSize = 20.sp,
                )
                Row(
                    modifier.padding(10.dp)
                ){
                    Button(
                        modifier = Modifier
//                    .width(100.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(1.dp)
                            .height(75.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,  // Kolor tła przycisku
                            contentColor = Color.Magenta     // Kolor tekstu na przycisku
                        ),
                        onClick = {
                            if (personalisationEnabled.value) {
                                navController.navigate("personalisation")
                            } else {
                                Toast.makeText(context, "Nie odblokowałeś/aś jeszcze tej funkcji!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Create, "Personalisation")
                            Text(text = stringResource(id = R.string.personalisation), fontSize = 10.sp)
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(1.dp)
                            .height(75.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Red
                        ),
                        onClick = {
                            signOutAlert = true
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(painter = painterResource(id = R.drawable.logout_icon), "Log out")
                            Text(text = stringResource(id = R.string.log_out), fontSize = 11.sp)
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(75.dp)
                            .padding(1.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,  // Kolor tła przycisku
                            contentColor = Color.Black     // Kolor tekstu na przycisku
                        ),
                        onClick = {
                            navController.navigate("settings")
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Settings, "Settings")
                            Text(text = stringResource(id = R.string.settings), fontSize = 11.sp)
                        }
                    }
                }
                Text(
                    text = "${stringResource(id = R.string.unlocked_achievements)}:",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        items(achievements) { achievement ->
            if (achievement.isUnlocked) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = achievement.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = achievement.description
                            )
                        }
                        when (achievement.achievementType) {
                            0 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.app_icon),
                                    contentDescription = "Click score achievement logo - unlocked",
                                )
                            }
                            1 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.wheel_logo),
                                    contentDescription = "Fortune Wheel achievement logo - unlocked",
                                )
                            }
                            2 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.store_logo),
                                    contentDescription = "Upgrade achievement logo - unlocked",
                                )
                            }
                            3 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.secret_logo),
                                    contentDescription = "Other achievement logo - unlocked",
                                )
                            }
                        }
                    }
                }
            }
        }
        item{
            Text(
                text = "${stringResource(id = R.string.locked_achievements)}:",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        items(achievements) { achievement ->
            if (!achievement.isUnlocked) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🔒 " + achievement.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (achievement.isSecret) {
                                Text(
                                    text = "To osiągnięcie jest tajne. Odblokuj je, aby poznać treść. 😉",
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = achievement.description
                                )
                            }
                        }
                        when (achievement.achievementType) {
                            0 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.logo_bw),
                                    contentDescription = "Click score achievement logo - locked",
                                )
                            }
                            1 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.wheel_logo_bw),
                                    contentDescription = "Fortune Wheel achievement logo - locked",
                                )
                            }
                            2 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.store_logo_bw),
                                    contentDescription = "Upgrade achievement logo - locked",
                                )
                            }
                            3 -> {
                                Image(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    painter = painterResource(id = R.drawable.secret_logo_bw),
                                    contentDescription = "Other achievement logo - locked",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun scaleBitmap(context: Context, uri: Uri, width: Int, height: Int): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source)
    return bitmap.scale(width, height)
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun base64ToBitmap(base64: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}