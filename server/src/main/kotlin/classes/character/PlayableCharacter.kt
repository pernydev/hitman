package me.perny.hitman.classes.character

import classes.Item
import me.perny.hitman.classes.world.World
import me.perny.hitman.utils.Glyphs
import me.perny.hitman.utils.hideFromTab
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minestom.server.entity.Player
import java.time.Duration

class PlayableCharacter(world: World, val player: Player, uuid: String) : Character(world, uuid) {
    var inventory: List<Item> = listOf()
    var spawned = false

    fun speakTo(line: String, character: Character) {
        // TODO: Logic
    }

    override fun spawn() {
        println("Spawning player")
        try {
            player.showTitle(
                Title.title(
                    Component.text(Glyphs.BLACK),
                    Component.empty(),
                    Title.Times.times(
                        Duration.ofMillis(0),
                        Duration.ofMillis(300),
                        Duration.ofMillis(200)
                    )
                )
            )
            player.teleport(position)
            println(player.position)
            spawned = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun tick(tick: Byte) {}
    override fun nonblockingTick() {
        if (!spawned) return
        position = player.position
    }
}