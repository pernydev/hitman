package me.perny.hitman.classes.character

import classes.Item
import me.perny.hitman.classes.Clothing
import me.perny.hitman.classes.Skin
import me.perny.hitman.classes.world.World
import net.minestom.server.coordinate.Pos

abstract class Character(val world: World, val uuid: String, var glowColor: GlowColor? = null) {
    var health: Int = 100
    var heldItem: Item? = null

    var skin: Skin = Skin(Clothing.SUIT, false, 1)

    var wakenessState: State = State.ALIVE
    var position: Pos = Pos(0.0, -60.0, 0.0)
    var rotation: Float = 0.0f


    var isSneaking: Boolean = false
    var isSprinting: Boolean = false

    abstract fun spawn()
    abstract fun tick(tick: Byte)
    abstract fun nonblockingTick()
}

enum class State {
    ALIVE,
    STUNNED,
    POISONED,
    KNOCKED_OUT,
    DEAD,
}

enum class GlowColor {
    NPC,
    GUARD,
    PLAYER,
}