package me.perny.hitman.classes.character.npc

import me.perny.hitman.classes.character.GlowColor
import me.perny.hitman.classes.world.World
import me.perny.hitman.classes.character.signal.Signal
import me.perny.hitman.classes.character.npc.task.TaskPriority
import me.perny.hitman.classes.character.npc.task.tasks.InvestigationTask

open class AINPCCharacter(world: World, uuid: String, glowColor: GlowColor = GlowColor.NPC) : NPCCharacter(world, uuid, glowColor) {
    open var state: AIState = AIState.IDLE

    override fun tick(tick: Byte) {}

    open fun investigate(signal: Signal, priority: TaskPriority = TaskPriority.MEDIUM) {
        updateState(AIState.INVESTIGATING)
        taskManager.addTask(InvestigationTask(priority, signal))
    }

    private fun updateState(newState: AIState) {
        state = newState
    }
}

enum class AIState {
    IDLE,
    PANIC,
    IN_SHELTER,
    INVESTIGATING,
    REPORTING,
    AGRESSIVELY_INVESTIGATING,
    ALERT,
    ATTACKING,
}
