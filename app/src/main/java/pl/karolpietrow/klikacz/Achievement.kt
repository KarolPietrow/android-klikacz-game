package pl.karolpietrow.klikacz

data class Achievement(
    val id: Int,
    val achievementType: Int, // 0 = Score, 1 = Fortune Wheel, 2 = Upgrades, 3 = Other
    val name: String,
    val description: String,
    val value: Long,
    val isSecret: Boolean,
    var isUnlocked: Boolean = false
)
