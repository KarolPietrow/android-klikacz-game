package pl.karolpietrow.klikacz

data class Upgrade(
    val id: Int,
    val upgradeType: Int, // 0 = multiplier, 1 = autoFrequency, 2 = autoMultiplier, 3 = personalisation, 4 = end
    val name: String,
    val description: String,
    val price: Long,
    val value: Long,
    var isPurchased: Boolean = false
)
