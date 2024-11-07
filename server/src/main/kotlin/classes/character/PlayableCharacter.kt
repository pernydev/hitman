package me.perny.hitman.classes.character

import classes.Item
import me.perny.hitman.classes.world.World
import net.minestom.server.entity.Player

class PlayableCharacter(world: World, val player: Player) : Character(world) {
    var inventory: List<Item> = listOf()

    fun speakTo(line: String, character: Character) {
        // TODO: Logic
    }

    override fun spawn() {
        println("Spawning player")
        try {
            player.setInstance(world.instanceContainer, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun tick(tick: Byte) {}
    override fun nonblockingTick() {
        position = player.position
    }
}