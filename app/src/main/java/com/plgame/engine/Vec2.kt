package com.plgame.engine

import kotlin.math.sqrt

data class Vec2(val x: Float = 0f, val y: Float = 0f) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(s: Float) = Vec2(x * s, y * s)
    operator fun div(s: Float) = if (s != 0f) Vec2(x / s, y / s) else Vec2()

    fun length(): Float = sqrt(x * x + y * y)
    fun lengthSq(): Float = x * x + y * y

    fun normalize(): Vec2 {
        val len = length()
        return if (len > 0.0001f) Vec2(x / len, y / len) else Vec2()
    }

    fun dot(other: Vec2): Float = x * other.x + y * other.y

    fun distanceTo(other: Vec2): Float = (this - other).length()

    fun lerp(target: Vec2, t: Float): Vec2 = this + (target - this) * t

    fun angle(): Float = kotlin.math.atan2(y.toDouble(), x.toDouble()).toFloat()

    companion object {
        fun fromAngle(angle: Float, length: Float = 1f) = Vec2(
            (kotlin.math.cos(angle.toDouble()) * length).toFloat(),
            (kotlin.math.sin(angle.toDouble()) * length).toFloat()
        )
        fun random(minX: Float, maxX: Float, minY: Float, maxY: Float) = Vec2(
            (Math.random() * (maxX - minX) + minX).toFloat(),
            (Math.random() * (maxY - minY) + minY).toFloat()
        )
    }
}
