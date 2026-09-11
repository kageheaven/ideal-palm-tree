package com.plgame.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plgame.engine.GamePhase
import com.plgame.engine.GameState

@Composable
fun GameOverScreen(
    state: GameState,
    onRestart: () -> Unit
) {
    val isVictory = state.phase == GamePhase.VICTORY

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC0A0A1A))
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isVictory) "🏆" else "💀",
                fontSize = 80.sp
            )
            Text(
                if (isVictory) "ПОБЕДА!" else "ПОРАЖЕНИЕ",
                color = if (isVictory) Color(0xFF44FF44) else Color(0xFFFF4444),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Очки: ${state.score}",
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                "Золото: ${state.gold}",
                color = Color(0xFFFFD700),
                fontSize = 20.sp
            )
            Text(
                "Волна: ${state.currentWave}/${state.maxWaves}",
                color = Color(0xFFAAAAFF),
                fontSize = 18.sp
            )
            Text(
                "Шмоток: ${state.player.equipment.size}",
                color = Color(0xFFAAAAAA),
                fontSize = 16.sp
            )
            Text(
                "Время: ${state.gameTime.toInt()}с",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF0066AA))
                    .border(2.dp, Color(0xFF00CCFF), RoundedCornerShape(28.dp))
                    .clickable { onRestart() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ЗАНОВО",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
