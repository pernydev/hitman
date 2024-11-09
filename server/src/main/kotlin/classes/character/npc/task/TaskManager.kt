package me.perny.hitman.classes.character.npc.task

import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.debugger.d

class TaskManager(private var baseTask: Task? = null, private val character: NPCCharacter) {
    private val taskQueue = mutableListOf<Task>()
    var currentTask: Task? = null

    fun addTask(task: Task) {
        task.taskManager = this
        task.requirementTask.let { requirement ->
            if (requirement == null) return@let
            if (requirement.state != TaskState.COMPLETED) {
                addTask(requirement)
            }
        }

        if (task.priority == TaskPriority.BASE) {
            baseTask?.updateState(TaskState.INTERRUPTED)
            baseTask = task
            update()
            return
        }

        currentTask?.let { current ->
            if (current.shouldBeInterruptedBy(task)) {
                current.interrupt()

                if (current != baseTask) {
                    taskQueue.add(current)
                }
                currentTask = null
            }
        }

        if (taskQueue.firstOrNull()?.shouldBeInterruptedBy(task) == true) {
            taskQueue.add(0, task)
        } else {
            taskQueue.add(task)
        }

        update()
    }

    private fun startTask(task: Task) {
        currentTask?.interrupt()

        currentTask = task
        task.updateState(TaskState.ACTIVE)
        task.execute(character)
    }

    fun update() {

        if (currentTask?.state == TaskState.COMPLETED || currentTask?.state == TaskState.FAILED) {
            currentTask = null
        }

        if (currentTask == null) {
            val task = taskQueue.firstOrNull() ?: (baseTask ?: return)

            // Requirement checks
            if (task.requirementTask?.state == TaskState.FAILED) {
                // if the task failed and this check was not here, it would end up in an infinite loop
                taskQueue.removeFirstOrNull()
                update()
                return
            } else if ((task.requirementTask?.state ?: TaskState.COMPLETED) != TaskState.COMPLETED) {
                currentTask = task.requirementTask
                return
            }

            taskQueue.removeFirstOrNull()
            startTask(task)
        }

    }
}