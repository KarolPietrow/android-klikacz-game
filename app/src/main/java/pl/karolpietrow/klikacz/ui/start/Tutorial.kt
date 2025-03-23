package pl.karolpietrow.klikacz.ui.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.R

@Composable
fun Tutorial(navController: NavHostController, clickViewModel: ClickViewModel) {
    var currentScreen by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
    ) {
//        Spacer(modifier = Modifier.height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .padding(1.dp)
                        .weight(1f)
                        .height(10.dp)
                        .background(
                            if (index <= currentScreen) MaterialTheme.colorScheme.primary
                            else Color.Gray,
                            RoundedCornerShape(25)
                        )
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                if(currentScreen>0) { currentScreen--}
                            }
                        )
//                    .background(Color.Red)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                if(currentScreen<5) { currentScreen++}
                            }
                        )
//                    .background(Color.Blue)
                )
            }
            when(currentScreen) {
                0 -> Screen0()
                1 -> Screen1()
                2 -> Screen2()
                3 -> Screen3()
                4 -> Screen4()
                5 -> Screen5(navController, clickViewModel)
                else -> Unit
            }
        }
    }
}

@Composable
fun Screen0() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier
//                .weight(0.1f)
                .padding(10.dp),
            text = "Witaj w Klikaczu!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier
//                .weight(0.1f)
                .padding(10.dp),
            text = "Klikacz to gra typu Clicker, w której celem jest zdobycie jak największego wyniku!",
            fontSize = 20.sp,
        )
        Image(
            modifier = Modifier
//                .weight(0.8f)
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black))
//                .scale(0.75f)
                .height(550.dp),
            painter = painterResource(id = R.drawable.screenshot0),
            contentDescription = "App screenshot",
        )
        Text(
            modifier = Modifier
//                .weight(0.1f)
                .padding(10.dp),
            text = "Naciśnij prawą stronę ekranu, aby kontynuować",
            fontSize = 20.sp,
        )
    }
}

@Composable
fun Screen1() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Sklep z ulepszeniami",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Odwiedź sklep i kup ulepszenia, które pomogą ci w szybszym zwiększaniu wyniku!",
            fontSize = 20.sp,
        )
        Image(
            modifier = Modifier
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black))
//                .scale(0.75f)
                .height(600.dp),
            painter = painterResource(id = R.drawable.screenshot1),
            contentDescription = "App screenshot",
        )
    }
}

@Composable
fun Screen2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Ranking graczy",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Rywalizuj z innymi i przeglądaj najlepszych graczy!",
            fontSize = 20.sp,
        )
        Image(
            modifier = Modifier
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black)),
//                .scale(0.75f)
//                .height(600.dp),
            painter = painterResource(id = R.drawable.screenshot2),
            contentDescription = "App screenshot",
        )
    }
}

@Composable
fun Screen3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Osiągnięcia",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Zdobywaj osiągnięcia i przeglądaj je w zakładce Profil!",
            fontSize = 20.sp,
        )
        Image(
            modifier = Modifier
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black)),
//                .scale(0.75f)
//                .height(600.dp),
            painter = painterResource(id = R.drawable.screenshot3),
            contentDescription = "App screenshot",
        )
    }
}
@Composable
fun Screen4() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Koło Fortuny Klikacza",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Obejrzyj reklamę, a potem potrząśnij telefonem, aby wylosować przydatne nagrody!",
            fontSize = 20.sp,
        )
        Image(
            modifier = Modifier
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black))
//                .scale(0.75f)
                .height(600.dp),
            painter = painterResource(id = R.drawable.screenshot4),
            contentDescription = "App screenshot",
        )
    }
}

@Composable
fun Screen5(navController: NavHostController, clickViewModel: ClickViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            text = "Miłej zabawy z Klikaczem!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Button(
            modifier = Modifier,
            onClick = {
                clickViewModel.completeTutorial()
                navController.navigate("home") {
                    popUpTo("tutorial") { inclusive = true }
                }
            },
        ) {
            Text("ROZPOCZNIJ")
        }
    }
}
