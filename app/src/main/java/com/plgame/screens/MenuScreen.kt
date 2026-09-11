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

@Composable
fun MenuScreen(
    onStartGame: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1A))
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                "⚔️",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                "PHANTOM LANCER",
                color = Color(0xFF00CCFF),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "ADVENTURE",
                color = Color(0xFF00AADD),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Бродилка по волнам крипов",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp
            )
            Text(
                "Собирай амулеты",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp
            )
            Text(
                "Крафти мусор вместо нормальных шмоток",
                color = Color(0xFFFF6666),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(48.dp))

            // Start button
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF0066AA))
                    .border(2.dp, Color(0xFF00CCFF), RoundedCornerShape(28.dp))
                    .clickable { onStartGame() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "НАЧАТЬ ИГРУ",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(Color(0xFF1A1A2E), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "УПРАВЛЕНИЕ:",
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text("👆 Свайп = движение", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                Text("👆 Тап = автоатака", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                Text("👆👆 Двойной тап = крафт", color = Color(0xFFFFAA00), fontSize = 13.sp)
                Text("🔮 Кнопки внизу = скиллы", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "inspired by Dota 2 | v1.0",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp
            )
        }
    }
}
