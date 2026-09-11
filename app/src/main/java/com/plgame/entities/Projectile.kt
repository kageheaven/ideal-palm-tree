package com.plgame.entities

import com.plgame.engine.Vec2

data class Projectile(
    var position: Vec2,
    val velocity: Vec2,
    val damage: Float,
    var lifetime: Float,
    val color: Long,
    val size: Float,
    val isVisual: Boolean = false
) {
    fun isExpired(): Boolean = lifetime <= 0f
}
