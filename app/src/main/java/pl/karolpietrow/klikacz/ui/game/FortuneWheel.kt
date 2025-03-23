package pl.karolpietrow.klikacz.ui.game

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.R
import pl.karolpietrow.klikacz.Reward
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun FortuneWheel(context: Context, navController: NavController, clickViewModel: ClickViewModel, modifier: Modifier = Modifier) {
    val commonReward = listOf(
        Reward(0, 0, 0, 0.25), // + Score * 0.25
        Reward(1, 0, 0, 0.5), // + Score * 0.5
        Reward(2, 0, 0, 1.0), // + Score * 1.0
        Reward(3, 1, 0, 0.25), // + Modifier * 0.25
        Reward(4, 1, 0, 0.5), // + Modifier * 0.5
        Reward(4, 1, 0, 1.0) // + Modifier * 1.0
    )
    val rareReward = listOf(
        Reward(10, 0, 1, 2.0), // + Score * 2.0
        Reward(11, 1, 1, 2.0) // + Modifier * 2.0
    )
    val legendaryReward = listOf(
        Reward(20, 0, 2, 20.0), // + Score * 20
        Reward(21, 1, 2, 10.0) // + Modifier * 10
    )
    val rewardList = remember { mutableStateListOf<Reward>() }
    val view = LocalView.current
    val rotation = remember { Animatable(0f) }
    val sections = listOf("1", "2", "3", "4", "5", "6")
    val colors = listOf(Color.Magenta, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Red)
    val counter = clickViewModel.counter.collectAsState()
    val multiplier = clickViewModel.multiplier.collectAsState()
    val achievements = clickViewModel.achievements.collectAsState()
    Log.d("KLIKACZAPP", "INITIAL counter: ${counter.value}, INITIAL multiplier: ${multiplier.value}")

    var selectedPrize by remember { mutableStateOf("") }
    var spinAvailable by remember { mutableStateOf(true) }

    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val compositionCounter = rememberSaveable { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()


    val sensorManager = remember {
        localContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val shakeThreshold = 40f // Sensitivity threshold for shake detection
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    val acceleration = sqrt(x * x + y * y + z * z)
                    if (acceleration > shakeThreshold && spinAvailable) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        spinAvailable = false
                        coroutineScope.launch {
                            val randomSpin = Random.nextInt(4200, 6000).toFloat() // Losowa ilość stopni do obrotu
                            val decay = exponentialDecay<Float>(frictionMultiplier = 0.2f) // Efekt zwalniania
                            rotation.animateDecay(randomSpin, decay)

                            val finalRotation = ((rotation.value % 360) + 360) % 360
                            val adjustedAngle = (270f - finalRotation + 360f) % 360f
                            val anglePerSection = 360f / 6
                            val sectorIndex = (adjustedAngle / anglePerSection).toInt()
                            selectedPrize = sectorIndex.toString()
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    fun getRewardColor(rarity: Int): Color {
        return when (rarity) {
            0 -> Color.Black // Common
            1 -> Color.Blue // Rare
            2 -> Color(0xFFd4bb00)
            else -> Color.Black
        }
    }

    fun getRewardInfo(reward: Reward): String {
        return when (reward.rewardType) {
            0 -> "+ ${ DecimalFormat("#,###").format((counter.value * reward.value).toLong()) } do wyniku"
            1 -> "+ ${ DecimalFormat("#,###").format((multiplier.value * reward.value).toLong()) } do mnożnika"
            else -> "N/A"
        }
    }

    fun receiveReward(reward: Reward) {
        if (reward.rarity == 2) {
            clickViewModel.unlockAchievement(context, achievements.value.find { it.id == 105 })
        }

        if (reward.rewardType == 0) {
            clickViewModel.getCounterReward((counter.value * reward.value).toLong())
        } else if (reward.rewardType == 1) {
            clickViewModel.updateMultiplier((multiplier.value * reward.value).toLong())
        }
    }

    LaunchedEffect(Unit) {
        compositionCounter.intValue++
    }

    if (compositionCounter.intValue > 1) {
        LaunchedEffect(Unit) {
            Log.d("KLIKACZAPP", "Detected recomposition - navigating away")
            navController.navigate("home") {
                popUpTo("wheel") { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        rewardList.clear()
        rewardList.addAll(
            listOf(
                commonReward.random(),
                commonReward.random(),
                commonReward.random(),
                commonReward.random(),
                rareReward.random(),
                if (Random.nextInt(11) == 5) legendaryReward.random() else rareReward.random()
            ).shuffled()
        )

    }
    DisposableEffect(Unit) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "\uD83C\uDFA1 ${stringResource(id = R.string.fortune_wheel)}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.fw_welcome),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (rewardList.isNotEmpty()) {
                Text(
                    text = "1: " + getRewardInfo(rewardList[0]),
                    fontSize = 20.sp,
                    color = getRewardColor(rewardList[0].rarity),
                    fontWeight = if (rewardList[0].rarity == 2) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "2: " + getRewardInfo(rewardList[1]),
                    fontSize = 20.sp,
                    color = getRewardColor(rewardList[1].rarity),
                    fontWeight = if (rewardList[1].rarity == 2) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "3: " + getRewardInfo(rewardList[2]),
                    fontSize = 20.sp,
                    color = getRewardColor(rewardList[2].rarity),
                    fontWeight = if (rewardList[2].rarity == 2) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "4: " + getRewardInfo(rewardList[3]),
                    fontSize = 20.sp,
                    color = getRewardColor(rewardList[3].rarity),
                    fontWeight = if (rewardList[3].rarity == 2) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "5: " + getRewardInfo(rewardList[4]),
                    fontSize = 20.sp,
                    color = getRewardColor(rewardList[4].rarity),
                    fontWeight = if (rewardList[4].rarity == 2) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "6: " + getRewardInfo(rewardList[5]),
                    fontSize = 20.sp,
                    color = getRewardColor(rewardList[5].rarity),
                    fontWeight = if (rewardList[5].rarity == 2) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.LightGray, shape = CircleShape)
            ) {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)
                val anglePerSection = 360f / sections.size

                // Wskaźnik (strzałka)
                drawLine(
                    color = Color.Black,
                    start = Offset(center.x, -20f),
                    end = Offset(center.x, 50f),
                    strokeWidth = 10f
                )

                // Obracanie koła
                withTransform({
                    rotate(rotation.value, pivot = center)
                }) {
                    sections.forEachIndexed { index, section ->
                        val startAngle = index * anglePerSection
                        val textAngle = (startAngle + anglePerSection / 2) * Math.PI / 180f
                        val textX = center.x + radius * 0.6f * cos(textAngle).toFloat()
                        val textY = center.y + radius * 0.6f * sin(textAngle).toFloat()

                        drawContext.canvas.nativeCanvas.apply {
                            drawArc(
                                color = colors[index],
                                startAngle = startAngle,
                                sweepAngle = anglePerSection,
                                useCenter = true,
                                topLeft = Offset.Zero,
                                size = Size(size.width, size.height)
                            )
                            save()
                            rotate((startAngle + anglePerSection / 2 + 90f), textX, textY)
                            drawText(
                                section,
                                textX,
                                textY,
                                Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = 100f
                                    textAlign = Paint.Align.CENTER
                                    typeface = Typeface.DEFAULT_BOLD
                                }
                            )
                            restore()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            if (selectedPrize.isEmpty()) {
                if (spinAvailable) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.fw_shake),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 35.sp
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.fw_wait),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 35.sp
                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.fw_won),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getRewardInfo(rewardList[selectedPrize.toInt()]),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            Log.d("KLIKACZAPP", "Current coutner: ${counter.value}, Current multiplier: ${multiplier.value}")

                            val reward = rewardList[selectedPrize.toInt()]

                            receiveReward(reward)
//                                delay(1000)

                            Log.d("KLIKACZAPP", "!! NEW !! coutner: ${counter.value}, New multiplier: ${multiplier.value}")

                            clickViewModel.incrementWheel(context)
                            navController.navigate("home") {
                                popUpTo("wheel") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.fw_accept))
                }
            }
        }
    }
}