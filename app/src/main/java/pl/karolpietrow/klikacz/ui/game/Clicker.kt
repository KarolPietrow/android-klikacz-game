package pl.karolpietrow.klikacz.ui.game

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.PersonalisationViewModel
import pl.karolpietrow.klikacz.ui.RewardAd
import java.text.DecimalFormat

@Composable
fun Clicker(context: Context, modifier: Modifier, clickViewModel: ClickViewModel, personalisationViewModel: PersonalisationViewModel, navController: NavController) {
    val view = LocalView.current
    val counter = clickViewModel.counter.collectAsState()
    val topText = personalisationViewModel.topText.collectAsState()
    val buttonText = personalisationViewModel.buttonText.collectAsState()
    val bottomText = personalisationViewModel.bottomText.collectAsState()
    val isPurchaseAvailable = clickViewModel.isPurchaseAvailable.collectAsState()

    val fontSize = when {
        counter.value < 1_000_000 -> 75.sp
        counter.value < 1_000_000_000 -> 60.sp
        counter.value < 1_000_000_000_000 -> 45.sp
        else -> 30.sp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(15.dp),
        horizontalArrangement = Arrangement.End
    ){
        if (!isPurchaseAvailable.value) {
            RewardAd(navController)
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = topText.value,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = DecimalFormat("#,###").format(counter.value),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
//            maxLines = 1,
//            overflow = TextOverflow.Clip
        )

        Spacer(modifier = Modifier.height(50.dp))
        AnimatedButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                if (buttonText.value == "KLIK--") {
                    clickViewModel.decrementCounter(context)
                } else {
                    clickViewModel.incrementCounter(context)
                    clickViewModel.checkPurchases()
                }
            },
            text = buttonText.value
        )
//        Button(
//            modifier = Modifier
//                .width(300.dp)
//                .height(100.dp),
//            shape = RoundedCornerShape(15.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (buttonText.value == "KLIK--" ) { Color.Red } else { Color.Green},  // Kolor tła przycisku
//                contentColor = Color.White     // Kolor tekstu na przycisku
//            ),
//            onClick = {
//                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//                if (buttonText.value == "KLIK--") {
//                    clickViewModel.decrementCounter(context)
//                } else {
//                    clickViewModel.incrementCounter(context)
//                    clickViewModel.checkPurchases()
//                }
//            },
//
//            ) {
//            Text(
//                text = buttonText.value,
//                fontSize = 50.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = bottomText.value,
            modifier = Modifier,
            fontSize = 50.sp
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimatedButton(onClick: () -> Unit, text: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearOutSlowInEasing
        ), label = "buttonScale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .width(300.dp)
            .height(100.dp)
            .scale(scale),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (text == "KLIK--" ) { Color.Red } else { Color.Green},
            contentColor = Color.White
        ),
    ) {
        Text(
            text = text,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold
        )
    }

}