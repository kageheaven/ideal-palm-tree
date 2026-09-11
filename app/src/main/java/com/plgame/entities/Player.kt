package com.plgame.entities

import com.plgame.engine.Vec2
import com.plgame.items.*

data class Player(
    var position: Vec2 = Vec2(1000f, 1000f),
    var facingAngle: Float = 0f,
    var isMoving: Boolean = false,

    // Stats
    var maxHp: Float = 500f,
    var hp: Float = 500f,
    var maxMana: Float = 200f,
    var mana: Float = 200f,
    var manaRegen: Float = 3f,
    var damage: Float = 45f,
    var armor: Float = 2f,
    var attackRange: Float = 120f,
    var moveSpeed: Float = 280f,

    // Attack
    var attackCooldown: Float = 0f,
    var baseAttackSpeed: Float = 1.0f,
    var attackSpeedBonus: Float = 0f,

    // Skills
    val skills: List<Skill> = listOf(SPIRIT_LANCE, DOPPELGANGER, PHANTOM_STRIKE, JUXTAPOSE),
    var skillCooldowns: MutableList<Float> = mutableListOf(0f, 0f, 0f, 0f),

    // Illusions
    val illusions: MutableList<Illusion> = mutableListOf(),
    val illusionSpeed: Float = 250f,
    val illusionDamage: Float = 15f,

    // Equipment
    val equipment: MutableList<GameItem> = mutableListOf(),
    var bonusDamage: Float = 0f,
    var bonusHp: Float = 0f,
    var bonusArmor: Float = 0f,
    var bonusMoveSpeed: Float = 0f,
    var bonusAttackSpeed: Float = 0f,

    // State
    var invulnTimer: Float = 0f,
    var hitFlashTimer: Float = 0f
) {
    fun getAttackSpeed(): Float = baseAttackSpeed + attackSpeedBonus / 100f + bonusAttackSpeed / 100f
    fun getMovementSpeed(): Float = moveSpeed + bonusMoveSpeed
    fun getTotalDamage(): Float = damage + bonusDamage

    fun takeDamage(rawDamage: Float) {
        if (invulnTimer > 0f) return
        val effectiveDamage = (rawDamage - armor * 0.05f * rawDamage).coerceAtLeast(rawDamage * 0.1f)
        hp -= effectiveDamage
        hitFlashTimer = 0.15f
    }

    fun heal(amount: Float) {
        hp = (hp + amount).coerceAtMost(maxHp + bonusHp)
    }

    fun equipItem(item: GameItem) {
        equipment.add(item)
        when (item) {
            is GameItem.StrengthAmulet -> { bonusHp += item.hpBonus; hp += item.hpBonus }
            is GameItem.AgilityAmulet -> { bonusDamage += item.damageBonus; bonusAttackSpeed += item.attackSpeedBonus }
            is GameItem.IntelligenceAmulet -> { maxMana += item.manaBonus; mana += item.manaBonus; manaRegen += item.manaRegenBonus }
            is GameItem.BootsOfSpeed -> { bonusMoveSpeed += item.speedBonus }
            is GameItem.PoorMansShield -> { armor += item.armorBonus; bonusArmor += item.armorBonus }
            is GameItem.ClarityRing -> { manaRegen += item.manaRegenBonus }
            is GameItem.CritAmulet -> { bonusDamage += item.damageBonus }
            is GameItem.CursedAmulet -> {
                bonusDamage += item.damageBonus
                bonusHp -= item.hpPenalty
                hp = hp.coerceAtMost(maxHp + bonusHp)
            }
            is GameItem.MidasAmulet -> { /* Gold bonus handled elsewhere */ }
            else -> {}
        }
    }

    fun unequipItem(index: Int) {
        if (index !in equipment.indices) return
        val item = equipment.removeAt(index)
        when (item) {
            is GameItem.StrengthAmulet -> { bonusHp -= item.hpBonus; hp = hp.coerceAtMost(maxHp + bonusHp) }
            is GameItem.AgilityAmulet -> { bonusDamage -= item.damageBonus; bonusAttackSpeed -= item.attackSpeedBonus }
            is GameItem.IntelligenceAmulet -> { maxMana -= item.manaBonus; mana = mana.coerceAtMost(maxMana); manaRegen -= item.manaRegenBonus }
            is GameItem.BootsOfSpeed -> { bonusMoveSpeed -= item.speedBonus }
            is GameItem.PoorMansShield -> { armor -= item.armorBonus; bonusArmor -= item.armorBonus }
            is GameItem.ClarityRing -> { manaRegen -= item.manaRegenBonus }
            is GameItem.CritAmulet -> { bonusDamage -= item.damageBonus }
            is GameItem.CursedAmulet -> { bonusDamage -= item.damageBonus; bonusHp += item.hpPenalty }
            else -> {}
        }
    }
}

data class Illusion(
    var position: Vec2,
    var facingAngle: Float,
    val damage: Float,
    var hp: Float,
    var lifetime: Float
)
