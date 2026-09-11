package com.plgame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.plgame.engine.*
import com.plgame.entities.*
import com.plgame.items.*

@Composable
fun GameRenderer(
    state: GameState,
    modifier: Modifier = Modifier
) {
    val cameraOffset = Offset(
        -state.camera.x + 540f, // half screen width
        -state.camera.y + 960f  // half screen height
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Background - dark grid
        drawRect(Color(0xFF0A0A1A))

        // Grid lines
        val gridSize = 80f
        val gridColor = Color(0xFF1A1A2E)
        val startX = (cameraOffset.x % gridSize)
        val startY = (cameraOffset.y % gridSize)

        for (x in startX.toInt()..size.width.toInt() step gridSize.toInt()) {
            drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1f)
        }
        for (y in startY.toInt()..size.height.toInt() step gridSize.toInt()) {
            drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1f)
        }

        // World bounds
        drawRect(
            Color(0xFF222244),
            Offset(cameraOffset.x, cameraOffset.y),
            Size(state.worldSize.x, state.worldSize.y),
            style = Stroke(width = 4f)
        )

        // Dropped items
        state.items.forEach { dropped ->
            val sx = dropped.position.x + cameraOffset.x
            val sy = dropped.position.y + cameraOffset.y + kotlin.math.sin((state.gameTime * 3 + dropped.bobPhase).toDouble()).toFloat() * 5f
            drawCircle(
                dropped.item.rarity.color.copy(alpha = 0.6f),
                radius = 10f,
                center = Offset(sx, sy)
            )
            drawCircle(
                dropped.item.rarity.color,
                radius = 6f,
                center = Offset(sx, sy)
            )
        }

        // Enemy projectiles
        state.projectiles.filter { !it.isVisual }.forEach { proj ->
            drawCircle(
                Color(proj.color),
                radius = proj.size,
                center = Offset(proj.position.x + cameraOffset.x, proj.position.y + cameraOffset.y)
            )
        }

        // Enemies
        state.enemies.filter { it.isAlive() }.forEach { enemy ->
            val sx = enemy.position.x + cameraOffset.x
            val sy = enemy.position.y + cameraOffset.y
            val color = if (enemy.hitFlashTimer > 0f) Color.White else Color(enemy.color)

            // Shadow
            drawOval(
                Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(sx - enemy.size * 0.6f, sy + enemy.size * 0.6f),
                size = Size(enemy.size * 1.2f, enemy.size * 0.4f)
            )

            // Body
            drawCircle(color, radius = enemy.size, center = Offset(sx, sy))
            drawCircle(color.copy(alpha = 0.5f), radius = enemy.size * 1.1f, center = Offset(sx, sy))

            // HP bar
            val hpRatio = enemy.hp / enemy.maxHp
            val barWidth = enemy.size * 2f
            drawRect(Color(0xFF333333), Offset(sx - barWidth / 2, sy - enemy.size - 10f), Size(barWidth, 4f))
            drawRect(
                Color(0xFFCC0000),
                Offset(sx - barWidth / 2, sy - enemy.size - 10f),
                Size(barWidth * hpRatio, 4f)
            )

            // Boss indicator
            if (enemy.creepType == CreepType.BOSS) {
                drawCircle(Color.Red.copy(alpha = 0.3f), radius = enemy.size * 1.5f, center = Offset(sx, sy))
            }
        }

        // Illusions
        state.player.illusions.forEach { illusion ->
            val sx = illusion.position.x + cameraOffset.x
            val sy = illusion.position.y + cameraOffset.y
            val alpha = (illusion.lifetime / 12f).coerceIn(0.2f, 0.7f)

            drawCircle(
                Color(0xFF00AACC).copy(alpha = alpha),
                radius = 16f,
                center = Offset(sx, sy)
            )
            drawCircle(
                Color(0xFF00DDFF).copy(alpha = alpha * 0.5f),
                radius = 20f,
                center = Offset(sx, sy)
            )
        }

        // Player
        val px = state.player.position.x + cameraOffset.x
        val py = state.player.position.y + cameraOffset.y
        val playerColor = if (state.player.hitFlashTimer > 0f) Color.White
        else if (state.player.invulnTimer > 0f) Color(0xFF00FFAA)
        else Color(0xFF00CCFF)

        // Player shadow
        drawOval(
            Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(px - 20f, py + 14f),
            size = Size(40f, 12f)
        )

        // Player body
        drawCircle(playerColor, radius = 20f, center = Offset(px, py))
        drawCircle(Color(0xFFAAEEFF), radius = 16f, center = Offset(px, py))

        // Spear direction indicator
        val spearEnd = Offset(
            px + kotlin.math.cos(state.player.facingAngle.toDouble()).toFloat() * 35f,
            py + kotlin.math.sin(state.player.facingAngle.toDouble()).toFloat() * 35f
        )
        drawLine(Color(0xFF00FFFF), Offset(px, py), spearEnd, strokeWidth = 3f)
        drawCircle(Color(0xFF00FFFF), radius = 4f, center = spearEnd)

        // Visual projectiles (from skills)
        state.projectiles.filter { it.isVisual }.forEach { proj ->
            drawLine(
                Color(proj.color),
                Offset(proj.position.x + cameraOffset.x, proj.position.y + cameraOffset.y),
                Offset(
                    proj.position.x + cameraOffset.x + proj.velocity.x * 0.05f,
                    proj.position.y + cameraOffset.y + proj.velocity.y * 0.05f
                ),
                strokeWidth = proj.size,
                cap = StrokeCap.Round
            )
        }

        // Attack range indicator (subtle)
        if (state.isAttacking) {
            drawCircle(
                Color.White.copy(alpha = 0.1f),
                radius = state.player.attackRange,
                center = Offset(px, py),
                style = Stroke(width = 1f)
            )
        }
    }
}
