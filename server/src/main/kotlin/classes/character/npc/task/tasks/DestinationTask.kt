package me.perny.hitman.classes.character.npc.task.tasks

import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.character.npc.pathfinding.pathfindTo
import me.perny.hitman.classes.character.npc.task.Task
import me.perny.hitman.classes.character.npc.task.TaskPriority
import me.perny.hitman.classes.debugger.d
import net.minestom.server.coordinate.Pos
import kotlin.concurrent.thread

class DestinationTask(
    override val priority: TaskPriority = TaskPriority.LOW,
    private val destination: Pos,
    private val allowUnfinished: Boolean = false,
    private val run: Boolean = false
) : Task(priority) {

    var isUnfinished: Boolean = false

    override fun execute(npc: NPCCharacter) {
        thread {
            val t1 = System.currentTimeMillis()
            val path = npc.pathfindTo(destination)
            val t2 = System.currentTimeMillis()
            println("Pathfinding took ${t2 - t1}ms")
            if (isPointDestination(npc.position)) {
                notifyCompleted()
                return@thread
            }
            if (!isPointDestination(path.lastOrNull())) {
                // Pathfinding was unable to find a path to the destination and one is required
                if (!allowUnfinished) {
                    notifyFailed()
                    return@thread
                }
                isUnfinished = true
            }

            npc.npcrenderer?.playPath(path) {
                npc.d("DestinationTask finished")
                notifyCompleted()
            }
        }
    }

    private fun isPointDestination(point: Pos?): Boolean {
        val distanceFromEnd = point?.distance(destination) ?: 9999.0
        if (distanceFromEnd < 1.5) {
            return true
        }
        return false
    }

    override fun onInterrupt() {
    }
}