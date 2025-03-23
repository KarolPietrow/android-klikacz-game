package pl.karolpietrow.klikacz.ui

import android.app.Activity
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import pl.karolpietrow.klikacz.BuildConfig

@Composable
fun RewardAd(navController: NavController) {
    val context = LocalContext.current

    val adUnitId = BuildConfig.REWARD_AD_TEST_ID
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdMob", "Reklama nie załadowana: ${adError.message}")
                rewardedAd = null
                isLoading = false
            }
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("AdMob", "Reklama załadowana")
                rewardedAd = ad
                isLoading = false
            }
        })
    }

    LaunchedEffect(Unit) {
        loadAd()
    }

    if (rewardedAd != null) {
        Button(
            onClick = {
                val activity = context as? Activity
                rewardedAd?.show(activity!!) { rewardItem: RewardItem ->
                    Log.d("AdMob", "Otrzymano nagrodę: ${rewardItem.amount} ${rewardItem.type}")
                    navController.navigate("wheel") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        ) {
            Text("KOŁO FORTUNY")
        }
    }
}