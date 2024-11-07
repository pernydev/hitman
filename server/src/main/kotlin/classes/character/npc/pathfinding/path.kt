package me.perny.hitman.classes.character.npc.pathfinding

import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.pathfinding.PathGenerator
import net.minestom.server.entity.pathfinding.generators.GroundNodeGenerator
import net.minestom.server.instance.InstanceContainer
import com.mattworzala.debug.DebugMessage
import com.mattworzala.debug.shape.LineShape
import com.mattworzala.debug.shape.Shape
import me.perny.hitman.classes.character.Character

fun findPath(start: Pos, end: Pos, instanceContainer: InstanceContainer): List<Pos> {
    val boundingBox = BoundingBox(
        Vec(0.0, 0.0, 0.0),
        Vec(.01, 2.0, .01)
    )

    println("making walkable")

    val path = PathGenerator.generate(
        BlockGetter(instanceContainer),
        start,
        end,
        1.0,
        100990.0,
        1000.0,
        boundingBox,
        true,
        GroundNodeGenerator(),
        Runnable { }
    )

    println("path generated")

    val builder = DebugMessage.builder();

    if (path.nodes.size > 2) {
        val line = Shape.line()
            .type(LineShape.Type.STRIP)
            .color(0xFF00FF00.toInt())
            .lineWidth(4f)


        path.nodes.forEach {
            if (it.x() == 0.0 && it.y() == 0.0 && it.z() == 0.0) return@forEach
            line.point(Pos(it.x(), it.y(), it.z()))
        }

        val packet = builder.set(
            "debug:test_line", line.build()
        ).build().packet

        instanceContainer.players.forEach {
            it.sendPacket(packet)
        }
    }

    return path.nodes.map { Pos(it.x(), it.y(), it.z()) }
}

fun Character.canGetTo(pos: Pos): Boolean {
    val path = findPath(this.position, pos, this.world.instanceContainer)
    if (path.isEmpty()) return false
    if (path.last().distance(pos) > 1.5) return false
    return true
}

fun Character.canWalkTo(pos: Pos): Boolean {
    return GroundNodeGenerator().canMoveTowards(
        BlockGetter(this.world.instanceContainer),
        this.position,
        pos,
        BoundingBox(Vec(0.0, 0.0, 0.0), Vec(.01, 2.0, .01))
    )
}

fun Character.pathfindTo(end: Pos): List<Pos> {
    return findPath(this.position, end, this.world.instanceContainer)
}