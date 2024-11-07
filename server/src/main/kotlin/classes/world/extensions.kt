package me.perny.hitman.classes.world

import net.minestom.server.coordinate.Pos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun Pos.findGround(world: World): Pos? {
    var pos = this
    while (pos.y() > 0) {
        if (world.instanceContainer.getBlock(pos).isSolid) {
            return pos
        }
        pos = pos.sub(0.0, 1.0, 0.0)
    }
    return null
}

fun Area.randomPosition(): Pos {
    val minX = minOf(corner1.x(), corner2.x())
    val maxX = maxOf(corner1.x(), corner2.x())
    val minY = minOf(corner1.y(), corner2.y())
    val maxY = maxOf(corner1.y(), corner2.y())
    val minZ = minOf(corner1.z(), corner2.z())
    val maxZ = maxOf(corner1.z(), corner2.z())

    return Pos(
        Random.nextDouble(minX, maxX),
        Random.nextDouble(minY, maxY),
        Random.nextDouble(minZ, maxZ)
    )


}

fun Pos.addRelative(x: Double, y: Double, z: Double): Pos {
    // Convert yaw to radians (Minecraft uses degrees)
    val yawRad = Math.toRadians(this.yaw().toDouble())

    // Calculate rotated X and Z coordinates
    // For yaw 0: x goes to -x, z stays z
    // For yaw 90: x goes to -z, z goes to -x
    val rotatedX = x * cos(yawRad) - z * sin(yawRad)
    val rotatedZ = x * sin(yawRad) + z * cos(yawRad)

    return Pos(
        this.x() + rotatedX,
        this.y() + y,
        this.z() + rotatedZ,
        this.yaw(),
        this.pitch()
    )
}
