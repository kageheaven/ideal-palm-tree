package com.plgame.items

import androidx.compose.ui.graphics.Color

sealed class GameItem(
    val name: String,
    val description: String,
    val rarity: ItemRarity,
    val icon: String,
    val type: ItemType
) {
    // Amulets (активные)
    class StrengthAmulet(
        val hpBonus: Float = 100f,
        rarity: ItemRarity = ItemRarity.COMMON
    ) : GameItem(
        name = "Амулет Силы +${hpBonus.toInt()} HP",
        description = "Даёт ${hpBonus.toInt()} максимального здоровья. Абсолютный мусор.",
        rarity = rarity,
        icon = "💪",
        type = ItemType.AMULET
    )

    class AgilityAmulet(
        val damageBonus: Float = 10f,
        val attackSpeedBonus: Float = 10f,
        rarity: ItemRarity = ItemRarity.COMMON
    ) : GameItem(
        name = "Амулет Ловкости +${damageBonus.toInt()} DMG",
        description = "+${damageBonus.toInt()} урона и +${attackSpeedBonus.toInt()} к скорости атаки. Нормально, но не впечатляет.",
        rarity = rarity,
        icon = "⚡",
        type = ItemType.AMULET
    )

    class IntelligenceAmulet(
        val manaBonus: Float = 50f,
        val manaRegenBonus: Float = 2f,
        rarity: ItemRarity = ItemRarity.COMMON
    ) : GameItem(
        name = "Амулет Интеллекта +${manaBonus.toInt()} MP",
        description = "+${manaBonus.toInt()} маны и +${manaRegenBonus} к регенерации. Для магов, а не для Лансера.",
        rarity = rarity,
        icon = "🧠",
        type = ItemType.AMULET
    )

    // Браслеты (пассивные)
    class BootsOfSpeed(
        val speedBonus: Float = 40f,
        rarity: ItemRarity = ItemRarity.UNCOMMON
    ) : GameItem(
        name = "Сапоги Скорости +${speedBonus.toInt()} MS",
        description = "+${speedBonus.toInt()} к скорости передвижения. Единственная полезная штука.",
        rarity = rarity,
        icon = "👢",
        type = ItemType.BRACELET
    )

    class PoorMansShield(
        val armorBonus: Float = 3f,
        rarity: ItemRarity = ItemRarity.COMMON
    ) : GameItem(
        name = "Щит Бедняка +${armorBonus.toInt()} Armor",
        description = "+${armorBonus.toInt()} к броне. Буквально щит из картона.",
        rarity = rarity,
        icon = "🛡️",
        type = ItemType.BRACELET
    )

    class ClarityRing(
        val manaRegenBonus: Float = 3f,
        rarity: ItemRarity = ItemRarity.UNCOMMON
    ) : GameItem(
        name = "Кольцо Ясности +${manaRegenBonus} MRegen",
        description = "+${manaRegenBonus} к регенерации маны. Лансер не использует ману.",
        rarity = rarity,
        icon = "💍",
        type = ItemType.BRACELET
    )

    class CritAmulet(
        val damageBonus: Float = 15f,
        rarity: ItemRarity = ItemRarity.RARE
    ) : GameItem(
        name = "Критический Амулет +${damageBonus.toInt()} DMG",
        description = "+${damageBonus.toInt()} урона. Наконец-то что-то нормальное!",
        rarity = rarity,
        icon = "💥",
        type = ItemType.AMULET
    )

    // Мусорные предметы (нарочно плохие)
    class CursedAmulet(
        val damageBonus: Float = 20f,
        val hpPenalty: Float = 100f,
        rarity: ItemRarity = ItemRarity.RARE
    ) : GameItem(
        name = "Проклятый Амулет",
        description = "+${damageBonus.toInt()} урона, но -${hpPenalty.toInt()} HP. Классика Dota.",
        rarity = rarity,
        icon = "☠️",
        type = ItemType.AMULET
    )

    class MidasAmulet(
        val goldMultiplier: Float = 1.5f,
        rarity: ItemRarity = ItemRarity.LEGENDARY
    ) : GameItem(
        name = "Амулет Мидаса",
        description = "x${goldMultiplier} к золоту. Вероятность выпадения: 0.001%.",
        rarity = rarity,
        icon = "👑",
        type = ItemType.AMULET
    )

    // Consumables
    class HealthPotion(val healAmount: Float = 100f) : GameItem(
        name = "Зелье здоровья +${healAmount.toInt()}",
        description = "Восстанавливает ${healAmount.toInt()} HP. Нормальная штука.",
        rarity = ItemRarity.COMMON,
        icon = "❤️",
        type = ItemType.CONSUMABLE
    )

    class ManaPotion(val manaAmount: Float = 80f) : GameItem(
        name = "Зелье маны +${manaAmount.toInt()}",
        description = "Восстанавливает ${manaAmount.toInt()} MP. Зачем это Лансеру?",
        rarity = ItemRarity.COMMON,
        icon = "💙",
        type = ItemType.CONSUMABLE
    )

    class GoldCoin(val amount: Int = 10) : GameItem(
        name = "Золото",
        description = "+$amount золота",
        rarity = ItemRarity.COMMON,
        icon = "🪙",
        type = ItemType.CONSUMABLE
    )
}

enum class ItemRarity(val displayName: String, val color: Color) {
    COMMON("Обычный", Color(0xFF888888)),
    UNCOMMON("Необычный", Color(0xFF44FF44)),
    RARE("Редкий", Color(0xFF4444FF)),
    EPIC("Эпический", Color(0xFFAA44FF)),
    LEGENDARY("Легендарный", Color(0xFFFFAA00)),
    CURSED("Проклятый", Color(0xFFFF4444))
}

enum class ItemType(val displayName: String) {
    AMULET("Амулет"),
    BRACELET("Браслет"),
    CONSUMABLE("Расходник")
}
