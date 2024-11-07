package me.perny.hitman.classes.character.speak

import me.perny.hitman.classes.character.Character

class Conversation(val starter: Character, val target: Character, val scriptId: String) {
    var currentLine = 0
    var isOver = false
    private val whenOverListeners = mutableListOf<() -> Unit>()

    fun listenWhenOver(callback: () -> Unit) {
        whenOverListeners.add(callback)
    }

    fun nextLine() {
        currentLine++
        if (currentLine >= 3) {
            end()
        }
    }

    fun end() {
        isOver = true
        whenOverListeners.forEach { it() }
    }
}