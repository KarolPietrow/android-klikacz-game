package pl.karolpietrow.klikacz.ui.start

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import pl.karolpietrow.klikacz.BuildConfig
import pl.karolpietrow.klikacz.R

class GoogleAuthUiClient (
    private val context: Context,
) {
    suspend fun signInRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(BuildConfig.DEFAULT_WEB_CLIENT_ID)
                    .build()
            )
            .build()
    }
}
