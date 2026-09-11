package com.plgame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.plgame.engine.GameState
import com.plgame.entities.Player

@Composable
fun GameHUD(
    state: GameState,
    onSkillUse: (Int) -> Unit,
    onAttackToggle: (Boolean) -> Unit,
    onCraftOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Top bar - HP, Mana, Wave, Score
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // HP Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("❤️", fontSize = 16.sp)
                Spacer(Modifier.width(4.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF333333))
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(state.player.hp / (state.player.maxHp + state.player.bonusHp))
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFCC0000))
                    )
                    Text(
                        "${state.player.hp.toInt()}/${(state.player.maxHp + state.player.bonusHp).toInt()}",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Mana Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💎", fontSize = 16.sp)
                Spacer(Modifier.width(4.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF333333))
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(state.player.mana / state.player.maxMana)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF4444FF))
                    )
                    Text(
                        "${state.player.mana.toInt()}/${state.player.maxMana.toInt()}",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Wave, Score, Gold
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "🌊 Волна: ${state.currentWave}/${state.maxWaves}",
                    color = Color(0xFFAAAAFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "⭐ ${state.score}",
                    color = Color(0xFFFFFF44),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "🪙 ${state.gold}",
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (state.craftTokens > 0) {
                    Text(
                        "🔨 ×${state.craftTokens}",
                        color = Color(0xFFFFAA00),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Notifications
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.notifications.forEach { notif ->
                val alpha = notif.alpha(state.gameTime).coerceIn(0f, 1f)
                val offsetY = notif.offsetY(state.gameTime)
                if (alpha > 0f) {
                    Text(
                        notif.text,
                        color = Color(notif.color).copy(alpha = alpha),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = offsetY.dp)
                    )
                }
            }
        }

        // Bottom - Skills
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.player.skills.forEachIndexed { index, skill ->
                val isOnCooldown = state.player.skillCooldowns[index] > 0f
                val noMana = state.player.mana < skill.manaCost
                val skillColor = when {
                    isOnCooldown -> Color(0xFF333333)
                    noMana -> Color(0xFF663333)
                    else -> Color(0xFF0066AA)
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(skillColor)
                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { if (!isOnCooldown && !noMana) onSkillUse(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            when (skill.id) {
                                "spirit_lance" -> "🔱"
                                "doppelganger" -> "👤"
                                "phopri" -> "⚔️"
                                "juxtapose" -> "🌀"
                                else -> "?"
                            },
                            fontSize = 20.sp
                        )
                        Text(
                            if (isOnCooldown) "${state.player.skillCooldowns[index].toInt()}s"
                            else skill.manaCost.toInt().toString(),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Craft button (if tokens available)
        if (state.craftTokens > 0 && state.isWaveActive.not()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp, 80.dp, 16.dp, 16.dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFAA00))
                    .border(3.dp, Color(0xFFFFD700), CircleShape)
                    .clickable { onCraftOpen() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔨", fontSize = 24.sp)
                    Text("КРАФТ", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Attack indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp, 80.dp, 16.dp, 16.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(if (state.isAttacking) Color(0xFFCC0000) else Color(0xFF444444))
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable { onAttackToggle(!state.isAttacking) },
            contentAlignment = Alignment.Center
        ) {
            Text("⚔️", fontSize = 28.sp)
        }

        // Equipment count
        if (state.player.equipment.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color(0xFF222244).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    "📦 ${state.player.equipment.size} шмоток",
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp
                )
            }
        }
    }
}
