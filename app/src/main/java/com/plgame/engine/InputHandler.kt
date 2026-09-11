package com.plgame.engine

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.gameInput(
    onDirectionChange: (Vec2) -> Unit,
    onAttackToggle: (Boolean) -> Unit,
    onSkill: (Int) -> Unit,
    onCraftTap: () -> Unit
): Modifier = this
    .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            onDirectionChange(Vec2(dragAmount.x, dragAmount.y).normalize())
        }
    }
    .pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                onAttackToggle(true)
            },
            onDoubleTap = {
                onCraftTap()
            }
        )
    }
