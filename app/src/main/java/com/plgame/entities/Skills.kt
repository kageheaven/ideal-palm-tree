package com.plgame.entities

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val damage: Float = 0f,
    val manaCost: Float = 20f,
    val cooldown: Float = 5f,
    val range: Float = 300f,
    val illusions: Int = 0,
    val duration: Float = 0f,
    val attackSpeedBonus: Float = 0f
)

val SPIRIT_LANCE = Skill(
    id = "spirit_lance",
    name = "Spirit Lance",
    description = "Бросает копьё, наносит урон и замедляет",
    damage = 80f,
    manaCost = 30f,
    cooldown = 6f,
    range = 350f
)

val DOPPELGANGER = Skill(
    id = "doppelganger",
    name = "Doppelganger",
    description = "Создаёт иллюзии и даёт кратковременную неуязвимость",
    manaCost = 50f,
    cooldown = 10f,
    illusions = 3,
    duration = 8f
)

val PHANTOM_STRIKE = Skill(
    id = "phopri",
    name = "Phantom Strike",
    description = "Рывок к врагу + бонус к скорости атаки",
    damage = 60f,
    manaCost = 40f,
    cooldown = 8f,
    range = 400f,
    attackSpeedBonus = 100f
)

val JUXTAPOSE = Skill(
    id = "juxtapose",
    name = "Juxtapose",
    description = "Ультимейт: куча иллюзий!",
    manaCost = 80f,
    cooldown = 20f,
    illusions = 6,
    duration = 12f
)
