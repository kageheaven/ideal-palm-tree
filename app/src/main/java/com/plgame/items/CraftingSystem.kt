package com.plgame.items

import com.plgame.engine.GameState
import com.plgame.engine.CraftOption

data class CraftRecipe(
    val name: String,
    val description: String,
    val materials: Map<String, Int>,
    val result: GameItem,
    val craftChance: Float = 1.0f, // Chance to succeed
    val trollChance: Float = 0.0f  // Chance to give trash instead
)

class CraftingSystem {
    private var craftedCount = 0

    fun reset() {
        craftedCount = 0
    }

    // Pool of recipes - some good, some trash, just like Dota 2
    private val allRecipes: List<CraftRecipe> = listOf(
        // Good items
        CraftRecipe("Копать за Силу", "Создаёт Амулет Силы", mapOf("token" to 1), GameItem.StrengthAmulet(100f, ItemRarity.UNCOMMON), 0.8f),
        CraftRecipe("Ковать за Ловкость", "Создаёт Амулет Ловкости", mapOf("token" to 1), GameItem.AgilityAmulet(12f, ItemRarity.UNCOMMON), 0.75f),
        CraftRecipe("Сапоги", "Создаёт Сапоги Скорости", mapOf("token" to 1), GameItem.BootsOfSpeed(40f, ItemRarity.UNCOMMON), 0.7f),
        CraftRecipe("Щит Бедняка", "Создаёт щит", mapOf("token" to 1), GameItem.PoorMansShield(3f, ItemRarity.UNCOMMON), 0.8f),

        // Okay items
        CraftRecipe("Кольцо Ясности", "Создаёт Кольцо Ясности", mapOf("token" to 1), GameItem.ClarityRing(3f, ItemRarity.UNCOMMON), 0.6f),
        CraftRecipe("Крит-амулет", "Создаёт Критический Амулет", mapOf("token" to 1), GameItem.CritAmulet(15f, ItemRarity.RARE), 0.5f),

        // Trash items (hilariously bad)
        CraftRecipe("??? Проклятый Амулет ???", "Создаёт проклятие", mapOf("token" to 1), GameItem.CursedAmulet(20f, 100f, ItemRarity.CURSED), 1.0f, 0.4f),
        CraftRecipe("Амулет Интеллекта", "Зачем?", mapOf("token" to 1), GameItem.IntelligenceAmulet(50f, 2f, ItemRarity.COMMON), 0.9f),
        CraftRecipe("Зелье HP", "Просто зелье", mapOf("token" to 1), GameItem.HealthPotion(100f), 1.0f, 0.2f),
        CraftRecipe("Зелье Маны", "Лансеру не нужна мана", mapOf("token" to 1), GameItem.ManaPotion(80f), 1.0f, 0.3f),

        // Legendary (ultra rare troll)
        CraftRecipe("👑 АМУЛЕТ МИДАСА 👑", "Легенда...", mapOf("token" to 3), GameItem.MidasAmulet(1.5f, ItemRarity.LEGENDARY), 0.05f, 0.0f),
    )

    fun generateOptions(count: Int, state: GameState): List<CraftOption> {
        val options = mutableListOf<CraftOption>()
        val available = allRecipes.toMutableList()

        repeat(count) {
            if (available.isEmpty()) return@repeat
            val recipe = available.random()
            available.remove(recipe)

            val result = if (Math.random() < recipe.trollChance) {
                // TROLL: Give trash instead!
                generateTrollResult(state.currentWave)
            } else {
                recipe.result
            }

            options.add(CraftOption(recipe, result))
        }

        return options
    }

    private fun generateTrollResult(wave: Int): GameItem {
        val trolls = listOf(
            GameItem.HealthPotion(50f),  // Маленькая горелка
            GameItem.ManaPotion(30f),    // Маленькое зелье маны
            GameItem.IntelligenceAmulet(20f, 1f, ItemRarity.COMMON), // Бесполезно для PL
            GameItem.PoorMansShield(1f, ItemRarity.COMMON), // Ещё хуже
            GameItem.StrengthAmulet(30f, ItemRarity.COMMON) // Маленький амулет
        )
        return trolls.random()
    }

    fun applyCraft(option: CraftOption, state: GameState) {
        craftedCount++

        when (val item = option.result) {
            is GameItem.HealthPotion,
            is GameItem.ManaPotion,
            is GameItem.GoldCoin -> {
                // Consumables go to inventory directly
                state.player.equipItem(item)
                state.addNotification("Получено: ${item.name}", item.rarity.color.hashCode().toLong())
            }
            else -> {
                // Equipment gets equipped
                state.player.equipItem(item)
                state.addNotification("Экипировано: ${item.name}", item.rarity.color.hashCode().toLong())
            }
        }
    }

    fun randomDrop(wave: Int): GameItem {
        return when {
            Math.random() < 0.15f -> GameItem.HealthPotion(80f + wave * 10f)
            Math.random() < 0.1f -> GameItem.ManaPotion(60f + wave * 5f)
            Math.random() < 0.05f -> GameItem.CritAmulet(10f + wave * 2f, ItemRarity.RARE)
            Math.random() < 0.01f -> GameItem.CursedAmulet(15f + wave * 3f, 80f, ItemRarity.CURSED) // LMAO
            Math.random() < 0.3f -> GameItem.StrengthAmulet(50f + wave * 10f)
            else -> GameItem.AgilityAmulet(5f + wave, 5f + wave)
        }
    }
}
