package me.perny.hitman.classes.character.npc.task.tasks

import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.character.speak.Conversation
import me.perny.hitman.classes.character.npc.task.Task
import me.perny.hitman.classes.character.npc.task.TaskPriority

class StartConversationTask (
    override val priority: TaskPriority,
    private val conversation: Conversation
) :
    Task(
        priority,
        requirementTask = DestinationTask(
            priority = priority,
            destination = conversation.target.position,
        )
    ) {
    override fun execute(npc: NPCCharacter) {
        conversation.listenWhenOver { notifyCompleted() }
    }

    override fun onInterrupt() {
        conversation.end() // End can be used even if the conversation has lines left
    }
}