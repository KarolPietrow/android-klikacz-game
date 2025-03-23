package pl.karolpietrow.klikacz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImageViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _image = MutableStateFlow("null")
    val image: StateFlow<String> = _image

    fun saveBase64(base64: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val data = hashMapOf("imageBase64" to base64)
                db.collection("users")
                    .document(user.uid)
                    .collection("data")
                    .document("image")
                    .set(data)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error saving image", e) }
            }
        }
    }

    fun loadBase64() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .collection("data")
                .document("image")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val base64 = document.getString("imageBase64")
                        if (base64 != null) {
                            _image.value = base64
                        } else {
                            _image.value = ""
                        }
                    } else {
                        _image.value = ""
                    }
                }
                .addOnFailureListener {
                    _image.value = ""
                }
        } else {
            _image.value = ""
        }
    }

    fun deleteImageFromCloud() {
        val user = auth.currentUser
        if (user != null) {
            val data = hashMapOf("imageBase64" to "")
            db.collection("users")
                .document(user.uid)
                .collection("data")
                .document("image")
                .set(data)
                .addOnSuccessListener {
                    loadBase64()
                }
        }
        clearLocalData()
    }

    fun clearLocalData() {
        _image.value = ""
    }
}