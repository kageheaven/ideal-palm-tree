package com.plgame.engine

import com.plgame.entities.*
import com.plgame.items.*

enum class GamePhase {
    MENU,
    PLAYING,
    PAUSED,
    CRAFTING,
    GAME_OVER,
    VICTORY
}

data class GameState(
    var phase: GamePhase = GamePhase.MENU,
    var player: Player = Player(),
    var enemies: MutableList<Enemy> = mutableListOf(),
    var projectiles: MutableList<Projectile> = mutableListOf(),
    var items: MutableList<DroppedItem> = mutableListOf(),
    var camera: Vec2 = Vec2(),
    var worldSize: Vec2 = Vec2(2000f, 2000f),
    var currentWave: Int = 0,
    var maxWaves: Int = 10,
    var waveTimer: Float = 0f,
    var waveCooldown: Float = 5f,
    var isWaveActive: Boolean = false,
    var score: Int = 0,
    var gameTime: Float = 0f,
    var inputDirection: Vec2 = Vec2(),
    var isAttacking: Boolean = false,
    var craftTokens: Int = 0,
    var craftOptions: List<CraftOption> = emptyList(),
    var notifications: MutableList<GameNotification> = mutableListOf(),
    var gold: Int = 0
) {
    fun addNotification(text: String, color: Long = 0xFFFFFFFF) {
        notifications.add(GameNotification(text, gameTime, color))
        if (notifications.size > 5) notifications.removeAt(0)
    }

    fun removeDeadEnemies() {
        enemies.removeAll { !it.isAlive() }
        enemies.removeAll { it.deathTimer > 2f }
    }

    fun removeDeadProjectiles() {
        projectiles.removeAll { it.isExpired() }
    }
}

data class GameNotification(
    val text: String,
    val spawnTime: Float,
    val color: Long = 0xFFFFFFFF
) {
    fun alpha(currentTime: Float): Float {
        val age = currentTime - spawnTime
        return when {
            age < 0f -> 0f
            age < 2f -> 1f
            age < 3f -> 1f - (age - 2f)
            else -> 0f
        }
    }
    fun offsetY(currentTime: Float): Float {
        return -(currentTime - spawnTime) * 30f
    }
}

data class DroppedItem(
    val position: Vec2,
    val item: GameItem,
    var lifetime: Float = 30f,
    var bobPhase: Float = (Math.random() * Math.PI * 2).toFloat()
) {
    fun isExpired(): Boolean = lifetime <= 0f
}

data class CraftOption(
    val recipe: CraftRecipe,
    val result: GameItem
)
