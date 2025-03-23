package pl.karolpietrow.klikacz

data class Reward(
    val id: Int,
    val rewardType: Int, // 0 = Score, 1 = Modifier, 2 = (not yet implemented) TempHighModifier
    val rarity: Int, // 0 = Common, 1 = Rare, 2 = Legendary
//    val name: String,
//    val description: String,
    val value: Double,
)
