package pl.karolpietrow.klikacz.ui.start

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import pl.karolpietrow.klikacz.R

class GoogleAuthUiClient (
    private val context: Context,
) {
    suspend fun signInRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    suspend fun handleSignInResult(result: Credential): String {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.data)
        return googleIdTokenCredential.idToken
    }
}
