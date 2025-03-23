package pl.karolpietrow.klikacz.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import java.text.DecimalFormat

@Composable
fun Personalisation(navController: NavController, clickViewModel: ClickViewModel, personalisationViewModel: PersonalisationViewModel) {
    val counter = clickViewModel.counter.collectAsState()
    val topText = personalisationViewModel.topText.collectAsState()
    val buttonText = personalisationViewModel.buttonText.collectAsState()
    val bottomText = personalisationViewModel.bottomText.collectAsState()

    var editTopText by remember { mutableStateOf(false) }
    var editButton by remember { mutableStateOf(false) }
    var editBottomText by remember { mutableStateOf(false) }

    if (editTopText) {
        var tempText by remember { mutableStateOf(personalisationViewModel.topText.value) }
        AlertDialog(
            onDismissRequest = { editTopText = false },
            confirmButton = {
                Button(
                    onClick = {
                        personalisationViewModel.updateTopText(tempText)
                        editTopText = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text("Zastosuj")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        editTopText = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Anuluj")
                }
            },
            title = { Text("Edycja tekstu górnego") },
            text = {
                Column {
                    Text("Możesz tutaj zmienić tekst wyświetlany na górze głónwnego ekranu gry.")
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = tempText,
                        onValueChange = { newText ->
                            if (newText.length <= 20 && !newText.contains("\n")) {
                                tempText = newText
                            }},
                        label = { Text("Nowy tekst") },
                        singleLine = true
                    )
                }
            },
        )
    }

    if (editButton) {
        var tempText by remember { mutableStateOf(personalisationViewModel.buttonText.value) }
        AlertDialog(
            onDismissRequest = { editButton = false },
            confirmButton = {
                Button(
                    onClick = {
                        personalisationViewModel.updateButtonText(tempText)
                        editButton = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text("Zastosuj")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        editButton = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Anuluj")
                }
            },
            title = { Text("Edycja przycisku") },
            text = {
                Column {
                    Text("Możesz tutaj zmienić tekst przycisku do klikania na głównym ekranie gry.")
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = tempText,
                        onValueChange = { newText ->
                            if (newText.length < 10 && !newText.contains("\n")) {
                                tempText = newText
                            }},
                        label = { Text("Nowy tekst") },
                        singleLine = true
                    )
                }
            },
        )
    }
    if (editBottomText) {
        var tempText by remember { mutableStateOf(personalisationViewModel.bottomText.value) }
        AlertDialog(
            onDismissRequest = { editBottomText = false },
            confirmButton = {
                Button(
                    onClick = {
                        personalisationViewModel.updateBottomText(tempText)
                        editBottomText = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text("Zastosuj")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        editBottomText = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Anuluj")
                }
            },
            title = { Text("Edycja emotki") },
            text = {
                Column {
                    Text("Możesz tutaj zmienić emotkę wyświetlaną na dole, pod przyciskiem do klikania, na głównym ekranie gry. Pssst, teoretycznie to nawet nie musi być emotka, tylko jakiś krótki tekst 😉.")
                    TextField(
                        modifier = Modifier.padding(10.dp),
                        value = tempText,
                        onValueChange = { newText ->
                            if (newText.length < 10 && !newText.contains("\n")) {
                                tempText = newText
                            }},
                        label = { Text("Nowy tekst") },
                        singleLine = true
                    )
                }
            },
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
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
                        Icons.Default.Create,
                        contentDescription = "Personalisation icon",
                        Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(
                        text = "Personalizacja",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            val themeMode = clickViewModel.themeMode.collectAsState().value
            val darkTheme = when (themeMode) {
                0 -> isSystemInDarkTheme()
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(
                        if (darkTheme) Color.Black else Color.White
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = topText.value,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedIconButton(
                        onClick = {
                            editTopText = true
                        }
                    ) {
                        Icon(Icons.Default.Edit, "Edit button")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val fontSize = when {
                        counter.value < 1_000_000 -> 75.sp
                        counter.value < 1_000_000_000 -> 60.sp
                        counter.value < 1_000_000_000_000 -> 45.sp
                        else -> 30.sp
                    }
                    Text(
                        text = DecimalFormat("#,###").format(counter.value),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .width(300.dp)
                            .height(100.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (buttonText.value == "KLIK--") {
                                Color.Red
                            } else {
                                Color.Green
                            },  // Kolor tła przycisku
                            contentColor = Color.White     // Kolor tekstu na przycisku
                        ),
                        onClick = { },
                    ) {
                        Text(
                            text = buttonText.value,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedIconButton(
                        onClick = {
                            editButton = true
                        }
                    ) {
                        Icon(Icons.Default.Edit, "Edit button")
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bottomText.value,
                        modifier = Modifier,
                        fontSize = 50.sp
                    )
                    OutlinedIconButton(
                        onClick = {
                            editBottomText = true
                        }
                    ) {
                        Icon(Icons.Default.Edit, "Edit button")
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
                TextButton(
                    onClick = {
                        personalisationViewModel.deleteDataLocal()
                    }
                ) {
                    Text(
                        text = "Przywróć domyślne",
                        color = Color.Red
                    )
                }
            }
        }
    }
}