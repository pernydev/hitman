package me.perny.hitman.classes.character.npc.task

import me.perny.hitman.classes.character.npc.NPCCharacter

abstract class Task(open val priority: TaskPriority, val requirementTask: Task? = null) {
    var state: TaskState = TaskState.QUEUED
    var taskManager: TaskManager? = null

    fun shouldBeInterruptedBy(task: Task): Boolean {
        return shouldBeInterruptedBy(task.priority)
    }

    fun shouldBeInterruptedBy(newTaskPriority: TaskPriority): Boolean {
        return when (priority) {
            TaskPriority.BASE -> false
            TaskPriority.LOW -> newTaskPriority > TaskPriority.LOW
            TaskPriority.MEDIUM -> newTaskPriority > TaskPriority.MEDIUM
            TaskPriority.HIGH -> newTaskPriority > TaskPriority.HIGH
            TaskPriority.VERY_HIGH -> newTaskPriority > TaskPriority.VERY_HIGH
            TaskPriority.EMERGENCY -> true
        }
    }

    abstract fun execute(npc: NPCCharacter)
    abstract fun onInterrupt()

    fun interrupt() {
        updateState(TaskState.INTERRUPTING)
        onInterrupt()
        updateState(TaskState.INTERRUPTED)
    }

    fun updateState(newState: TaskState) {
        state = newState
    }

    protected fun notifyFailed() {
        if (state == TaskState.INTERRUPTING) return
        updateState(TaskState.FAILED)
        taskManager?.update()
    }

    protected fun notifyCompleted() {
        if (state == TaskState.INTERRUPTING) return
        updateState(TaskState.COMPLETED)
        taskManager?.update()
    }
}



enum class TaskState {
    QUEUED,
    ACTIVE,
    INTERRUPTING,
    INTERRUPTED,
    COMPLETED,
    FAILED,
}

enum class TaskPriority {
    BASE,
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH,
    EMERGENCY,
}
