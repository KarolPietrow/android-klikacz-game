package pl.karolpietrow.klikacz.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import pl.karolpietrow.klikacz.ClickViewModel
import pl.karolpietrow.klikacz.ui.theme.SuperApkaTheme

class MainActivity : ComponentActivity() {

    val context = this

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.resources.configuration.smallestScreenWidthDp < 600) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

//        Log.d("KLIKACZAPP", this.resources.configuration.screenWidthDp.toString())
        Log.d("KLIKACZAPP", this.resources.configuration.smallestScreenWidthDp.toString())

        createNotificationChannel()
        MobileAds.initialize(this) { }

        setContent {
            val openScreen = intent.getStringExtra("openScreen")
            val clickViewModel: ClickViewModel = viewModel()
            val themeMode = clickViewModel.themeMode.collectAsState().value
            SuperApkaTheme(
                themeMode = themeMode
            ) {
                Navigation(context, openScreen)
            }
        }
    }

    private fun createNotificationChannel() {
        val CHANNEL_ID = "Default"
        val CHANNEL_NAME = "Default notification channel"
        val CHANNEL_DESCRIPTION = "Default notification channel for Clicker App."

        // Pomijam sprawdzenie wersji Androida, ponieważ minimalne API dla projektu jest ustawione na 26
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}