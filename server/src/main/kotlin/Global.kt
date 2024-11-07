package me.perny.hitman

import me.perny.hitman.classes.map.Map
import net.minestom.server.MinecraftServer
import net.minestom.server.world.DimensionType

object Global {
    val FULL_BRIGHT_DIMENSION =
        MinecraftServer.getDimensionTypeRegistry().register("perny:mission", DimensionType.builder().ambientLight(2.0f).build())

    val serverMode: ServerMode = ServerMode.EDITOR
    var map: Map? = null
}

enum class ServerMode {
    EDITOR,
    PRODUCTION,
    TESTING,
}
