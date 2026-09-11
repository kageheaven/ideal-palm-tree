package com.plgame.engine

import com.plgame.entities.*
import com.plgame.items.CraftingSystem
import com.plgame.items.GameItem
import kotlinx.coroutines.*

class GameLoop(
    private val onUpdate: (Float) -> Unit,
    private val onRender: (GameState) -> Unit
) {
    private var job: Job? = null
    private var lastTime: Long = 0L
    private var running = false

    fun start(scope: CoroutineScope) {
        if (running) return
        running = true
        lastTime = System.nanoTime()
        job = scope.launch(Dispatchers.Default) {
            while (running && isActive) {
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000.0).coerceAtMost(0.05).toFloat()
                lastTime = now
                onUpdate(dt)
                delay(1) // ~60fps cap, yield to other coroutines
            }
        }
    }

    fun stop() {
        running = false
        job?.cancel()
        job = null
    }
}

class GameLogic(private var state: GameState) {
    private val craftingSystem = CraftingSystem()

    fun getState(): GameState = state

    fun update(dt: Float) {
        if (state.phase != GamePhase.PLAYING) return

        state.gameTime += dt

        // Player movement
        updatePlayer(dt)

        // Camera follow
        updateCamera(dt)

        // Wave management
        updateWaves(dt)

        // Enemies AI
        updateEnemies(dt)

        // Projectiles
        updateProjectiles(dt)

        // Dropped items
        updateDroppedItems(dt)

        // Collision detection
        checkCollisions()

        // Cleanup
        state.removeDeadEnemies()
        state.removeDeadProjectiles()

        // Death check
        if (state.player.hp <= 0) {
            state.phase = GamePhase.GAME_OVER
            state.addNotification("Фантом Лансер пал в бою!", 0xFFFF4444)
        }

        // Victory check
        if (state.currentWave > state.maxWaves && state.enemies.isEmpty()) {
            state.phase = GamePhase.VICTORY
            state.addNotification("ПОБЕДА! Лансер выжил!", 0xFF44FF44)
        }

        // Notifications cleanup
        state.notifications.removeAll { it.alpha(state.gameTime) <= 0f }
    }

    private fun updatePlayer(dt: Float) {
        val player = state.player
        val dir = state.inputDirection.normalize()

        if (dir.length() > 0.01f) {
            val speed = player.getMovementSpeed()
            val newPos = player.position + dir * speed * dt
            player.position = Vec2(
                newPos.x.coerceIn(32f, state.worldSize.x - 32f),
                newPos.y.coerceIn(32f, state.worldSize.y - 32f)
            )
            player.facingAngle = dir.angle()
            player.isMoving = true
        } else {
            player.isMoving = false
        }

        // Mana regen
        player.mana = (player.mana + player.manaRegen * dt).coerceAtMost(player.maxMana)

        // Skill cooldowns
        player.skillCooldowns.forEachIndexed { i, cd ->
            if (cd > 0f) player.skillCooldowns[i] = (cd - dt).coerceAtLeast(0f)
        }

        // Illusion update
        player.illusions.removeAll { it.lifetime <= 0f }
        player.illusions.forEach { illusion ->
            illusion.lifetime -= dt
            // Illusions chase nearest enemy
            val nearest = state.enemies.filter { it.isAlive() }.minByOrNull { it.position.distanceTo(illusion.position) }
            if (nearest != null) {
                val toEnemy = (nearest.position - illusion.position).normalize()
                illusion.position = illusion.position + toEnemy * player.illusionSpeed * dt
                illusion.facingAngle = toEnemy.angle()
            }
            // Illusions deal reduced damage
            if (nearest != null && nearest.position.distanceTo(illusion.position) < 50f) {
                nearest.takeDamage(player.illusionDamage * dt)
            }
        }

        // Attack
        if (state.isAttacking) {
            tryAttack()
        }
    }

    private fun tryAttack() {
        val player = state.player
        val attackSpeed = player.getAttackSpeed()
        if (player.attackCooldown > 0f) return

        val nearest = state.enemies
            .filter { it.isAlive() && it.position.distanceTo(player.position) < player.attackRange }
            .minByOrNull { it.position.distanceTo(player.position) }

        if (nearest != null) {
            nearest.takeDamage(player.damage)
            player.attackCooldown = 1f / attackSpeed
            state.projectiles.add(
                Projectile(
                    position = player.position,
                    velocity = (nearest.position - player.position).normalize() * 600f,
                    damage = 0f, // Direct damage already applied
                    lifetime = 0.3f,
                    color = 0xFF00FFFF,
                    size = 6f,
                    isVisual = true
                )
            )
        }
    }

    private fun updateCamera(dt: Float) {
        val target = state.player.position
        state.camera = state.camera.lerp(target, 5f * dt)
    }

    private fun updateWaves(dt: Float) {
        if (!state.isWaveActive) {
            state.waveTimer += dt
            if (state.waveTimer >= state.waveCooldown) {
                state.waveTimer = 0f
                spawnWave()
            }
        } else if (state.enemies.all { !it.isAlive() }) {
            state.isWaveActive = false
            state.currentWave++
            state.craftTokens++
            state.addNotification("Волна $state.currentWave пройдена! +1 токен крафта", 0xFF44FF44)
            if (state.currentWave <= state.maxWaves) {
                state.addNotification("Следующая волна через ${state.waveCooldown.toInt()}с", 0xFFAAAAFF)
            }
        }
    }

    private fun spawnWave() {
        state.isWaveActive = true
        val wave = state.currentWave
        val creepCount = 3 + wave * 2
        val isBoss = wave % 5 == 0 && wave > 0

        repeat(creepCount) { i ->
            val angle = (i.toFloat() / creepCount) * Math.PI.toFloat() * 2f
            val dist = 300f + (Math.random() * 200f).toFloat()
            val spawnPos = state.player.position + Vec2.fromAngle(angle, dist)
            val clampedPos = Vec2(
                spawnPos.x.coerceIn(50f, state.worldSize.x - 50f),
                spawnPos.y.coerceIn(50f, state.worldSize.y - 50f)
            )

            val creepType = if (isBoss && i == 0) {
                com.plgame.entities.CreepType.BOSS
            } else {
                com.plgame.entities.CreepType.values().filter { it != com.plgame.entities.CreepType.BOSS }
                    .random()
            }
            state.enemies.add(Enemy.create(creepType, clampedPos, wave))
        }

        state.addNotification("Волна $wave: $creepCount крипов${if (isBoss) " (БОСС!)" else ""}", 0xFFFF8844)
    }

    private fun updateEnemies(dt: Float) {
        state.enemies.filter { it.isAlive() }.forEach { enemy ->
            enemy.update(dt, state.player, state.enemies)
        }
    }

    private fun updateProjectiles(dt: Float) {
        state.projectiles.forEach { proj ->
            proj.position = proj.position + proj.velocity * dt
            proj.lifetime -= dt
        }
    }

    private fun updateDroppedItems(dt: Float) {
        state.items.forEach { it.lifetime -= dt }
        state.items.removeAll { it.isExpired() }

        // Player pickup
        val pickupRange = 60f
        val toPickup = state.items.filter {
            it.position.distanceTo(state.player.position) < pickupRange
        }
        toPickup.forEach { dropped ->
            when (dropped.item) {
                is GameItem.GoldCoin -> {
                    state.gold += dropped.item.amount
                    state.addNotification("+${dropped.item.amount} золота", 0xFFFFD700)
                }
                is GameItem.HealthPotion -> {
                    state.player.hp = (state.player.hp + dropped.item.healAmount).coerceAtMost(state.player.maxHp)
                    state.addNotification("+${dropped.item.healAmount} HP", 0xFF44FF44)
                }
                is GameItem.ManaPotion -> {
                    state.player.mana = (state.player.mana + dropped.item.manaAmount).coerceAtMost(state.player.maxMana)
                    state.addNotification("+${dropped.item.manaAmount} MP", 0xFF4444FF)
                }
                else -> {}
            }
        }
        state.items.removeAll { it in toPickup }
    }

    private fun checkCollisions() {
        val player = state.player

        // Projectiles vs enemies
        state.projectiles.filter { !it.isVisual && it.lifetime > 0f }.forEach { proj ->
            state.enemies.filter { it.isAlive() }.forEach { enemy ->
                if (proj.position.distanceTo(enemy.position) < enemy.hitRadius + proj.size) {
                    enemy.takeDamage(proj.damage)
                    proj.lifetime = 0f
                    if (!enemy.isAlive()) {
                        onEnemyKilled(enemy)
                    }
                }
            }
        }

        // Enemy melee vs player
        state.enemies.filter { it.isAlive() && it.attackType == AttackType.MELEE }.forEach { enemy ->
            if (enemy.position.distanceTo(player.position) < 40f && enemy.attackCooldown <= 0f) {
                player.takeDamage(enemy.damage)
                enemy.attackCooldown = 1f / enemy.attackSpeed
                state.addNotification("-${enemy.damage.toInt()} HP", 0xFFFF4444)
            }
        }

        // Ranged enemy attacks
        state.enemies.filter { it.isAlive() && it.attackType == AttackType.RANGED }.forEach { enemy ->
            if (enemy.attackCooldown <= 0f) {
                val dist = enemy.position.distanceTo(player.position)
                if (dist < enemy.attackRange) {
                    val dir = (player.position - enemy.position).normalize()
                    state.projectiles.add(
                        Projectile(
                            position = enemy.position,
                            velocity = dir * 350f,
                            damage = enemy.damage,
                            lifetime = 2f,
                            color = enemy.projectileColor,
                            size = 5f
                        )
                    )
                    enemy.attackCooldown = 1f / enemy.attackSpeed
                }
            }
        }

        // Player attack auto-check
        if (state.isAttacking && player.attackCooldown <= 0f) {
            val nearest = state.enemies
                .filter { it.isAlive() && it.position.distanceTo(player.position) < player.attackRange }
                .minByOrNull { it.position.distanceTo(player.position) }
            if (nearest != null) {
                nearest.takeDamage(player.damage)
                player.attackCooldown = 1f / player.getAttackSpeed()
            }
        }
    }

    private fun onEnemyKilled(enemy: Enemy) {
        state.score += enemy.scoreValue
        state.gold += enemy.goldDrop

        // Drop items
        val dropChance = 0.3f + (state.currentWave * 0.02f)
        if (Math.random() < dropChance) {
            val item = craftingSystem.randomDrop(state.currentWave)
            state.items.add(DroppedItem(enemy.position, item))
        }

        // Drop gold coins
        if (Math.random() < 0.5f) {
            state.items.add(DroppedItem(
                enemy.position + Vec2((Math.random() * 20 - 10).toFloat(), (Math.random() * 20 - 10).toFloat()),
                GameItem.GoldCoin(5 + state.currentWave * 2)
            ))
        }

        state.addNotification("${enemy.creepType.displayName} убит! +${enemy.scoreValue}", 0xFFFFFF44)
    }

    fun openCrafting() {
        if (state.craftTokens <= 0) {
            state.addNotification("Нет токенов крафта!", 0xFFFF4444)
            return
        }
        state.craftOptions = craftingSystem.generateOptions(3, state)
        state.phase = GamePhase.CRAFTING
    }

    fun selectCraft(optionIndex: Int) {
        if (optionIndex !in state.craftOptions.indices) return
        val option = state.craftOptions[optionIndex]
        craftingSystem.applyCraft(option, state)
        state.craftTokens--
        state.phase = GamePhase.PLAYING
        state.addNotification("Скрафчено: ${option.result.name}!", 0xFFFFAA00)
    }

    fun useSkill(skillIndex: Int) {
        val player = state.player
        if (skillIndex !in player.skills.indices) return
        val skill = player.skills[skillIndex]
        if (player.skillCooldowns[skillIndex] > 0f) return
        if (player.mana < skill.manaCost) {
            state.addNotification("Не хватает маны!", 0xFF4444FF)
            return
        }

        player.mana -= skill.manaCost
        player.skillCooldowns[skillIndex] = skill.cooldown

        when (skill.id) {
            "spirit_lance" -> spiritLance(skill)
            "doppelganger" -> doppelganger(skill)
            "phopri" -> phantomStrike(skill)
            "juxtapose" -> juxtapose(skill)
        }
    }

    private fun spiritLance(skill: Skill) {
        val dir = Vec2.fromAngle(state.player.facingAngle)
        val target = state.player.position + dir * skill.range
        val enemy = state.enemies.filter { it.isAlive() }
            .minByOrNull { it.position.distanceTo(target) }

        if (enemy != null && enemy.position.distanceTo(target) < 80f) {
            enemy.takeDamage(skill.damage)
            enemy.slow(2f, 0.5f)
            state.projectiles.add(
                Projectile(
                    position = state.player.position,
                    velocity = dir * 800f,
                    damage = 0f,
                    lifetime = 0.2f,
                    color = 0xFF00FFFF,
                    size = 8f,
                    isVisual = true
                )
            )
            state.addNotification("Spirit Lance! -${skill.damage.toInt()}", 0xFF00FFFF)
        }
    }

    private fun doppelganger(skill: Skill) {
        val player = state.player
        // Create illusions
        repeat(skill.illusions) { i ->
            val angle = (i.toFloat() / skill.illusions) * Math.PI.toFloat() * 2f
            val offset = Vec2.fromAngle(angle, 80f)
            player.illusions.add(
                Illusion(
                    position = player.position + offset,
                    facingAngle = angle,
                    damage = player.damage * 0.3f,
                    hp = player.maxHp * 0.3f,
                    lifetime = skill.duration
                )
            )
        }
        // Short invulnerability
        player.invulnTimer = 0.5f
        state.addNotification("Doppelganger! ${skill.illusions} иллюзий!", 0xFFAA44FF)
    }

    private fun phantomStrike(skill: Skill) {
        val player = state.player
        val nearest = state.enemies.filter { it.isAlive() }
            .minByOrNull { it.position.distanceTo(player.position) }

        if (nearest != null && nearest.position.distanceTo(player.position) < skill.range) {
            player.position = nearest.position - Vec2.fromAngle(player.facingAngle, 30f)
            nearest.takeDamage(skill.damage)
            player.attackSpeedBonus = skill.attackSpeedBonus
            state.addNotification("Phantom Strike! +${skill.attackSpeedBonus}% AS", 0xFFFF4400)
        }
    }

    private fun juxtapose(skill: Skill) {
        val player = state.player
        // Mass illusion spawn
        repeat(skill.illusions) { i ->
            val angle = (Math.random() * Math.PI * 2).toFloat()
            val dist = (Math.random() * 100f).toFloat()
            player.illusions.add(
                Illusion(
                    position = player.position + Vec2.fromAngle(angle, dist),
                    facingAngle = angle,
                    damage = player.damage * 0.25f,
                    hp = player.maxHp * 0.25f,
                    lifetime = skill.duration
                )
            )
        }
        state.addNotification("JUXTAPOSE! ${skill.illusions} иллюзий!", 0xFFFF44FF)
    }

    fun resetGame() {
        state = GameState()
        craftingSystem.reset()
    }
}
