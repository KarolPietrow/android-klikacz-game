package pl.karolpietrow.klikacz

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import pl.karolpietrow.klikacz.ui.MainActivity
import androidx.core.content.edit

class ClickViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)

    private val _notificationsEnabled = MutableStateFlow(sharedPreferences.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _tutorialComplete = MutableStateFlow(sharedPreferences.getBoolean("tutorial_complete", false))
    val tutorialComplete: StateFlow<Boolean> = _tutorialComplete

    private val _counter = MutableStateFlow(sharedPreferences.getLong("counter", 0L))
    val counter: StateFlow<Long> = _counter

    private val _multiplier = MutableStateFlow(sharedPreferences.getLong("multiplier", 1L))
    val multiplier: StateFlow<Long> = _multiplier

    private val _autoFrequency = MutableStateFlow(sharedPreferences.getLong("auto_frequency", 2000L))
    val autoFrequency: StateFlow<Long> = _autoFrequency

    private val _autoMultiplier = MutableStateFlow(sharedPreferences.getLong("auto_multiplier", 0L))
    val autoMultiplier: StateFlow<Long> = _autoMultiplier

    private val _themeMode = MutableStateFlow(sharedPreferences.getInt("theme_mode", 0))
    val themeMode: StateFlow<Int> = _themeMode

    fun setThemeMode(id: Int) {
        when (id) {
            0 -> _themeMode.value = 0
            1 -> _themeMode.value = 1
            2 -> _themeMode.value = 2
        }
        savePointDataLocal()
    }

    private val _upgrades = MutableStateFlow(listOf(
        Upgrade(0, 0, "DoubleClick", "Mnożnik 2x", 50, 2),
        Upgrade(1, 2, "Pomoc w klikaniu! AutoKlik", "Automatyczne klinięcia! Włącz AutoKlik!", 500, 1),
        Upgrade(2, 0, "5XMOC!", "Mnożnik 5x", 1000, 5),
        Upgrade(3, 1, "Szybszy AutoKlik", "Autoklik będzie klikał co sekundę.", 3000, 1000),
        Upgrade(4,2, "Zwiększ mnożnik AutoKlika!", "Mnożnik AutoKlik x5", 5000, 5),
        Upgrade(5, 0, "Mnożnik X25", "Mnożnik x25", 7500, 25),
        Upgrade(6, 1, "Super Szybki AutoKlik!", "Częstotliwość AutoKlik 0,5/s", 10_000, 500),
        Upgrade(7, 3, "PERSONALIZACJA!", "Odblokuj personalizację gry - zmieniaj kolory i nie tylko!", 15_000, 0),
        Upgrade(8, 0, "Mnożnik X100!", "Mnożnik x100", 20_000, 100),


        Upgrade(9,2, "AutoKlik Mnożnik x10", "Mnożnik AutoKlik x10", 15_000, 10),

        Upgrade(10,1, "Jeszcze szybszy AutoKlik!", "Częstotliwość AutoKlik 0,25/s!", 20_000, 250),

        Upgrade(11, 0, "Mnożnik X1500!", "Mnożnik x1500", 100_000, 1500),

        Upgrade(14, 2, "AutoKlik Mnożnik X1000!", "Mnożnik AutoKlik x1000", 250_000, 1000),

        Upgrade(15, 0, "Mnożnik X10 000!", "Mnożnik x10000", 1_000_000, 10_000),

        Upgrade(16, 2, "AutoKlik Mnożnik X10000!", "Mnożnik AutoKlik x10000!", 10_000_000, 10_000),

        Upgrade(17, 0, "Mnożnik X100 000!", "Mnożnik x100000", 100_000_000, 100_000),

        Upgrade(999, 4, "Super Tajne Ulepszenie", "🤔🤔🤔", 999_999_999_999, 0), // 999 999 999 999 (bilion)
    ))
    val upgrades: StateFlow<List<Upgrade>> = _upgrades

    private val _isPurchaseAvailable = MutableStateFlow(false)
    val isPurchaseAvailable: StateFlow<Boolean> = _isPurchaseAvailable

    fun checkPurchases() {
        val purchasesToCheck = _upgrades.value.filter { !it.isPurchased && it.price <= counter.value }.map { it }
        if (purchasesToCheck.isNotEmpty()) {
            _isPurchaseAvailable.value = true
        } else {
            _isPurchaseAvailable.value = false
        }
    }

    fun purchaseUpgrade(context: Context, upgrade: Upgrade) {
        if (upgrade.price <= counter.value && !upgrade.isPurchased) {
            when (upgrade.upgradeType) {
                0 -> {
                    updateMultiplier(upgrade.value)
                }
                1 -> {
                    setAutoFrequency(upgrade.value)
                }
                2 -> {
                    updateAutoMultiplier(upgrade.value)
                }
                3 -> {
                    enablePersonalisation()
                    unlockAchievement(context, _achievements.value.find { it.id == 300 })
                }
                4 -> {
                    unlockAchievement(context, _achievements.value.find { it.id == 305 })
                }
            }
            _counter.value -= upgrade.price
            _upgradeCount.value++

            val currentUpgrades = _upgrades.value.toMutableList()
            val index = currentUpgrades.indexOfFirst { it.id == upgrade.id }
            if (index != -1) {
                val updatedUpgrade = upgrade.copy(isPurchased = true)
                currentUpgrades[index] = updatedUpgrade
                _upgrades.value = currentUpgrades
            }

            val scoresToCheck = _achievements.value.filter { !it.isUnlocked && it.achievementType == 2 }.map { it }
            if (scoresToCheck.isNotEmpty()) {
                if (_upgradeCount.value >= scoresToCheck[0].value) {
                    unlockAchievement(context, scoresToCheck[0])
                } else {
                    savePointDataLocal()
                    savePointDataCloud()
                }
            } else {
                savePointDataLocal()
                savePointDataCloud()
            }
            checkPurchases()
//            upgrade.isPurchased = true
        }
    }

    private val _upgradeCount = MutableStateFlow(sharedPreferences.getInt("upgrade_count", 0))
    val upgradeCount: StateFlow<Int> = _upgradeCount

    private val _achievements = MutableStateFlow(listOf(
        // Score
        Achievement(0, 0, "Klikanie czas zacząć!", "Zdobyto wynik 100.", 100, false),
        Achievement(1,0, "Pierwszy tysiąc", "Zdobyto wynik 1000.", 1000, false),
        Achievement(2,0, "10 tysięcy kliknięć!", "Zdobyto wynik 10 000!", 10000, false),
        Achievement(3,0, "100K kliknięć!", "Zdobyto wynik 100 000!", 100000, false),
        Achievement(4,0, "Pierwsza bańka 😎", "Zdobyto wynik 1 000 000!", 1000000, false),
        Achievement(5,0, "10 milionów!", "Zdobyto wynik 10 000 000!", 10000000, false),
        Achievement(6,0, "100 milionów!", "Zdobyto wynik 100 000 000!", 100000000, false),

        // Fortune Wheel
        Achievement(100,1, "Fortuna kołem się toczy", "Skorzystano po raz pierwszy z Koła Fortuny.", 1, false),
        Achievement(101,1, "Piąte koło u wozu", "Skorzystano z Koła Fortuny 5 razy!", 5, false),
        Achievement(102,1, "Czas się rozkręcić!", "Skorzystano z Koła Fortuny 20 razy!.", 20, false),
        Achievement(103,1, "Prawda w oczy kole", "Skorzystano z Koła Fortuny 50 razy!", 50, false),
        Achievement(104,1, "Dobra, już, wystarczy 😆", "Skorzystano z Koła Fortuny 100 razy. Serio ci się chciało aż tyle razy? xD", 100, false),
        Achievement(105,1, "Szukając złota znalazłem diament", "Wylosowano legendarną nagrodę w Kole Fortuny Klikacza! ", 0, false),

        // Store
        Achievement(200, 2, "Zakupy czas zacząć!", "Zakupiono po raz pierwszy ulepszenie w Sklepiku!", 1, false),
        Achievement(201, 2, "Stały klient", "Zakupiono 5 ulepszeń w Sklepiku.", 5, false),
        Achievement(202, 2, "Black Friday", "Zakupiono 10 ulepszeń w Sklepiku.", 10, false),
        Achievement(203, 2, "Jak nie wiadomo o co chodzi...", "Zakupiono 15 ulepszeń w Sklepiku.", 15, false),

        // Other
        Achievement(300,3, "Czas na zmiany", "Odblokowano personalizację Klikacza!", 0, true),
        Achievement(301,3, "Najlepszy z najlepszych", "Zdobyto pierwsze miejsce w Rankingu Klikacza!", 0, true),
        Achievement(302,3, "Nie w tą stronę xD", "Odwrócono kierunek klikania za pomocą personalizacji przycisku. PS nie da się zejść poniżej zera i nie ma za to osiągnięcia, więc się nie męcz 😆", 0, true),
//        Achievement(303,3, "Hacker 😈", "↑ ↑ ↓ ↓ ← → ← → B A", 0, true),
//        Achievement(304,3, "Przekroczenie zakresu 🤔", "Przekroczono dopuszczalny zakres licznika (Integer Overflow), powodując przekręcenie licznika do zera 😉", 0, true),
        // Do Countera używam Longa, a max wartość Longa to 9,223,372,036,854,775,807. xD
        Achievement(305,3, "Game over?", "Zdobyto ostatnie ulepszenie i ukończono grę Klikacz. Gratulacje!", 0, true),
//        Achievement(306,3, "😁", "nie wiem, jakieś super tajne osiągnięcie czy coś xd", 0, true),
    ))
    val achievements: StateFlow<List<Achievement>> = _achievements

    fun unlockAchievement(context: Context, achievement: Achievement?) {
        if (achievement != null && !achievement.isUnlocked) {
            _achievementCount.value++
            achievement.isUnlocked = true
            savePointDataLocal()
            savePointDataCloud()
            if (_notificationsEnabled.value) {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("openScreen", "profile")
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    achievement.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val builder = NotificationCompat.Builder(context, "Default")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Zdobyto osiągnięcie! 🥳")
                    .setContentText("\"${achievement.name}\"")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(achievement.id, builder)
            }
        }
    }

    private val _achievementCount = MutableStateFlow(sharedPreferences.getInt("achievement_count", 0))
    val achievementCount: StateFlow<Int> = _achievementCount

    private val _wheelCount = MutableStateFlow(sharedPreferences.getInt("wheel_count", 0))
    val wheelCount: StateFlow<Int> = _wheelCount

    fun incrementWheel(context: Context) {
        _wheelCount.value++
        val scoresToCheck = _achievements.value.filter { !it.isUnlocked && it.achievementType == 1 && it.id != 105 }.map { it }
        if (scoresToCheck.isNotEmpty()) {
            if (_wheelCount.value >= scoresToCheck[0].value) {
                unlockAchievement(context, scoresToCheck[0])
            } else {
                savePointDataLocal()
                savePointDataCloud()
            }
        } else {
            savePointDataLocal()
            savePointDataCloud()
        }
    }

    private val _personalisationEnabled: MutableStateFlow<Boolean> = MutableStateFlow(sharedPreferences.getBoolean("personalisation_enabled", false))
    val personalisationEnabled: StateFlow<Boolean> = _personalisationEnabled

    fun incrementCounter(context: Context) {
        _counter.value += _multiplier.value
        checkScoreAchievements(context)
        checkPurchases()
    }

    fun decrementCounter(context: Context) {
        if (_counter.value > 0) {
            _counter.value--
        }
        unlockAchievement(context, _achievements.value.find { it.id == 302 })
    }

    fun getCounterReward(value: Long) {
        _counter.value += value
    }

    fun autoIncrement() {
        _counter.value += (0 + _autoMultiplier.value)
    }

    fun checkScoreAchievements(context: Context) {
        val scoresToCheck = _achievements.value.filter { !it.isUnlocked && it.achievementType == 0 }.map { it }
        if (scoresToCheck.isNotEmpty()) {
            if (_counter.value >= scoresToCheck[0].value) {
                unlockAchievement(context, scoresToCheck[0])
            }
        }
    }

    fun updateMultiplier(value: Long) {
        _multiplier.value += value
    }

    private fun updateAutoMultiplier(value: Long) {
        _autoMultiplier.value += value
    }

    private fun setAutoFrequency(value: Long) {
        _autoFrequency.value = value
    }

    private fun enablePersonalisation() {
        _personalisationEnabled.value = true
    }

    fun enableNotifications() {
        _notificationsEnabled.value = true
        sharedPreferences.edit() {
            putBoolean(
                "notifications_enabled",
                _notificationsEnabled.value
            )
        }
    }

    fun disableNotifications() {
        _notificationsEnabled.value = false
        sharedPreferences.edit() {
            putBoolean(
                "notifications_enabled",
                _notificationsEnabled.value
            )
        }
    }

    fun savePointDataLocal() {
        sharedPreferences.edit { putLong("counter", _counter.value) }
        sharedPreferences.edit { putLong("multiplier", _multiplier.value) }
        sharedPreferences.edit { putLong("auto_frequency", _autoFrequency.value) }
        sharedPreferences.edit { putLong("auto_multiplier", _autoMultiplier.value) }
        sharedPreferences.edit { putBoolean("personalisation_enabled", _personalisationEnabled.value) }
        sharedPreferences.edit { putInt("upgrade_count", _upgradeCount.value) }
        sharedPreferences.edit { putInt("achievement_count", _achievementCount.value) }
        sharedPreferences.edit { putInt("wheel_count", _wheelCount.value) }
        sharedPreferences.edit { putBoolean("tutorial_complete", _tutorialComplete.value) }
        sharedPreferences.edit { putInt("theme_mode", _themeMode.value) }
        saveUpgradesLocal()
    }

    fun deletePointDataLocal() {
        sharedPreferences.edit() {
            remove("counter")
                .remove("multiplier")
                .remove("auto_frequency")
                .remove("auto_multiplier")
                .remove("personalisation_enabled")
                .remove("notifications_enabled")
                .remove("upgrade_count")
                .remove("purchased_upgrades")
                .remove("achievement_count")
                .remove("wheel_count")
                .remove("tutorial_complete")
                .remove("theme_mode")
        }

        _counter.value = 0L
        _multiplier.value = 1L
        _autoFrequency.value = 2000L
        _autoMultiplier.value = 0L
        _personalisationEnabled.value = false
        _upgradeCount.value = 0
        _achievementCount.value = 0
        _wheelCount.value = 0
        _tutorialComplete.value = false
        _notificationsEnabled.value = true
        _achievements.value.forEach { achievement ->
            achievement.isUnlocked = false
        }
        _upgrades.value.forEach { upgrade ->
            upgrade.isPurchased = false
        }
        _themeMode.value = 0
    }

    suspend fun getPointDataCloud(): Boolean {
        Log.d("KLIKACZAPP", "getPointDataCloud")
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
                    if (_counter.value < (document.getLong("counter") ?: 0)) {
                        _counter.value = document.getLong("counter") ?: 0
                        _multiplier.value = document.getLong("multiplier") ?: 1L
                        _autoFrequency.value = document.getLong("auto_frequency") ?: 2000L
                        _autoMultiplier.value = document.getLong("auto_multiplier") ?: 0
                        _personalisationEnabled.value =
                            document.getBoolean("personalisation_enabled") ?: false
                        _upgradeCount.value = (document.getLong("upgrade_count") ?: 0L).toInt()
                        _achievementCount.value = (document.getLong("achievement_count") ?: 0L).toInt()
                        _wheelCount.value = (document.getLong("wheel_count") ?: 0L).toInt()
                        _tutorialComplete.value = document.getBoolean("tutorial_complete") ?: false
                        true
                    } else {
                        // Wynik lokalny jest wyższy - nic nie rób
                        true
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } ?: false
    }

    fun savePointDataCloud() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .collection("data")
                .document("user_data")
                .update(
                    "counter", _counter.value,
                    "multiplier", _multiplier.value,
                    "auto_frequency", _autoFrequency.value,
                    "auto_multiplier", _autoMultiplier.value,
                    "personalisation_enabled", _personalisationEnabled.value,
                    "upgrade_count", _upgradeCount.value,
                    "achievement_count", _achievementCount.value,
                    "wheel_count", _wheelCount.value,
                    "tutorial_complete", _tutorialComplete.value
                )
                .addOnSuccessListener {
                    saveUpgradesCloud()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun saveUpgradesLocal() {
        val purchasedIds = _upgrades.value.filter { it.isPurchased }.map { it.id.toString() }.toSet()
        sharedPreferences.edit() { putStringSet("purchased_upgrades", purchasedIds) }

        val achievementIds = _achievements.value.filter { it.isUnlocked }. map { it.id.toString() }.toSet()
        sharedPreferences.edit() { putStringSet("unlocked_achievements", achievementIds) }
    }

    fun getUpgradesLocal() {
        Log.d("KLIKACZAPP", "getUpgradesLocal")
        val purchasedIds = sharedPreferences.getStringSet("purchased_upgrades", emptySet()) ?: emptySet()
        _upgrades.value.forEach { upgrade ->
            if (purchasedIds.contains(upgrade.id.toString())) {
                upgrade.isPurchased = true
            }
        }

        val unlockedAchievements = sharedPreferences.getStringSet("unlocked_achievements", emptySet()) ?: emptySet()
        _achievements.value.forEach { achievement ->
            if (unlockedAchievements.contains(achievement.id.toString())) {
                achievement.isUnlocked = true
            }
        }
    }

    private fun saveUpgradesCloud() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val purchasedIds = _upgrades.value.filter { it.isPurchased }.map { it.id }
            db.collection("users")
                .document(user.uid)
                .collection("data")
                .document("user_upgrades")
                .set(mapOf("purchased" to purchasedIds))
                .addOnSuccessListener {
                    val unlockedIds = _achievements.value.filter { it.isUnlocked }.map { it.id }
                    db.collection("users")
                        .document(user.uid)
                        .collection("data")
                        .document("user_achievements")
                        .set(mapOf("unlocked" to unlockedIds))
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    suspend fun getUpgradesCloud(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        val db = FirebaseFirestore.getInstance()

        return withTimeoutOrNull(5000) {
            try {
                val document = db.collection("users")
                    .document(user.uid)
                    .collection("data")
                    .document("user_upgrades")
                    .get(Source.SERVER)
                    .await()

                val purchasedIds: List<Long> =
                    (document.get("purchased") as? List<*>)?.mapNotNull {
                        when (it) {
                            is Number -> it.toLong()
                            is String -> it.toLongOrNull()
                            else -> null
                        }
                    } ?: emptyList()
                _upgrades.value.forEach { upgrade ->
                    upgrade.isPurchased = purchasedIds.contains(upgrade.id.toLong())
                }
                val document2 = db.collection("users")
                    .document(user.uid)
                    .collection("data")
                    .document("user_achievements")
                    .get()
                    .await()
                val unlockedIds: List<Long> =
                    (document2.get("unlocked") as? List<*>)?.mapNotNull {
                        when (it) {
                            is Number -> it.toLong()
                            is String -> it.toLongOrNull()
                            else -> null
                        }
                    } ?: emptyList()
                _achievements.value.forEach { achievement ->
                    achievement.isUnlocked =
                        unlockedIds.contains(achievement.id.toLong())
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } ?: false
    }

    fun completeTutorial() {
        if (!_tutorialComplete.value) {
            _tutorialComplete.value = true
        }
    }
}