package me.perny.hitman.classes.character.npc.task.tasks

import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.character.speak.Conversation
import me.perny.hitman.classes.character.npc.task.Task
import me.perny.hitman.classes.character.npc.task.TaskPriority

class ConversationTask(priority: TaskPriority, private val conversation: Conversation) : Task(priority) {
    override fun execute(npc: NPCCharacter) {
        conversation.listenWhenOver { notifyCompleted() }
    }

    override fun onInterrupt() {
        conversation.end()
    }
}