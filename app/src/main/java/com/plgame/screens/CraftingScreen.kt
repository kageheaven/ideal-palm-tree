package com.plgame.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.plgame.engine.CraftOption
import com.plgame.engine.GameState

@Composable
fun CraftingScreen(
    state: GameState,
    onCraftSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC0A0A1A))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .background(Color(0xFF1A1A2E), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFAA00), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                "🔨 КРАФТ АМУЛЕТОВ",
                color = Color(0xFFFFAA00),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Токены крафта: ${state.craftTokens}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "⚠️ Результат рандомный. Можешь получить мусор. Как в Dota.",
                color = Color(0xFFFF6666),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(state.craftOptions) { index, option ->
                    CraftOptionCard(option, index) { onCraftSelect(index) }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Нажми вне окна чтобы отменить",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CraftOptionCard(option: CraftOption, index: Int, onClick: () -> Unit) {
    val rarityColor = option.result.rarity.color
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF222244))
            .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(rarityColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(option.result.icon, fontSize = 28.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                option.result.name,
                color = rarityColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                option.recipe.description,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Text(
                "Шанс крафта: ${(option.recipe.craftChance * 100).toInt()}%",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp
            )
            if (option.recipe.trollChance > 0f) {
                Text(
                    "⚠️ Шанс мусора: ${(option.recipe.trollChance * 100).toInt()}%",
                    color = Color(0xFFFF4444),
                    fontSize = 10.sp
                )
            }
        }

        // Index
        Text(
            "${index + 1}",
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
