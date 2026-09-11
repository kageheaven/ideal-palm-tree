package com.plgame.entities

import com.plgame.engine.Vec2

enum class CreepType(
    val displayName: String,
    val baseHp: Float,
    val baseDamage: Float,
    val speed: Float,
    val attackType: AttackType,
    val attackSpeed: Float,
    val attackRange: Float,
    val hitRadius: Float,
    val scoreValue: Int,
    val goldDrop: Int,
    val color: Long,
    val size: Float
) {
    MELEE_CREEP("Крип melee", 120f, 15f, 150f, AttackType.MELEE, 1.0f, 40f, 18f, 10, 5, 0xFF888888, 16f),
    RANGED_CREEP("Крип ranged", 80f, 20f, 130f, AttackType.RANGED, 0.7f, 300f, 14f, 15, 7, 0xFF884488, 14f),
    FAST_CREEP("Быстрый крип", 60f, 10f, 300f, AttackType.MELEE, 1.5f, 40f, 12f, 12, 6, 0xFFFFAA44, 12f),
    TANK_CREEP("Танк крип", 300f, 25f, 100f, AttackType.MELEE, 0.6f, 45f, 22f, 25, 12, 0xFF448844, 20f),
    MAGE_CREEP("Маг крип", 100f, 35f, 120f, AttackType.RANGED, 0.5f, 350f, 14f, 20, 10, 0xFF4444FF, 14f),
    BOSS("БОСС", 1500f, 50f, 180f, AttackType.RANGED, 0.8f, 400f, 30f, 100, 50, 0xFFFF0000, 32f)
}

enum class AttackType {
    MELEE,
    RANGED
}

data class Enemy(
    var position: Vec2,
    var hp: Float,
    var maxHp: Float,
    val creepType: CreepType,
    val baseDamage: Float,
    var damage: Float,
    val speed: Float,
    val attackType: AttackType,
    var attackSpeed: Float,
    val attackRange: Float,
    val hitRadius: Float,
    val scoreValue: Int,
    val goldDrop: Int,
    val color: Long,
    val size: Float,

    // State
    var deathTimer: Float = 0f,
    var hitFlashTimer: Float = 0f,
    var attackCooldown: Float = 0f,
    var slowTimer: Float = 0f,
    var slowFactor: Float = 1f,
    var waveScale: Float = 1f,
    val projectileColor: Long = 0xFFFF4444
) {
    companion object {
        fun create(type: CreepType, position: Vec2, wave: Int): Enemy {
            val scale = 1f + (wave - 1) * 0.15f
            return Enemy(
                position = position,
                hp = type.baseHp * scale,
                maxHp = type.baseHp * scale,
                creepType = type,
                baseDamage = type.baseDamage,
                damage = type.baseDamage * scale,
                speed = type.speed,
                attackType = type.attackType,
                attackSpeed = type.attackSpeed,
                attackRange = type.attackRange,
                hitRadius = type.hitRadius,
                scoreValue = type.scoreValue + wave * 5,
                goldDrop = type.goldDrop + wave,
                color = type.color,
                size = type.size * scale,
                waveScale = scale
            )
        }
    }

    fun isAlive(): Boolean = hp > 0f && deathTimer <= 0f

    fun takeDamage(amount: Float) {
        hp -= amount
        hitFlashTimer = 0.1f
        if (hp <= 0f) {
            deathTimer = 0.01f
        }
    }

    fun slow(duration: Float, factor: Float) {
        slowTimer = duration
        slowFactor = factor
    }

    fun update(dt: Float, player: com.plgame.entities.Player, allEnemies: List<Enemy>) {
        if (!isAlive()) {
            deathTimer += dt
            return
        }

        hitFlashTimer = (hitFlashTimer - dt).coerceAtLeast(0f)
        attackCooldown = (attackCooldown - dt).coerceAtLeast(0f)
        slowTimer = (slowTimer - dt).coerceAtLeast(0f)

        val effectiveSpeed = if (slowTimer > 0f) speed * slowFactor else speed

        // Simple AI: move toward player
        val toPlayer = player.position - position
        val distToPlayer = toPlayer.length()

        when {
            distToPlayer > attackRange -> {
                // Chase
                val dir = toPlayer.normalize()
                position = position + dir * effectiveSpeed * dt
            }
            attackCooldown <= 0f -> {
                // Attack will be handled by collision check
                attackCooldown = 1f / attackSpeed
            }
            else -> {
                // Strafe slightly
                val perpAngle = toPlayer.angle() + (Math.PI.toFloat() / 2f) * (if ((position.x + position.y) % 2 > 1) 1f else -1f)
                position = position + Vec2.fromAngle(perpAngle, effectiveSpeed * 0.3f * dt)
            }
        }
    }
}
