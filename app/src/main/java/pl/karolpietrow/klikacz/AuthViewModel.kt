package pl.karolpietrow.klikacz

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import pl.karolpietrow.klikacz.ui.start.GoogleAuthUiClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.content.edit
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _name = MutableStateFlow(sharedPreferences.getString("name", "N/A") ?: "N/A")
    val name: StateFlow<String> = _name

    private val _username = MutableStateFlow(sharedPreferences.getString("username", "N/A") ?: "N/A")
    val username: StateFlow<String> = _username

    private val _joinDate = MutableStateFlow(sharedPreferences.getString("join_date", "N/A") ?: "N/A")
    val joinDate: StateFlow<String> = _joinDate

    private val _email = MutableStateFlow(sharedPreferences.getString("email", "N/A") ?: "N/A")
    val email: StateFlow<String> = _email

    // Google
    private val googleAuthUiClient = GoogleAuthUiClient(application.applicationContext)

    fun signInWithGoogle(credentialManager: CredentialManager, context: Context) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val request = googleAuthUiClient.signInRequest()
                val credential = credentialManager.getCredential(
                    request = request,
                    context = context
                ).credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    idToken.let {
//                    val firebaseCredential = GoogleAuthProvider.getCredential(it, null)
                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("KLIKACZAPP", "Successfully signed in")
                                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                                    if (firebaseUser != null) {
                                        val db = FirebaseFirestore.getInstance()
                                        val userDocRef =
                                            db.collection("users").document(firebaseUser.uid)
                                                .collection("data").document("user_data")

                                        userDocRef.get()
                                            .addOnSuccessListener { document ->
                                                if (!document.exists()) { // Nie utworzono konta, wymagana rejestracja
                                                    Log.d(
                                                        "KLIKACZAPP",
                                                        "No account detected, register required"
                                                    )

                                                    _authState.value = AuthState.GoogleRegisterRequired
                                                } else { // Konto istnieje, można się zalogować
                                                    Log.d("KLIKACZAPP", "Account exists, logging in")
                                                    _authState.value = AuthState.Authenticated
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("KLIKACZAPP", e.message ?: "Error")
                                                Log.d("KLIKACZAPP", "Error reading data")
                                                _authState.value = AuthState.Unauthenticated
                                            }
                                    } else {
                                        Log.d("KLIKACZAPP", "Error occurred")
                                    }
//                                _signInResult.value = "Zalogowano przez Google!"
                                } else {
                                    Log.d("KLIKACZAPP", "Error signing in")
                                    _authState.value = AuthState.Unauthenticated
//                                _signInResult.value = "Błąd logowania: ${task.exception?.message}"
                                }
                            }
                    }
                } else {
                    Log.e("GoogleSignIn", "Nieprawidłowy typ credentiala: ${credential.javaClass.name}")
                    _authState.value = AuthState.Unauthenticated

                }
//                val idToken = googleAuthUiClient.handleSignInResult(credential)
            } catch (e: NoCredentialException) {
                Log.e("GoogleSignIn", "Brak zapisanych poświadczeń lub użytkownik anulował logowanie")
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Błąd logowania: ${e.message}")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun registerWithGoogle(context: Context, username: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)

        if (username.isEmpty()) {
            Toast.makeText(context, "Insufficient data provided", Toast.LENGTH_SHORT).show()
        }
        isUsernameAvailable(username) { isAvailable ->
            if (isAvailable) {
                _authState.value = AuthState.Loading
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val googleName = firebaseUser?.displayName ?: "Imię"
                if (firebaseUser != null) {
                    saveUserDataCloud(googleName, username, firebaseUser.uid, current)
                    getEmail()
                    _authState.value = AuthState.Authenticated
                }
            } else {
                Toast.makeText(context, "Nazwa użytkownika zajęta. Wybierz inną.", Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.GoogleRegisterRequired
            }
        }
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(context: Context, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email or password not provided", Toast.LENGTH_SHORT).show()
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful) {
//                    saveUserDataLocal()
                    _authState.value = AuthState.Authenticated
                } else {
                    Toast.makeText(context, task.exception?.message?: "Nieznany błąd", Toast.LENGTH_SHORT).show()
                    _authState.value = AuthState.Unauthenticated
                }
            }
    }

    fun isUsernameAvailable(username: String, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collectionGroup("data")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                onResult(documents.isEmpty) // true jeśli nazwa wolna, false jeśli zajęta
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(false) // W razie błędu zakładamy, że username jest zajęty
            }
    }

    fun register(context: Context, name: String, username: String, email: String, password: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(context, "Insufficient data provided", Toast.LENGTH_SHORT).show()
        }
        isUsernameAvailable(username) { isAvailable ->
            if (isAvailable) {
                _authState.value = AuthState.Loading
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser!!.uid
                            saveUserDataCloud(name, username, userId, current)
                            _authState.value = AuthState.Authenticated
                            Toast.makeText(context, "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.message ?: "Nieznany błąd",
                                Toast.LENGTH_SHORT
                            ).show()
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
            } else {
                Toast.makeText(context, "Nazwa użytkownika zajęta. Wybierz inną.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun signout() {
        auth.signOut()
        deleteUserDataLocal()
        _authState.value = AuthState.Unauthenticated
    }

    fun getEmail(): String {
        val user = auth.currentUser
        return if (user != null) {
            user.email ?: "N/A"
        } else "N/A"
    }

    fun updateEmail(context: Context, email: String, password: String, newEmail: String) {
        val user = auth.currentUser

        if (user != null) {
            if (user.email == email) {
                val credential: AuthCredential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            user.verifyBeforeUpdateEmail(newEmail)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "E-mail weryfikacyjny wysłany.", Toast.LENGTH_SHORT).show()
                                        auth.signOut()
                                    } else {
                                        Toast.makeText(context, task.exception?.message ?: "Nieznany błąd", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
            } else {
                Toast.makeText(context, "Niepoprawny e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updatePassword(context: Context, email: String, password: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            if (user.email == email) {
                val credential: AuthCredential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Hasło zostało zmienione.", Toast.LENGTH_SHORT).show()
                                        auth.signOut()
                                    } else {
                                        Toast.makeText(context, task.exception?.message ?: "Nieznany błąd", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
            } else {
                Toast.makeText(context, "Niepoprawny e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun verifyCredential(email: String, password: String, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            if (user.email == email) {
                val credential: AuthCredential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true)
                        } else {
                            onResult(false)
                        }
                    }
            } else onResult(false)
        } else onResult(false)
    }

    fun resetPassword(context: Context, email: String) {
        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Jeśli konto o podanym adresie e-mail istnieje, wysłano maila.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, task.exception?.message ?: "Nieznany błąd", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Podaj poprawny adres e-mail.", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveUserDataLocal() {
        sharedPreferences.edit() { putString("name", _name.value) }
        sharedPreferences.edit() { putString("username", _username.value) }
        sharedPreferences.edit() { putString("join_date", _joinDate.value) }
        sharedPreferences.edit() { putString("email", _email.value) }
    }

    fun deleteUserDataLocal() {
        sharedPreferences.edit() {
            remove("name")
                .remove("username")
                .remove("join_date")
                .remove("email")
        }

        _name.value = "N/A"
        _username.value = "N/A"
        _joinDate.value = "N/A"
        _email.value = "N/A"
    }

    suspend fun getUserDataCloud(): Boolean {
        Log.d("KLIKACZAPP", "getUserDataCloud()")
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        val db = FirebaseFirestore.getInstance()
        return withTimeoutOrNull(5000) {
            try {
                val document = db.collection("users")
                    .document(user.uid)
                    .collection("data")
                    .document("user_data")
                    .get(Source.SERVER)
                    .await()

                if (document != null && document.exists()) {
                    _email.value = getEmail()
                    _name.value = document.getString("name").toString()
                    _username.value = document.getString("username").toString()
                    _joinDate.value = document.getString("joinDate").toString()
                    _email.value = getEmail()
                    saveUserDataLocal()
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } ?: false
    }

    fun changeName(newName: String) {
        val user = auth.currentUser
        if (newName != _name.value && newName.isNotEmpty() && user != null) {
            _name.value = newName
            saveUserDataLocal()
            saveUserDataCloud(_name.value, _username.value, user.uid, _joinDate.value)
        }
    }

    fun saveUserDataCloud(name: String, username: String, userId: String, joinDate: String) {
        val db = FirebaseFirestore.getInstance()
        val userData = hashMapOf(
            "name" to name,
            "username" to username,
            "joinDate" to joinDate
        )
        db.collection("users")
            .document(userId)
            .collection("data")
            .document("user_data")
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("INFO:", "Saved User Data to Firebase")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun getTopPlayers(onResult: (List<Pair<String, Long>>) -> Unit, isLoading: (Boolean) -> Unit) {
        isLoading(true)
        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("data")
            .orderBy("counter", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { documents ->
                val ranking = documents.map { doc ->
                    val username = doc.getString("username") ?: "N/A"
                    val counter = doc.getLong("counter") ?: 0
                    username to counter
                }
                onResult(ranking)
                isLoading(false)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(emptyList())
                isLoading(false)
            }
    }

    fun deleteAccount(onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.collection("data").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                    userDocRef.delete()
                        .addOnSuccessListener {
                            user.delete()
                                .addOnSuccessListener {
                                    onResult(true)
                                }
                                .addOnFailureListener { e ->
                                    onResult(false)
                                    e.printStackTrace()
                                }
                        }
                        .addOnFailureListener { e ->
                            onResult(false)
                            e.printStackTrace()
                        }
                }
                .addOnFailureListener { e ->
                    onResult(false)
                    e.printStackTrace()
                }
        } else onResult(false)
    }
}

sealed class AuthState {
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data object GoogleRegisterRequired: AuthState()
}