package me.perny.hitman.classes.debugger

import me.perny.hitman.classes.character.Character
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player

val subscribedCharacters: Map<Player, Character> = mutableMapOf()

fun Character.d(message: String) {
}

fun prefixMessage(message: String): Component {
    return Component.text("[DEBUG] $message")
}

fun sendCharacterDebugMessage(character: Character, message: Component) {
    subscribedCharacters.forEach { (player, subscribedCharacter) ->
        if (subscribedCharacter == character) {
            player.sendMessage(message)
        }
    }
}
