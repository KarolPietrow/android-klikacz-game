package pl.karolpietrow.klikacz.ui.game

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.R
import java.text.DecimalFormat

@Composable
fun Shop(modifier: Modifier, clickViewModel: ClickViewModel) {
    Log.d("Shop", "Shop recomposed!")
    val localContext = LocalContext.current
    val counter = clickViewModel.counter.collectAsState()
    val multiplier = clickViewModel.multiplier.collectAsState()
    val autoMultiplier = clickViewModel.autoMultiplier.collectAsState()
    val autoFrequency = clickViewModel.autoFrequency.collectAsState()
    val perSecFreq: Float = autoFrequency.value.toFloat() / 1000
    val upgrades = clickViewModel.upgrades.collectAsState().value

    val inlineContent =
        mapOf(
            Pair(
                "inlineContent",
                InlineTextContent(
                    // Placeholder tells text layout the expected size and vertical alignment of
                    // children composable.
                    Placeholder(
                        width = 1.em,
                        height = 1.em,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.click_icon),
                        contentDescription = "Click icon",
                        modifier = Modifier
//                                        .size(50.dp)
                            .fillMaxSize()
                    )
                }
            )
        )
    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(10.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.store_bottom_icon),
                        contentDescription = "Shop view",
                        Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(
                        text = stringResource(id = R.string.shop_diminutive),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.shop_description1))
                        appendInlineContent("inlineContent", "[icon]")
                        append(stringResource(id = R.string.shop_description2))
                    },
                    inlineContent = inlineContent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                )
                Text(
                    text = "- Ulepszenia mnożnika powodują, że Twoje kliknięcia zwiększają wynik szybciej (większą wartością)." +
                            "\n - Ulepszenia AutoKlik sprawiają, że nawet kiedy nie klikasz, wynik jest zwiększany!",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Liczba ")
                        appendInlineContent("inlineContent", "[icon]")
                        append(" kliknięć: ${DecimalFormat("#,###").format(counter.value)}")
                    },
                    inlineContent = inlineContent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = "${stringResource(id = R.string.multiplier)}: ${DecimalFormat("#,###").format(multiplier.value)}x",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = buildAnnotatedString {
                        append("${stringResource(id = R.string.autoclick_frequency)}: 1")
                        appendInlineContent("inlineContent", "[icon]")
                        append("/${perSecFreq}s.")
                    },
                    inlineContent = inlineContent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = "${stringResource(id = R.string.autoclick_multiplier)}: ${DecimalFormat("#,###").format(autoMultiplier.value)}x",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }

        items(upgrades) { upgrade ->
            if (!upgrade.isPurchased) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
//                            .padding(5.dp),
//                            .clip(RoundedCornerShape(10.dp)),
//                            .background(Color(0xffb8c6db)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(0.75f).padding(5.dp)) {
                            if (upgrade.upgradeType == 3) {
                                Text(
                                    upgrade.name,
                                    fontSize = 20.sp,
                                    color = Color.Magenta,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(upgrade.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(upgrade.description, fontSize = 13.sp)
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = buildAnnotatedString {
                                    append("${DecimalFormat("#,###").format(upgrade.price)} ")
                                    appendInlineContent("inlineContent", "[icon]")
                                },
                                inlineContent = inlineContent,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(0.25f)
                                .padding(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                enabled = upgrade.price <= counter.value,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.White
                                ),
                                onClick = {
                                    clickViewModel.purchaseUpgrade(localContext, upgrade)
                                    Toast.makeText(
                                        localContext,
                                        "Zakupiono ulepszenie! 🥳",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                Text("Kup")
                            }
                        }
                    }
                }
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.shop_purchased_upgrades),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        items(upgrades) { upgrade ->
            if (upgrade.isPurchased) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
//                        .padding(5.dp)
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(Color.LightGray),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.padding(5.dp)) {
                            if (upgrade.upgradeType == 3) {
                                Text(
                                    upgrade.name,
                                    fontSize = 20.sp,
                                    color = Color.Magenta,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(upgrade.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(upgrade.description, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}