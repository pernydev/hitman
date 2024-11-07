package me.perny.hitman.classes.character.signal

import me.perny.hitman.classes.world.World
import me.perny.hitman.classes.character.Character
import me.perny.hitman.classes.character.npc.NPCCharacter
import net.minestom.server.coordinate.Pos

class Signal(val position: Pos, val channel: SignalChannel, val strength: Int = 1, val world: World, val sender: Character? = null) {
    fun send() {
        world.characters.forEach {
            if (it == sender) return@forEach
            if (it !is NPCCharacter) return@forEach

            if (it.position.distance(position) < strength) {
                it.signal(this)
            }
        }
    }
}

enum class SignalChannel {
    INVESTIGATE,
    HELP,
    DANGER,
}
