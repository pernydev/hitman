package me.perny.hitman

import me.perny.hitman.classes.game.Mission
import me.perny.hitman.classes.map.Map
import me.perny.hitman.classes.world.World
import net.minestom.server.MinecraftServer
import net.minestom.server.world.DimensionType

object Global {
    val FULL_BRIGHT_DIMENSION =
        MinecraftServer.getDimensionTypeRegistry().register("perny:mission", DimensionType.builder().ambientLight(2.0f).build())

    val serverMode: ServerMode = ServerMode.TESTING
    var mission: Mission? = null
    var map: Map? = null
    var world: World? = null
}

enum class ServerMode {
    EDITOR,
    PRODUCTION,
    TESTING,
}