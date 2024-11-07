package me.perny.hitman.utils

import me.perny.hitman.classes.character.Character
import me.perny.hitman.classes.world.findGround
import net.minestom.server.coordinate.Pos

fun Character.findNearbyRandomLocation(radius: Int): Pos {
    val x = position.x + (Math.random() * radius * 2 - radius).toInt()
    val y = position.y + (Math.random() * radius * 2 - radius).toInt()
    val z = position.z + (Math.random() * radius * 2 - radius).toInt()

    return Pos(x, y, z)
}