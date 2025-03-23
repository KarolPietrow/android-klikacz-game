package pl.karolpietrow.klikacz.ui.game

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.karolpietrow.klikacz.AuthViewModel
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.R
import java.text.DecimalFormat

@Composable
fun Ranking(context: Context, modifier: Modifier, clickViewModel: ClickViewModel, authViewModel: AuthViewModel) {
    val scrollState = rememberScrollState()
    var ranking by remember { mutableStateOf<List<Pair<String, Long>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val username = authViewModel.username.collectAsState()
    val achievements = clickViewModel.achievements.collectAsState()

    LaunchedEffect(Unit) {
        clickViewModel.savePointDataLocal()
        clickViewModel.savePointDataCloud()
        authViewModel.getTopPlayers(onResult = { ranking = it }, isLoading = { isLoading = it })
    }
    LaunchedEffect(ranking) {
        if (ranking.isNotEmpty()) {
            if (ranking[0].first == username.value) {
                clickViewModel.unlockAchievement(context, achievements.value.find { it.id == 301 })
            }
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Ładowanie...",
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
    } else {
        Column(
            modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.leaderboard_icon),
                    contentDescription = "Leaderboard icon",
                    Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = stringResource(id = R.string.ranking),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        authViewModel.getTopPlayers(
                            onResult = { ranking = it },
                            isLoading = { isLoading = it })
                    }
                ) {
                    Icon(Icons.Default.Refresh, "Refresh icon")
                }
            }
            Text("🏆 ${stringResource(id = R.string.ranking_top_players)}", fontSize = 20.sp)
            ranking.forEachIndexed { index, (username, score) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = "${index + 1}. ",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = username,
                                fontSize = 25.sp
                            )
                        }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "${DecimalFormat("#,###").format(score)} ${stringResource(id = R.string.ranking_points)}",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}