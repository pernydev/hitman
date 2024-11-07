package me.perny.hitman.classes.game

import me.perny.hitman.classes.map.Map
import me.perny.hitman.classes.world.World

import kotlinx.coroutines.*
import me.perny.hitman.Global

class Mission(val world: World, val map: Map) {
    var hasStarted = false

    fun start() {
        runBlocking {
            Global.map = map
            world.load()
            println("World loaded. ${world.instanceContainer.chunks.size} chunks loaded.")

            map.loadNPCData(world)

            world.characters.forEach {
                launch {
                    it.spawn()
                }
            }
            hasStarted = true
        }
    }
}