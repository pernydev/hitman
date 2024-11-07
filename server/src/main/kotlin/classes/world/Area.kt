package me.perny.hitman.classes.world

import net.minestom.server.coordinate.Pos

class Area(val corner1: Pos, val corner2: Pos, val type: AreaType) {
    fun center(): Pos {
        return Pos(
            (corner1.x() + corner2.x()) / 2,
            (corner1.y() + corner2.y()) / 2,
            (corner1.z() + corner2.z()) / 2,
        )
    }
}

enum class AreaType {
    SHELTER,
    CLOSET,
}