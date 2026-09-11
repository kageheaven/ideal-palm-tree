package com.plgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.plgame.engine.*
import com.plgame.ui.GameRenderer
import com.plgame.ui.GameHUD
import com.plgame.screens.*
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private var gameLoop: GameLoop? = null
    private var gameLogic: GameLogic? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val logic = GameLogic(GameState())
        gameLogic = logic

        setContent {
            var gameState by remember { mutableStateOf(logic.getState(), neverEqualPolicy()) }

            // Game loop
            LaunchedEffect(Unit) {
                val loop = GameLoop(
                    onUpdate = { dt ->
                        logic.update(dt)
                        // Force recomposition
                        gameState = logic.getState()
                    },
                    onRender = {}
                )
                gameLoop = loop
                loop.start(scope)
            }

            // Game state display
            LaunchedEffect(gameState.phase) {
                // Recompose on phase changes
            }

            when (gameState.phase) {
                GamePhase.MENU -> {
                    MenuScreen(
                        onStartGame = {
                            logic.resetGame()
                            gameState = logic.getState()
                        }
                    )
                }

                GamePhase.PLAYING, GamePhase.PAUSED -> {
                    GameScreen(
                        state = gameState,
                        onSkillUse = { logic.useSkill(it); gameState = logic.getState() },
                        onAttackToggle = { logic.getState().isAttacking = it },
                        onCraftOpen = { logic.openCrafting(); gameState = logic.getState() },
                        onDirectionChange = { logic.getState().inputDirection = it }
                    )
                }

                GamePhase.CRAFTING -> {
                    GameScreen(
                        state = gameState,
                        onSkillUse = {},
                        onAttackToggle = {},
                        onCraftOpen = {},
                        onDirectionChange = {}
                    )
                    CraftingScreen(
                        state = gameState,
                        onCraftSelect = { logic.selectCraft(it); gameState = logic.getState() },
                        onDismiss = { gameState = logic.getState().copy(phase = GamePhase.PLAYING) }
                    )
                }

                GamePhase.GAME_OVER, GamePhase.VICTORY -> {
                    GameOverScreen(
                        state = gameState,
                        onRestart = {
                            logic.resetGame()
                            gameState = logic.getState()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameLoop?.stop()
        scope.cancel()
    }
}

@Composable
fun GameScreen(
    state: GameState,
    onSkillUse: (Int) -> Unit,
    onAttackToggle: (Boolean) -> Unit,
    onCraftOpen: () -> Unit,
    onDirectionChange: (Vec2) -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .gameInput(
                onDirectionChange = onDirectionChange,
                onAttackToggle = onAttackToggle,
                onSkill = onSkillUse,
                onCraftTap = onCraftOpen
            )
    ) {
        // Game canvas
        GameRenderer(state = state)

        // HUD overlay
        GameHUD(
            state = state,
            onSkillUse = onSkillUse,
            onAttackToggle = onAttackToggle,
            onCraftOpen = onCraftOpen
        )
    }
}
