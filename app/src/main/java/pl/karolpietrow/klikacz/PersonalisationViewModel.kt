package pl.karolpietrow.klikacz

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.edit

class PersonalisationViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)

    private val _topText = MutableStateFlow(sharedPreferences.getString("top_text", "Klikacz")?: "Klikacz")
    val topText: StateFlow<String> = _topText

    private val _buttonText = MutableStateFlow(sharedPreferences.getString("button_text", "KLIK++")?: "KLIK++")
    val buttonText: StateFlow<String> = _buttonText

    private val _bottomText = MutableStateFlow(sharedPreferences.getString("bottom_text", "🥳")?: "🥳")
    val bottomText: StateFlow<String> = _bottomText

    fun saveDataLocal() {
        sharedPreferences.edit() { putString("top_text", _topText.value) }
        sharedPreferences.edit() { putString("button_text", _buttonText.value) }
        sharedPreferences.edit() { putString("bottom_text", _bottomText.value) }
    }

    fun deleteDataLocal() {
        sharedPreferences.edit() {
            remove("top_text")
                .remove("button_text")
                .remove("bottom_text")
        }

        _topText.value = "Klikacz"
        _buttonText.value = "KLIK++"
        _bottomText.value = "🥳"
    }

    fun updateTopText(newText: String) {
        _topText.value = newText
        saveDataLocal()
    }
    fun updateButtonText(newText: String) {
        _buttonText.value = newText
        saveDataLocal()
    }
    fun updateBottomText(newText: String) {
        _bottomText.value = newText
        saveDataLocal()
    }
}