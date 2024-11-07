package me.perny.hitman.classes.character.speak

import me.perny.hitman.classes.character.Character

fun makeConversationFromPool(pool: Pool, character: Character, target: Character): Conversation {
    val line = pools[pool]?.random() ?: throw IllegalArgumentException("Pool $pool is empty")

    return Conversation(
        character,
        target,
        line
    )
}