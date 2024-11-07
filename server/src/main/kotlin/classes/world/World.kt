package me.perny.hitman.classes.world

import classes.Item
import kotlinx.coroutines.runBlocking
import me.perny.hitman.classes.character.Character
import me.perny.hitman.classes.character.PlayableCharacter
import me.perny.hitman.classes.map.Map
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import kotlin.concurrent.thread

class World(val map: Map) {
    val characters: MutableList<Character> = mutableListOf()
    val instanceContainer: InstanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer()
    val playableCharacters: MutableList<PlayableCharacter> = mutableListOf()

    val items: MutableMap<Pos, Item> = mutableMapOf()
    val areas: MutableList<Area> = mutableListOf()

    fun load() {
        runBlocking {
            instanceContainer.chunkLoader = AnvilLoader("maps/${map.id}/world")
            instanceContainer.enableAutoChunkLoad(false)
            val chunkLoadAmount = 16
            for (x in -chunkLoadAmount..chunkLoadAmount) {
                for (z in -chunkLoadAmount..chunkLoadAmount) {
                    try {
                        instanceContainer.loadChunk(x, z).join()
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
        }
    }

    fun addPlayableCharacter(character: PlayableCharacter) {
        playableCharacters.add(character)
        characters.add(character)
    }

    fun addCharacter(character: Character) {
        characters.add(character)
    }

    fun findNearestArea(character: Character, type: AreaType): Area? {
        return areas.filter { it.type == type }.minByOrNull { it.center().distance(character.position) }
    }

    fun tick(tick: Byte) {
        characters.forEach { it.tick(tick) } // Call the tick function on each character
        thread {
            characters.forEach { thread { it.nonblockingTick() } } // Call the nonblockingTick function on each character
        }
    }
}