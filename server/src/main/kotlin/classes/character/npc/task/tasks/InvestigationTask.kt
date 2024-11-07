package me.perny.hitman.classes.character.npc.task.tasks

import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.character.signal.Signal
import me.perny.hitman.classes.character.npc.task.Task
import me.perny.hitman.classes.character.npc.task.TaskPriority

class InvestigationTask(
    priority: TaskPriority,
    signal: Signal
) :
    Task(
        priority = priority,
        requirementTask = DestinationTask(
            priority = priority,
            destination = signal.position,
            allowUnfinished = true
        )
    ) {
    override fun execute(npc: NPCCharacter) {
        TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}