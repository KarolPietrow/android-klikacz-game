package pl.karolpietrow.klikacz.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import pl.karolpietrow.klikacz.BuildConfig

@Composable
fun BannerAd(modifier: Modifier) {
    val context = LocalContext.current
    val adView = remember { AdView(context) }

    adView.setAdSize(AdSize.BANNER)
    adView.adUnitId = BuildConfig.BANNER_AD_TEST_ID

    val adRequest = AdRequest.Builder().build()

    adView.loadAd(adRequest)

    AndroidView(
        factory = { adView },
        modifier = modifier
            .fillMaxWidth()
    )
}