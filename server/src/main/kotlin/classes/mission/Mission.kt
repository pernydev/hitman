package me.perny.hitman.classes.game

import me.perny.hitman.classes.map.Map
import me.perny.hitman.classes.world.World

import kotlinx.coroutines.*
import me.perny.hitman.Global
import me.perny.hitman.classes.character.PlayableCharacter
import me.perny.hitman.classes.character.npc.NPCCharacter
import me.perny.hitman.classes.debugger.d
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.timer.TaskSchedule

class Mission(val world: World, val map: Map) {
    var hasStarted = false

    fun start() {
        runBlocking {
            world.characters.forEach {
                launch {
                    d("Spawning ${it::class.simpleName}", "Mission")
                    it.spawn()
                }
            }
            MinecraftServer.getSchedulerManager().submitTask {
                world.tick(0)
                return@submitTask TaskSchedule.tick(1)
            }
            hasStarted = true
            d("Mission started", "Mission")
        }
    }

    fun prep() {
        runBlocking {
            Global.map = map
            world.load()
            d("Loaded world", "Mission")

            map.loadNPCData(world)
            d("Loaded NPC data", "Mission")
        }
    }

    fun destroy() {
        runBlocking {
            world.characters.forEach {
                launch {
                    d("Despawning ${it::class.simpleName}", "Mission")
                    if (it is PlayableCharacter) {
                        it.player.kick("Mission ended")
                        return@launch
                    }

                    val npc = it as NPCCharacter
                    npc.npcrenderer?.remove()
                }
            }

            world.instanceContainer.chunks.forEach {
                world.instanceContainer.unloadChunk(it)
            }
            hasStarted = false
            d("Mission destroyed", "Mission")
        }
    }

    fun removePlayer(player: Player) {
        val playableCharacter = world.playableCharacters.find { it.player == player } ?: return
        world.playableCharacters.remove(playableCharacter)
        world.characters.remove(playableCharacter)
        if (world.playableCharacters.isEmpty()) {
            destroy()
        }
    }
}